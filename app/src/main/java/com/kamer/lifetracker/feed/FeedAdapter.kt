package com.kamer.lifetracker.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.ItemDayProgressBinding
import com.kamer.lifetracker.databinding.ItemSkippedDayBinding
import org.threeten.bp.LocalDate


class FeedAdapter(private val listener: (LocalDate) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: List<FeedItem> = emptyList()

    override fun getItemViewType(position: Int): Int = when (data[position]) {
        is SkippedDay -> 0
        is TodayProgress.Progress -> 1
        is TodayProgress.Done -> 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> SkippedDayViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_skipped_day,
                    parent,
                    false
                ), listener
            )
            1 -> ProgressViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_day_progress,
                    parent,
                    false
                ), listener
            )
            else -> DoneViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_day_done,
                    parent,
                    false
                )
            )
        }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = data[position]
        when {
            holder is SkippedDayViewHolder && model is SkippedDay -> holder.bind(model)
            holder is ProgressViewHolder && model is TodayProgress.Progress -> holder.bind(model)
        }
    }

    fun setData(data: List<FeedItem>) {
        this.data = data
        notifyDataSetChanged()
    }

    class SkippedDayViewHolder(view: View, private val listener: (LocalDate) -> Unit) :
        RecyclerView.ViewHolder(view) {

        private val binding = ItemSkippedDayBinding.bind(view)

        fun bind(model: SkippedDay) = with(binding) {
            buttonView.setOnClickListener { listener(model.date) }
            dateView.text = model.date.toString()
        }
    }

    class ProgressViewHolder(view: View, private val listener: (LocalDate) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private val binding = ItemDayProgressBinding.bind(view)

        fun bind(model: TodayProgress.Progress) = with(binding) {
            buttonView.setOnClickListener { listener(model.date) }
            textView.text = "${model.progress} of ${model.total}"
            progressView.max = model.total
            progressView.progress = model.progress
        }
    }

    class DoneViewHolder(view: View) : RecyclerView.ViewHolder(view)

}
