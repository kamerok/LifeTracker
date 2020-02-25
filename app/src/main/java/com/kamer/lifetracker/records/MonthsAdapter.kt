package com.kamer.lifetracker.records

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kamer.lifetracker.R
import kotlinx.android.synthetic.main.item_month.view.*


class MonthsAdapter(private val listener: (UiDay.RealDay) -> Unit) :
    RecyclerView.Adapter<MonthsAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<UiMonth>() {
        override fun areItemsTheSame(oldItem: UiMonth, newItem: UiMonth): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: UiMonth, newItem: UiMonth): Boolean =
            oldItem == newItem
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_month, parent, false),
        listener
    )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    fun setData(data: List<UiMonth>) {
        differ.submitList(data)
    }

    class ViewHolder(
        view: View,
        private val listener: (UiDay.RealDay) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val nameView: TextView = view.nameView
        private val recyclerView: RecyclerView = view.recyclerView

        private val adapter = DayAdapter(listener)

        init {
            recyclerView.layoutManager = GridLayoutManager(view.context, 7)
            recyclerView.adapter = adapter
        }

        fun bind(model: UiMonth) {
            nameView.text = model.name
            adapter.setData(model.days)
        }
    }

}
