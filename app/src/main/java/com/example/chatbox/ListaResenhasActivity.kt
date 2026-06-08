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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class ListaResenhasActivity : AppCompatActivity() {

    private val repositorio = RepositorioResenha()
    private val database = FirebaseDatabase.getInstance().getReference("resenhas")
    private lateinit var adapter: ResenhaAdapter
    private var livroId: String = ""
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_resenhas)

        livroId = intent.getStringExtra("LIVRO_ID") ?: "livro_teste_id"
        val livroTitulo = intent.getStringExtra("LIVRO_TITULO")
        if (livroTitulo != null) {
            findViewById<TextView>(R.id.tvTitleHeader).text = "Resenhas: $livroTitulo"
        }
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
        // Se o ID for de teste ou vazio, podemos mostrar tudo para ajudar o usuário a ver o que tem no banco
        val query = if (livroId == "livro_teste_id" || livroId.isEmpty()) {
            database
        } else {
            database.orderByChild("livroId").equalTo(livroId)
        }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaResenhas = mutableListOf<Resenha>()
                for (resenhaSnapshot in snapshot.children) {
                    try {
                        val resenha = resenhaSnapshot.getValue(Resenha::class.java)
                        resenha?.let { listaResenhas.add(it) }
                    } catch (e: Exception) {
                        // Log ou Toast para avisar sobre erro de parsing
                    }
                }

                // Se não encontrou nada com o filtro, tenta mostrar TUDO como fallback para debug
                if (listaResenhas.isEmpty() && query != database) {
                    database.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(allSnapshot: DataSnapshot) {
                            val todasResenhas = mutableListOf<Resenha>()
                            for (snap in allSnapshot.children) {
                                snap.getValue(Resenha::class.java)?.let { todasResenhas.add(it) }
                            }
                            if (todasResenhas.isNotEmpty()) {
                                Toast.makeText(this@ListaResenhasActivity, "Nenhuma resenha para este ID. Mostrando todas as resenhas do banco.", Toast.LENGTH_LONG).show()
                                adapter.updateList(todasResenhas.sortedByDescending { it.data })
                                findViewById<TextView>(R.id.tvNoReviews).visibility = View.GONE
                            } else {
                                mostrarListaVazia()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            mostrarListaVazia()
                        }
                    })
                } else {
                    val listaOrdenada = listaResenhas.sortedByDescending { it.data }
                    adapter.updateList(listaOrdenada)
                    findViewById<TextView>(R.id.tvNoReviews).visibility =
                        if (listaResenhas.isEmpty()) View.VISIBLE else View.GONE
                    
                    if (listaResenhas.isEmpty()) {
                        findViewById<TextView>(R.id.tvNoReviews).text = "Nenhuma resenha encontrada para: $livroId"
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ListaResenhasActivity, "Erro: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarListaVazia() {
        adapter.updateList(emptyList())
        findViewById<TextView>(R.id.tvNoReviews).visibility = View.VISIBLE
        findViewById<TextView>(R.id.tvNoReviews).text = "Banco de resenhas está vazio."
    }

    private fun apagarResenha(resenha: Resenha) {
        lifecycleScope.launch {
            try {
                repositorio.removerResenha(resenha.id)
                Toast.makeText(this@ListaResenhasActivity, "Resenha removida", Toast.LENGTH_SHORT).show()
                // carregarResenhas() removido pois o addValueEventListener atualiza automaticamente
            } catch (e: Exception) {
                Toast.makeText(this@ListaResenhasActivity, "Erro ao remover", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
