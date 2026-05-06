package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class TelaLojaCustodioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_loja_custodio)

        // Botão Home na Bottom Nav da Loja
        findViewById<View>(R.id.nav_home_loja)?.setOnClickListener {
            finish() // Volta para a tela anterior (geralmente a Home)
        }

        findViewById<View>(R.id.nav_reviews_loja)?.setOnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_perfil_loja)?.setOnClickListener {
            // Aqui precisaria saber se é admin ou não para decidir a tela
            // Como é um redirecionamento simples, vamos deixar para o fluxo normal de login
        }

        findViewById<View>(R.id.nav_data_loja)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }
    }
}