package com.example.chatbox

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class LibraryHomeActivity : AppCompatActivity() {

    private val repositorio = RepositorioLivros()
    private lateinit var adapter: HomeLivroAdapter
    private lateinit var rvLivros: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_home)

        rvLivros = findViewById(R.id.rvLivrosHome)
        rvLivros.layoutManager = GridLayoutManager(this, 3)
        adapter = HomeLivroAdapter(mutableListOf<Livro>()) { livro: Livro ->
            showAccessBookDialog(livro)
        }
        rvLivros.adapter = adapter

        // Configurar clique no ícone de perfil do topo (círculo)
        findViewById<View>(R.id.circleProfile).setOnClickListener { openProfile() }

        // Configurar clique no ícone de perfil da Bottom Nav
        findViewById<View>(R.id.nav_perfil_home).setOnClickListener { openProfile() }

        // Configurar clique no UniShop
        findViewById<View>(R.id.nav_unishop_home)?.setOnClickListener {
            val intent = Intent(this, TelaLojaCustodioActivity::class.java)
            startActivity(intent)
        }

        // Configurar clique no Reviews
        findViewById<View>(R.id.nav_reviews_home)?.setOnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            startActivity(intent)
        }

        // Configurar clique no Datas (Calendário)
        findViewById<View>(R.id.nav_datas_home)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        // Configurar clique no ícone do Terminal (Brackets)
        findViewById<View>(R.id.circleTerminal).setOnClickListener {
            val intent = Intent(this, ChatBoxActivity::class.java)
            startActivity(intent)
        }

        // Configurar clique no botão Requisitar Livro
        findViewById<Button>(R.id.btnRequisitarLivro)?.setOnClickListener {
            val intent = Intent(this, RequisitarLivroCustodioActivity::class.java)
            startActivity(intent)
        }

        // Mapeamento de IDs para nomes dos livros
        val bookNames = mapOf(
            R.id.book_norma to "Teoria da Norma Jurídica",
            R.id.book_legado to "Código Legado",
            R.id.book_sobotta to "Sobotta",
            R.id.book_sentido to "Em Busca de Sentido",
            R.id.book_red_center to "Livro Vermelho Central",
            R.id.book_crime to "Crime e Castigo",
            R.id.book_noite_taverna to "Noite na Taverna",
            R.id.book_jazz to "Jazz",
            R.id.book_lines to "Linhas Brancas",
            R.id.book_expressionismo to "Expressionismo",
            R.id.book_teologia to "Teologia",
            R.id.book_dadaismo to "Dadaismo"
        )

        val bookIds = bookNames.keys

        // Configurar a Pesquisa Inteligente
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim().lowercase()
                bookNames.forEach { (id, name) ->
                    val bookView = findViewById<View>(id)
                    if (name.lowercase().contains(query)) {
                        bookView.visibility = View.VISIBLE
                    } else {
                        bookView.visibility = View.GONE
                    }
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Configurar o clique para cada livro
        bookIds.forEach { id ->
            findViewById<View>(id)?.setOnClickListener {
                if (id == R.id.book_noite_taverna) {
                    val intent = Intent(this, AvaliacoesDetalhesActivity::class.java)
                    startActivity(intent)
                } else {
                    showAccessBookDialog()
                }
            }
        }
    }

    private fun openProfile() {
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        if (isAdmin) {
            val intent = Intent(this, perfiladm::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, perfiluser::class.java)
            startActivity(intent)
        }
    }

    private fun showAccessBookDialog(livro: Livro) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_access_book)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYes = dialog.findViewById<Button>(R.id.btnYes)
        val btnNo = dialog.findViewById<Button>(R.id.btnNo)

        btnYes.setOnClickListener {
            val intent = Intent(this, DetalhesLivroActivity::class.java)
            intent.putExtra("LIVRO_ID", livro.id)
            startActivity(intent)
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}