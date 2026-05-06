package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class AvaliacoesDetalhesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avaliacoes_detalhes)

        val btnClose = findViewById<ImageView>(R.id.btnCloseAvaliacoes)
        btnClose.setOnClickListener {
            finish()
        }

        val btnReviews = findViewById<Button>(R.id.btnReviewsList)
        btnReviews.setOnClickListener {
            val intent = Intent(this, AvaliarUsuariosActivity::class.java)
            startActivity(intent)
        }

        val btnAcessar = findViewById<Button>(R.id.btnAcessarLivro)
        btnAcessar.setOnClickListener {
            val intent = Intent(this, DetalhesLivroActivity::class.java)
            startActivity(intent)
        }
    }
}
