package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class CalendarActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var textYear: TextView
    private lateinit var textMonth: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        textYear = findViewById(R.id.textYear)
        textMonth = findViewById(R.id.textMonth)
        viewPager = findViewById(R.id.calendarPager)

        // Setup bottom navigation listeners
        findViewById<View>(R.id.nav_home_calendar)?.setOnClickListener {
            val intent = Intent(this, LibraryHomeActivity::class.java)
            intent.putExtra("IS_ADMIN", isAdmin)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_reviews_calendar)?.setOnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_unishop_calendar)?.setOnClickListener {
            val destination = if (isAdmin) {
                LojaCustodioPontosActivity::class.java
            } else {
                TelaLojaCustodioActivity::class.java
            }
            val intent = Intent(this, destination)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_perfil_calendar)?.setOnClickListener {
            val destination = if (isAdmin) {
                perfiladm::class.java
            } else {
                perfiluser::class.java
            }
            val intent = Intent(this, destination)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_datas_calendar)?.setOnClickListener {
            viewPager.setCurrentItem(2, true) // Volta para Março 2026
        }

        val startMonth = LocalDate.of(2026, 1, 1)
        val months = (0..23).map { startMonth.plusMonths(it.toLong()) }

        val adapter = CalendarAdapter(months)
        viewPager.adapter = adapter

        // Set to March 2026 (index 2)
        viewPager.setCurrentItem(2, false)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val currentMonth = months[position]
                textYear.text = currentMonth.year.toString()
                textMonth.text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                    .replaceFirstChar { it.uppercase() }
            }
        })

        findViewById<View>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
    }
}
