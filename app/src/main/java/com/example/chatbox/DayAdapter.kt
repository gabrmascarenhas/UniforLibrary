package com.example.chatbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class DayAdapter(private val days: List<LocalDate?>, private val currentMonth: LocalDate) :
    RecyclerView.Adapter<DayAdapter.DayViewHolder>() {

    // Mock special dates for March 2026 based on the user's design
    private val specialDates = listOf(
        LocalDate.of(2026, 3, 9),
        LocalDate.of(2026, 3, 10),
        LocalDate.of(2026, 3, 11),
        LocalDate.of(2026, 3, 12),
        LocalDate.of(2026, 3, 13),
        LocalDate.of(2026, 3, 18)
    )

    class DayViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textDay: TextView = view.findViewById(R.id.textDay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calendar_day, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val date = days[position]
        if (date != null) {
            holder.textDay.text = date.dayOfMonth.toString()
            
            if (date.month == currentMonth.month) {
                holder.textDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.white))
                
                if (specialDates.contains(date)) {
                    holder.textDay.setBackgroundResource(R.drawable.bg_blue_rounded_square)
                } else {
                    holder.textDay.background = null
                }
            } else {
                // Days from prev/next month
                holder.textDay.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gray_text))
                holder.textDay.background = null
            }
        } else {
            holder.textDay.text = ""
            holder.textDay.background = null
        }
    }

    override fun getItemCount(): Int = days.size
}
