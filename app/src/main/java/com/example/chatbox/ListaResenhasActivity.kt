package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
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
    private var livroCapa: String? = null
    private var isAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_resenhas)

        livroId = intent.getStringExtra("LIVRO_ID") ?: "livro_teste_id"
        livroCapa = intent.getStringExtra("LIVRO_CAPA")
        val livroTitulo = intent.getStringExtra("LIVRO_TITULO")
        isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        if (livroTitulo != null) {
            findViewById<TextView>(R.id.tvTitleHeader).text = "Resenhas: $livroTitulo"
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            voltarParaReviews()
        }

        findViewById<Button>(R.id.btnVoltarReviews).setOnClickListener {
            voltarParaReviews()
        }

        setupRecyclerView()
        carregarResenhas()
    }

    private fun voltarParaReviews() {
        val intent = Intent(this, ReviewsActivity::class.java)
        intent.putExtra("IS_ADMIN", isAdmin)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }

    private fun setupRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rvResenhas)
        adapter = ResenhaAdapter(emptyList(), isAdmin, livroCapa) { resenha ->
            apagarResenha(resenha)
        }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    private fun carregarResenhas() {
        database.orderByChild("livroId").equalTo(livroId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val listaResenhas = mutableListOf<Resenha>()
                    for (resenhaSnapshot in snapshot.children) {
                        val resenha = resenhaSnapshot.getValue(Resenha::class.java)
                        resenha?.let { listaResenhas.add(it) }
                    }

                    // Ordenar por data (mais recente primeiro)
                    val listaOrdenada = listaResenhas.sortedByDescending { it.data }
                    adapter.updateList(listaOrdenada)

                    findViewById<TextView>(R.id.tvNoReviews).visibility =
                        if (listaResenhas.isEmpty()) View.VISIBLE else View.GONE
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ListaResenhasActivity, "Erro: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
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
