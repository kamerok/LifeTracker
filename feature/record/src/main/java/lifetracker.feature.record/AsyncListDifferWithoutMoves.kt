/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lifetracker.feature.record

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.DiffUtil.DiffResult
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executor

/**
 * Helper for computing the difference between two lists via [DiffUtil] on a background
 * thread.
 *
 *
 * It can be connected to a
 * [RecyclerView.Adapter], and will signal the
 * adapter of changes between sumbitted lists.
 *
 *
 * For simplicity, the [ListAdapter] wrapper class can often be used instead of the
 * AsyncListDiffer directly. This AsyncListDiffer can be used for complex cases, where overriding an
 * adapter base class to support asynchronous List diffing isn't convenient.
 *
 *
 * The AsyncListDiffer can consume the values from a LiveData of `List` and present the
 * data simply for an adapter. It computes differences in list contents via [DiffUtil] on a
 * background thread as new `List`s are received.
 *
 *
 * Use [.getCurrentList] to access the current List, and present its data objects. Diff
 * results will be dispatched to the ListUpdateCallback immediately before the current list is
 * updated. If you're dispatching list updates directly to an Adapter, this means the Adapter can
 * safely access list items and total size via [.getCurrentList].
 *
 *
 * A complete usage pattern with Room would look like this:
 * <pre>
 * @Dao
 * interface UserDao {
 * @Query("SELECT * FROM user ORDER BY lastName ASC")
 * public abstract LiveData&lt;List&lt;User>> usersByLastName();
 * }
 *
 * class MyViewModel extends ViewModel {
 * public final LiveData&lt;List&lt;User>> usersList;
 * public MyViewModel(UserDao userDao) {
 * usersList = userDao.usersByLastName();
 * }
 * }
 *
 * class MyActivity extends AppCompatActivity {
 * @Override
 * public void onCreate(Bundle savedState) {
 * super.onCreate(savedState);
 * MyViewModel viewModel = new ViewModelProvider(this).get(MyViewModel.class);
 * RecyclerView recyclerView = findViewById(R.id.user_list);
 * UserAdapter adapter = new UserAdapter();
 * viewModel.usersList.observe(this, list -> adapter.submitList(list));
 * recyclerView.setAdapter(adapter);
 * }
 * }
 *
 * class UserAdapter extends RecyclerView.Adapter&lt;UserViewHolder> {
 * private final AsyncListDiffer&lt;User> mDiffer = new AsyncListDiffer(this, DIFF_CALLBACK);
 * @Override
 * public int getItemCount() {
 * return mDiffer.getCurrentList().size();
 * }
 * public void submitList(List&lt;User> list) {
 * mDiffer.submitList(list);
 * }
 * @Override
 * public void onBindViewHolder(UserViewHolder holder, int position) {
 * User user = mDiffer.getCurrentList().get(position);
 * holder.bindTo(user);
 * }
 * public static final DiffUtil.ItemCallback&lt;User> DIFF_CALLBACK
 * = new DiffUtil.ItemCallback&lt;User>() {
 * @Override
 * public boolean areItemsTheSame(
 * @NonNull User oldUser, @NonNull User newUser) {
 * // User properties may have changed if reloaded from the DB, but ID is fixed
 * return oldUser.getId() == newUser.getId();
 * }
 * @Override
 * public boolean areContentsTheSame(
 * @NonNull User oldUser, @NonNull User newUser) {
 * // NOTE: if you use equals, your object must properly override Object#equals()
 * // Incorrectly returning false here will result in too many animations.
 * return oldUser.equals(newUser);
 * }
 * }
 * }</pre>
 *
 * @param <T> Type of the lists this AsyncListDiffer will receive.
 *
 * @see DiffUtil
 *
 * @see AdapterListUpdateCallback
</T> */
class AsyncListDifferWithoutMoves<T>(
    private val mUpdateCallback: ListUpdateCallback,
    /* synthetic access */val mConfig: AsyncDifferConfig<T>
) {
    var mMainThreadExecutor: Executor

    private class MainThreadExecutor internal constructor() : Executor {
        val mHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mHandler.post(command)
        }
    }

    /**
     * Listener for when the current List is updated.
     *
     * @param <T> Type of items in List
    </T> */
    interface ListListener<T> {
        /**
         * Called after the current List has been updated.
         *
         * @param previousList The previous list.
         * @param currentList The new current list.
         */
        fun onCurrentListChanged(
            previousList: List<T>,
            currentList: List<T>
        )
    }

    private val mListeners: MutableList<ListListener<T>> =
        CopyOnWriteArrayList()

    /**
     * Convenience for
     * `AsyncListDiffer(new AdapterListUpdateCallback(adapter),
     * new AsyncDifferConfig.Builder().setDiffCallback(diffCallback).build());`
     *
     * @param adapter Adapter to dispatch position updates to.
     * @param diffCallback ItemCallback that compares items to dispatch appropriate animations when
     *
     * @see DiffUtil.DiffResult.dispatchUpdatesTo
     */
    constructor(
        adapter: RecyclerView.Adapter<*>,
        diffCallback: DiffUtil.ItemCallback<T>
    ) : this(
        AdapterListUpdateCallback(adapter),
        AsyncDifferConfig.Builder<T>(diffCallback).build()
    ) {
    }

    private var mList: List<T>? = null

    /**
     * Get the current List - any diffing to present this list has already been computed and
     * dispatched via the ListUpdateCallback.
     *
     *
     * If a `null` List, or no List has been submitted, an empty list will be returned.
     *
     *
     * The returned list may not be mutated - mutations to content must be done through
     * [.submitList].
     *
     * @return current List.
     */
    /**
     * Non-null, unmodifiable version of mList.
     *
     *
     * Collections.emptyList when mList is null, wrapped by Collections.unmodifiableList otherwise
     */
    var currentList: List<T> = emptyList()
        private set

    // Max generation of currently scheduled runnable
    var mMaxScheduledGeneration = 0

    /**
     * Pass a new List to the AdapterHelper. Adapter updates will be computed on a background
     * thread.
     *
     *
     * If a List is already present, a diff will be computed asynchronously on a background thread.
     * When the diff is computed, it will be applied (dispatched to the [ListUpdateCallback]),
     * and the new List will be swapped in.
     *
     *
     * The commit callback can be used to know when the List is committed, but note that it
     * may not be executed. If List B is submitted immediately after List A, and is
     * committed directly, the callback associated with List A will not be run.
     *
     * @param newList The new List.
     * @param commitCallback Optional runnable that is executed when the List is committed, if
     * it is committed.
     */
    /**
     * Pass a new List to the AdapterHelper. Adapter updates will be computed on a background
     * thread.
     *
     *
     * If a List is already present, a diff will be computed asynchronously on a background thread.
     * When the diff is computed, it will be applied (dispatched to the [ListUpdateCallback]),
     * and the new List will be swapped in.
     *
     * @param newList The new List.
     */
    @JvmOverloads
    fun submitList(
        newList: List<T>?,
        commitCallback: Runnable? = null
    ) {
        // incrementing generation means any currently-running diffs are discarded when they finish
        val runGeneration = ++mMaxScheduledGeneration
        if (newList === mList) {
            // nothing to do (Note - still had to inc generation, since may have ongoing work)
            commitCallback?.run()
            return
        }
        val previousList = currentList

        // fast simple remove all
        if (newList == null) {
            val countRemoved = mList!!.size
            mList = null
            currentList = emptyList()
            // notify last, after list is updated
            mUpdateCallback.onRemoved(0, countRemoved)
            onCurrentListChanged(previousList, commitCallback)
            return
        }

        // fast simple first insert
        if (mList == null) {
            mList = newList
            currentList = Collections.unmodifiableList(newList)
            // notify last, after list is updated
            mUpdateCallback.onInserted(0, newList.size)
            onCurrentListChanged(previousList, commitCallback)
            return
        }
        val oldList: List<T> = mList!!
        mConfig.backgroundThreadExecutor.execute {
            val result =
                DiffUtil.calculateDiff(object :
                    DiffUtil.Callback() {
                    override fun getOldListSize(): Int {
                        return oldList.size
                    }

                    override fun getNewListSize(): Int {
                        return newList.size
                    }

                    override fun areItemsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean {
                        val oldItem: T? = oldList[oldItemPosition]
                        val newItem: T? = newList[newItemPosition]
                        return if (oldItem != null && newItem != null) {
                            mConfig.diffCallback.areItemsTheSame(oldItem, newItem)
                        } else oldItem == null && newItem == null
                        // If both items are null we consider them the same.
                    }

                    override fun areContentsTheSame(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Boolean {
                        val oldItem: T? = oldList[oldItemPosition]
                        val newItem: T? = newList[newItemPosition]
                        if (oldItem != null && newItem != null) {
                            return mConfig.diffCallback
                                .areContentsTheSame(oldItem, newItem)
                        }
                        if (oldItem == null && newItem == null) {
                            return true
                        }
                        throw AssertionError()
                    }

                    override fun getChangePayload(
                        oldItemPosition: Int,
                        newItemPosition: Int
                    ): Any? {
                        val oldItem: T? = oldList[oldItemPosition]
                        val newItem: T? = newList[newItemPosition]
                        if (oldItem != null && newItem != null) {
                            return mConfig.diffCallback.getChangePayload(oldItem, newItem)
                        }
                        throw AssertionError()
                    }
                }, false)
            mMainThreadExecutor.execute {
                if (mMaxScheduledGeneration == runGeneration) {
                    latchList(newList, result, commitCallback)
                }
            }
        }
    }

    fun  /* synthetic access */latchList(
        newList: List<T>,
        diffResult: DiffResult,
        commitCallback: Runnable?
    ) {
        val previousList = currentList
        mList = newList
        // notify last, after list is updated
        currentList = Collections.unmodifiableList(newList)
        diffResult.dispatchUpdatesTo(mUpdateCallback)
        onCurrentListChanged(previousList, commitCallback)
    }

    private fun onCurrentListChanged(
        previousList: List<T>,
        commitCallback: Runnable?
    ) {
        // current list is always mReadOnlyList
        for (listener in mListeners) {
            listener.onCurrentListChanged(previousList, currentList)
        }
        commitCallback?.run()
    }

    /**
     * Add a ListListener to receive updates when the current List changes.
     *
     * @param listener Listener to receive updates.
     *
     * @see .getCurrentList
     * @see .removeListListener
     */
    fun addListListener(listener: ListListener<T>) {
        mListeners.add(listener)
    }

    /**
     * Remove a previously registered ListListener.
     *
     * @param listener Previously registered listener.
     * @see .getCurrentList
     * @see .addListListener
     */
    fun removeListListener(listener: ListListener<T>) {
        mListeners.remove(listener)
    }

    companion object {
        private val sMainThreadExecutor: Executor =
            MainThreadExecutor()
    }

    /**
     * Create a AsyncListDiffer with the provided config, and ListUpdateCallback to dispatch
     * updates to.
     *
     * @param listUpdateCallback Callback to dispatch updates to.
     * @param config Config to define background work Executor, and DiffUtil.ItemCallback for
     * computing List diffs.
     *
     * @see DiffUtil.DiffResult.dispatchUpdatesTo
     */
    init {
        mMainThreadExecutor = sMainThreadExecutor
    }
}
