package com.kamer.lifetracker.properties

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.kamer.lifetracker.R
import kotlinx.android.synthetic.main.item_property.view.*


class PropertyAdapter(
    private val listener: (String) -> Unit
) : RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<UiProperty>() {
        override fun areItemsTheSame(oldItem: UiProperty, newItem: UiProperty): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: UiProperty, newItem: UiProperty): Boolean =
            oldItem == newItem
    })

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_property, parent, false),
        listener
    )

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    fun setData(data: List<UiProperty>) {
        differ.submitList(data)
    }

    class ViewHolder(
        view: View,
        private val listener: (String) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.textView

        fun bind(model: UiProperty) {
            itemView.setOnClickListener { listener(model.id) }
            textView.text = model.name
        }
    }

}
