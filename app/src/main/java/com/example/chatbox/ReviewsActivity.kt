package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class ReviewsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        findViewById<View>(R.id.nav_home_reviews)?.setOnClickListener {
            val intent = Intent(this, LibraryHomeActivity::class.java)
            intent.putExtra("IS_ADMIN", isAdmin)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_unishop_reviews)?.setOnClickListener {
            val destination = if (isAdmin) {
                LojaCustodioPontosActivity::class.java
            } else {
                TelaLojaCustodioActivity::class.java
            }
            val intent = Intent(this, destination)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_datas_reviews)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_perfil_reviews)?.setOnClickListener {
            val destination = if (isAdmin) {
                perfiladm::class.java
            } else {
                perfiluser::class.java
            }
            val intent = Intent(this, destination)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }

        findViewById<View>(R.id.book_noite_taverna_reviews)?.setOnClickListener {
            val intent = Intent(this, ListaResenhasActivity::class.java)
            intent.putExtra("LIVRO_ID", "noite_na_taverna_id") // ID de exemplo
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }
    }
}
