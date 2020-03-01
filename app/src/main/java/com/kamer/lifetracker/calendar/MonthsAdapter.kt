package com.kamer.lifetracker.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kamer.lifetracker.R
import com.kamer.lifetracker.databinding.ItemMonthBinding
import org.threeten.bp.LocalDate


class MonthsAdapter(private val listener: (LocalDate) -> Unit) :
    RecyclerView.Adapter<MonthsAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<UiMonth>() {
        override fun areItemsTheSame(oldItem: UiMonth, newItem: UiMonth): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: UiMonth, newItem: UiMonth): Boolean =
            oldItem == newItem
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
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
        listener: (LocalDate) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val binding = ItemMonthBinding.bind(view)

        private val adapter = DayAdapter(listener)

        init {
            binding.recyclerView.adapter = adapter
        }

        fun bind(model: UiMonth) = with(binding) {
            nameView.text = model.name
            adapter.setData(model.days)
        }
    }

}
