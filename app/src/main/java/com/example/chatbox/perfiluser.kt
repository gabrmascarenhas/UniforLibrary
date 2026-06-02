package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
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
    // Definindo a URL explicitamente para garantir a conexão
    private val databaseUrl = "https://uniforlibrary-30c5c-default-rtdb.firebaseio.com/"
    private val db = Firebase.database(databaseUrl).reference
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfiluser)

        setupNavigation()
        carregarPontos()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.nav_unishop)?.setOnClickListener {
            startActivity(Intent(this, TelaLojaCustodioActivity::class.java))
        }

        findViewById<View>(R.id.nav_reviews)?.setOnClickListener {
            startActivity(Intent(this, ReviewsActivity::class.java))
        }

        findViewById<View>(R.id.nav_home)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.nav_datas_user)?.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
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
                Log.e("FirebasePerfil", "Erro ao carregar pontos", e)
                tvPontos.text = "Sua pontuação total:\n0 pontos!"
                // Mostrar erro detalhado para diagnóstico
                Toast.makeText(this@perfiluser, "Erro de conexão: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
