package com.kamer.lifetracker.record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kamer.lifetracker.R
import kotlinx.android.synthetic.main.item_record_field.view.*


class RecordAdapter : RecyclerView.Adapter<RecordAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<RecordField>() {
        override fun areItemsTheSame(oldItem: RecordField, newItem: RecordField): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: RecordField, newItem: RecordField): Boolean =
            oldItem == newItem
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_record_field, parent, false)
    )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    fun setData(data: List<RecordField>) {
        differ.submitList(data)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.nameView
        private val yesView: CheckBox = view.yesView
        private val noView: CheckBox = view.noView

        fun bind(model: RecordField) {
            nameView.text = model.name
            yesView.isChecked = model.isPositive == true
            noView.isChecked = model.isPositive == false
            itemView.alpha = if (model.isPositive == null) 1f else 0.5f
        }
    }

}
