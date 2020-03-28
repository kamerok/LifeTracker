package com.kamer.lifetracker.spreadsheets

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kamer.lifetracker.R
import com.kamer.lifetracker.Spreadsheet
import com.kamer.lifetracker.databinding.ItemPropertyBinding


class SpreadsheetAdapter(
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<SpreadsheetAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Spreadsheet>() {
        override fun areItemsTheSame(oldItem: Spreadsheet, newItem: Spreadsheet): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Spreadsheet, newItem: Spreadsheet): Boolean =
            oldItem == newItem
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_spreadsheet, parent, false)
    )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    fun setData(data: List<Spreadsheet>) {
        differ.submitList(data)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemPropertyBinding.bind(view)

        fun bind(model: Spreadsheet) = with(binding) {
            itemView.setOnClickListener { listener(model.id) }
            textView.text = model.name
        }
    }

}
