package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class perfiluser : AppCompatActivity() {
    private val db = Firebase.database.reference
    private val auth = Firebase.auth

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

        carregarPontos()
    }

    private fun carregarPontos() {
        val tvPontos = findViewById<TextView>(R.id.textView10)
        val userId = auth.currentUser?.uid ?: return

        lifecycleScope.launch {
            try {
                val snapshot = db.child("users").child(userId).child("pontos").get().await()
                val pontos = snapshot.getValue(Int::class.java) ?: 0
                tvPontos.text = "Sua pontuação total:\n$pontos pontos!"
            } catch (e: Exception) {
                tvPontos.text = "Sua pontuação total:\n0 pontos!"
            }
        }
    }
}
