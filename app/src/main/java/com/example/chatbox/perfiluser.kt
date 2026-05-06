package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class perfiluser : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfiluser)

        findViewById<View>(R.id.nav_unishop)?.setOnClickListener {
            val intent = Intent(this, TelaLojaCustodioActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_reviews)?.setOnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_home)?.setOnClickListener {
            finish() // Volta para a Home
        }

        findViewById<View>(R.id.nav_datas_user)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
