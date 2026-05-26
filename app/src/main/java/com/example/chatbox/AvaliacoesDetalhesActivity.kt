package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class AvaliacoesDetalhesActivity : AppCompatActivity() {

    private val repositorioLivros = RepositorioLivros()
    private var livroId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avaliacoes_detalhes)

        livroId = intent.getStringExtra("LIVRO_ID")

        if (livroId != null) {
            carregarDetalhesDoLivro(livroId!!)
        } else {
            Toast.makeText(this, "ID do livro não encontrado", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.btnCloseAvaliacoes).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnReviewsList).setOnClickListener {
            val intent = Intent(this, AvaliarUsuariosActivity::class.java)
            intent.putExtra("LIVRO_ID", livroId)
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnAcessarLivro).setOnClickListener {
            val intent = Intent(this, DetalhesLivroActivity::class.java)
            intent.putExtra("LIVRO_ID", livroId)
            startActivity(intent)
        }
    }

    private fun carregarDetalhesDoLivro(id: String) {
        lifecycleScope.launch {
            try {
                val livro = repositorioLivros.obterLivro(id)
                if (livro != null) {
                    findViewById<TextView>(R.id.tvBookTitleAvaliacoes).text = livro.titulo
                    findViewById<TextView>(R.id.tvBookAuthorAvaliacoes).text = livro.autor
                    findViewById<TextView>(R.id.tvBookPublisherAvaliacoes).text = "Editora: ${livro.editora}"
                    findViewById<TextView>(R.id.tvSinopseAvaliacoes).text = livro.sinopse
                    
                    val ivCover = findViewById<ImageView>(R.id.ivBookCoverAvaliacoes)
                    Glide.with(this@AvaliacoesDetalhesActivity)
                        .load(livro.capUrl)
                        .placeholder(R.drawable.noite_na_taverna)
                        .into(ivCover)
                    
                    // Nota fixa como exemplo, ou vinda do banco se existir esse campo
                    findViewById<TextView>(R.id.tvBookRatingAvaliacoes).text = "4,5/5"
                } else {
                    Toast.makeText(this@AvaliacoesDetalhesActivity, "Livro não encontrado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AvaliacoesDetalhesActivity, "Erro ao carregar detalhes", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
