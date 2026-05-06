package com.example.chatbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.YearMonth

class CalendarAdapter(private val months: List<LocalDate>) :
    RecyclerView.Adapter<CalendarAdapter.MonthViewHolder>() {

    class MonthViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerViewDays: RecyclerView = view.findViewById(R.id.recyclerViewDays)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_month_grid, parent, false)
        return MonthViewHolder(view)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        val monthDate = months[position]
        val daysInMonth = getDaysInMonthList(monthDate)

        holder.recyclerViewDays.layoutManager = GridLayoutManager(holder.itemView.context, 7)
        holder.recyclerViewDays.adapter = DayAdapter(daysInMonth, monthDate)
    }

    override fun getItemCount(): Int = months.size

    private fun getDaysInMonthList(date: LocalDate): List<LocalDate?> {
        val days = mutableListOf<LocalDate?>()
        val yearMonth = YearMonth.from(date)
        val daysInMonth = yearMonth.lengthOfMonth()
        val firstOfMonth = date.withDayOfMonth(1)
        val dayOfWeek = firstOfMonth.dayOfWeek.value % 7 // 0 = Sunday, 1 = Monday...

        // Add empty slots for days before the start of the month
        // In your design you showed previous month hints, but let's keep it simple or use nulls
        // Actually, to match your "sideways" grid, we'd need a different logic, 
        // but standard calendars use rows. The user's image shows:
        // Rows: dom, seg, ter, qua...
        // Columns: Weeks.
        // This is a 7-row grid (Day names + 6 weeks).
        
        // However, to keep it standard and easier to slide, we'll use a 7-column grid (Sun-Sat).
        // If the user strictly wants the "sideways" look, we would use 7 rows.
        
        for (i in 0 until dayOfWeek) {
            days.add(null)
        }
        for (i in 1..daysInMonth) {
            days.add(date.withDayOfMonth(i))
        }
        return days
    }
}
