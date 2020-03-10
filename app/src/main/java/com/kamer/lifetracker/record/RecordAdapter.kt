package com.kamer.lifetracker.record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.ItemRecordFieldBinding


class RecordAdapter(
    private val listener: (String, Boolean) -> Unit
) : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    private val differ = AsyncListDifferWithoutMoves(
        this,
        object : DiffUtil.ItemCallback<RecordField>() {
            override fun areItemsTheSame(oldItem: RecordField, newItem: RecordField): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: RecordField,
                newItem: RecordField
            ): Boolean =
                oldItem == newItem
        }
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_record_field, parent, false),
        listener
    )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    fun setData(data: List<RecordField>) {
        differ.submitList(data)
    }

    class ViewHolder(
        view: View,
        private val listener: (String, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding = ItemRecordFieldBinding.bind(view)

        fun bind(model: RecordField) = with(binding) {
            yesView.setOnClickListener { listener(model.id, true) }
            noView.setOnClickListener { listener(model.id, false) }
            nameView.text = model.name
            yesView.isChecked = model.isPositive == true
            noView.isChecked = model.isPositive == false
            val alpha = if (model.isPositive == null) 1f else 0.5f
            yesView.alpha = alpha
            noView.alpha = alpha
            nameView.alpha = alpha
        }
    }

}
