package com.kamer.lifetracker.calendar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.kamer.lifetracker.R
import kotlinx.android.synthetic.main.item_day.view.*
import org.threeten.bp.LocalDate


class DayAdapter(
    private val listener: (LocalDate) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data = listOf<UiDay>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            0 -> DummyHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_dummy_day,
                    parent,
                    false
                )
            )
            else -> RealHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_day,
                    parent,
                    false
                ), listener
            )
        }

    override fun getItemViewType(position: Int): Int = when (data[position]) {
        is UiDay.DummyDay -> 0
        else -> 1
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val day = data[position]
        if (holder is RealHolder && day is UiDay.RealDay) {
            holder.bind(day)
        }
    }

    fun setData(data: List<UiDay>) {
        this.data = data
        notifyDataSetChanged()
    }

    class DummyHolder(view: View) : RecyclerView.ViewHolder(view)

    class RealHolder(
        view: View,
        private val listener: (LocalDate) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.textView
        private val todayView: View = view.todayView
        private val filledView: View = view.filledView

        fun bind(model: UiDay.RealDay) {
            itemView.setOnClickListener { listener(model.date) }

            textView.text = model.date.dayOfMonth.toString()
            todayView.isVisible = model.isToday
            filledView.isVisible = model.isFilled
        }
    }
}
