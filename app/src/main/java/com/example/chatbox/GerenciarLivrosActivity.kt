package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class GerenciarLivrosActivity : AppCompatActivity() {

    private val repositorio = RepositorioLivros()
    private lateinit var adapter: LivroAdapter
    private lateinit var rvLivros: RecyclerView
    private var todosOsLivros: List<Livro> = mutableListOf<Livro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gerenciar_livros)

        rvLivros = findViewById(R.id.rvLivros)
        val fabAddLivro = findViewById<ExtendedFloatingActionButton>(R.id.fabAddLivro)
        val btnBack = findViewById<android.view.View>(R.id.btnBack)
        val etSearch = findViewById<EditText>(R.id.etSearchAdmin)

        btnBack.setOnClickListener {
            finish()
        }

        rvLivros.layoutManager = LinearLayoutManager(this)
        adapter = LivroAdapter(mutableListOf<Livro>(), 
            onDeleteClick = { livro: Livro ->
                confirmarRemocao(livro)
            },
            onEditClick = { livro: Livro ->
                abrirEdicao(livro)
            }
        )
        rvLivros.adapter = adapter

        fabAddLivro.setOnClickListener {
            val intent = Intent(this, CadastroLivroActivity::class.java)
            startActivity(intent)
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarLivros(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        carregarLivros()
    }

    private fun abrirEdicao(livro: Livro) {
        val intent = Intent(this, CadastroLivroActivity::class.java)
        intent.putExtra("LIVRO_ID", livro.id)
        intent.putExtra("TITULO", livro.titulo)
        intent.putExtra("AUTOR", livro.autor)
        intent.putExtra("EDITORA", livro.editora)
        intent.putExtra("ANO", livro.ano)
        intent.putExtra("SINOPSE", livro.sinopse)
        intent.putExtra("CAPA", livro.capUrl)
        intent.putExtra("PDF", livro.pdfUrl)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        carregarLivros()
    }

    private fun carregarLivros() {
        lifecycleScope.launch {
            try {
                todosOsLivros = repositorio.listarLivros()
                adapter.updateList(todosOsLivros)
            } catch (e: Exception) {
                Toast.makeText(this@GerenciarLivrosActivity, "Erro ao carregar livros: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filtrarLivros(query: String) {
        val listaFiltrada = if (query.isEmpty()) {
            todosOsLivros
        } else {
            todosOsLivros.filter { livro ->
                val t: String = livro.titulo
                val a: String = livro.autor
                t.contains(query, ignoreCase = true) || a.contains(query, ignoreCase = true)
            }
        }
        adapter.updateList(listaFiltrada)
    }

    private fun confirmarRemocao(livro: Livro) {
        lifecycleScope.launch {
            try {
                repositorio.removerLivro(livro.id)
                Toast.makeText(this@GerenciarLivrosActivity, "Livro removido", Toast.LENGTH_SHORT).show()
                carregarLivros()
            } catch (e: Exception) {
                Toast.makeText(this@GerenciarLivrosActivity, "Erro ao remover: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}