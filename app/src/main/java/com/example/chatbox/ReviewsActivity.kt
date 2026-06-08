package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ReviewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        findViewById<View>(R.id.nav_home_reviews)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.nav_unishop_reviews)?.setOnClickListener {
            val intent = Intent(this, TelaLojaCustodioActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_datas_reviews)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.book_noite_taverna_reviews)?.setOnClickListener {
            val intent = Intent(this, ListaResenhasActivity::class.java)
            intent.putExtra("LIVRO_ID", "noite_na_taverna_id")
            intent.putExtra("LIVRO_TITULO", "Noite na Taverna")
            intent.putExtra("IS_ADMIN", true)
            startActivity(intent)
        }
    }
}
