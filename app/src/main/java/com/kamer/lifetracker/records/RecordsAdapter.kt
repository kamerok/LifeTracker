package com.kamer.lifetracker.records

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kamer.lifetracker.R
import kotlinx.android.synthetic.main.item_record.view.*


class RecordsAdapter(private val listener: (UiRecord) -> Unit) :
    RecyclerView.Adapter<RecordsAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<UiRecord>() {
        override fun areItemsTheSame(oldItem: UiRecord, newItem: UiRecord): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UiRecord, newItem: UiRecord): Boolean =
            oldItem == newItem
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_record, parent, false),
        listener
    )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    fun setData(data: List<UiRecord>) {
        differ.submitList(data)
    }

    class ViewHolder(
        view: View,
        private val listener: (UiRecord) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val dateView: TextView = view.dateView
        private val stateView: TextView = view.stateView

        fun bind(model: UiRecord) {
            itemView.setOnClickListener { listener(model) }

            dateView.text = model.date.toString()
            stateView.text = model.state
        }
    }

}
