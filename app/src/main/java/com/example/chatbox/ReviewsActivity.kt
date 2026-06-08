package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ReviewsActivity : AppCompatActivity() {

    private val repositorio = RepositorioLivros()
    private lateinit var adapter: HomeLivroAdapter
    private lateinit var rvLivros: RecyclerView
    private var listaCompletaLivros: List<Livro> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        // Inicializar RecyclerView e Adapter
        rvLivros = findViewById(R.id.rvLivrosReviews)
        rvLivros.layoutManager = GridLayoutManager(this, 3)
        adapter = HomeLivroAdapter(listaCompletaLivros) { livro ->
            val intent = Intent(this, ListaResenhasActivity::class.java)
            intent.putExtra("LIVRO_ID", livro.id)
            intent.putExtra("LIVRO_TITULO", livro.titulo)
            intent.putExtra("LIVRO_CAPA", livro.capUrl)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }
        rvLivros.adapter = adapter

        // Barra de Busca
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarLivros(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Navegação
        findViewById<View>(R.id.nav_home_reviews)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.nav_unishop_reviews)?.setOnClickListener {
            val destination = if (isAdmin) LojaCustodioPontosActivity::class.java else TelaLojaCustodioActivity::class.java
            val intentNav = Intent(this, destination)
            intentNav.putExtra("IS_ADMIN", isAdmin)
            startActivity(intentNav)
        }

        findViewById<View>(R.id.nav_datas_reviews)?.setOnClickListener {
            val intentNav = Intent(this, CalendarActivity::class.java)
            intentNav.putExtra("IS_ADMIN", isAdmin)
            startActivity(intentNav)
        }

        findViewById<View>(R.id.nav_perfil_reviews)?.setOnClickListener {
            val destination = if (isAdmin) perfiladm::class.java else perfiluser::class.java
            val intentNav = Intent(this, destination)
            intentNav.putExtra("IS_ADMIN", isAdmin)
            startActivity(intentNav)
        }

        carregarLivros()
    }

    private fun carregarLivros() {
        lifecycleScope.launch {
            try {
                listaCompletaLivros = repositorio.listarLivros()
                if (::adapter.isInitialized) {
                    adapter.updateList(listaCompletaLivros)
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReviewsActivity, "Erro ao carregar livros: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filtrarLivros(query: String) {
        val listaFiltrada = if (query.isEmpty()) {
            listaCompletaLivros
        } else {
            listaCompletaLivros.filter { livro ->
                livro.titulo.contains(query, ignoreCase = true) ||
                        livro.autor.contains(query, ignoreCase = true)
            }
        }
        if (::adapter.isInitialized) {
            adapter.updateList(listaFiltrada)
        }
    }
}
