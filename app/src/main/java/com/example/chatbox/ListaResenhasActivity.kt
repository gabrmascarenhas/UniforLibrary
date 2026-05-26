package com.example.chatbox

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ListaResenhasActivity : AppCompatActivity() {

    private val repositorio = RepositorioResenha()
    private lateinit var adapter: ResenhaAdapter
    private var livroId: String = ""
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_resenhas)

        livroId = intent.getStringExtra("LIVRO_ID") ?: "livro_teste_id"
        // No mundo real, você pegaria o status de admin do UserManager ou do Intent
        isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        setupRecyclerView()
        carregarResenhas()
    }

    private fun setupRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rvResenhas)
        adapter = ResenhaAdapter(emptyList(), isAdmin) { resenha ->
            apagarResenha(resenha)
        }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    private fun carregarResenhas() {
        lifecycleScope.launch {
            try {
                val lista = repositorio.listarResenhasDeLivro(livroId)
                adapter.updateList(lista)
                findViewById<TextView>(R.id.tvNoReviews).visibility = 
                    if (lista.isEmpty()) View.VISIBLE else View.GONE
            } catch (e: Exception) {
                Toast.makeText(this@ListaResenhasActivity, "Erro: \${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun apagarResenha(resenha: Resenha) {
        lifecycleScope.launch {
            try {
                repositorio.removerResenha(resenha.id)
                Toast.makeText(this@ListaResenhasActivity, "Resenha removida", Toast.LENGTH_SHORT).show()
                carregarResenhas() // Atualiza a lista
            } catch (e: Exception) {
                Toast.makeText(this@ListaResenhasActivity, "Erro ao remover", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
