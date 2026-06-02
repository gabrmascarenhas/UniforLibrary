package com.example.chatbox

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
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

        carregarLivros()
    }

    override fun onResume() {
        super.onResume()
        carregarLivros()
    }

    private fun carregarLivros() {
        lifecycleScope.launch {
            try {
                val livros = repositorio.listarLivros()
                adapter.updateList(livros)
            } catch (e: Exception) {
                Toast.makeText(this@LibraryHomeActivity, "Erro ao carregar catálogo: ${e.message}", Toast.LENGTH_SHORT).show()
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