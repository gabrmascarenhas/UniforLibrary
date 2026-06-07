package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LibraryHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_home)

        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        // Configurar clique no ícone de perfil do topo (círculo)
        findViewById<View>(R.id.circleProfile)?.setOnClickListener {
            openProfile(isAdmin)
        }

        // Configurar clique no ícone de perfil da Bottom Nav
        findViewById<View>(R.id.nav_perfil_home)?.setOnClickListener {
            openProfile(isAdmin)
        }

        // Configurar clique no UniShop
        findViewById<View>(R.id.nav_unishop_home)?.setOnClickListener {
            val destination = if (isAdmin) {
                LojaCustodioPontosActivity::class.java
            } else {
                TelaLojaCustodioActivity::class.java
            }
            val intent = Intent(this, destination)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }

        // Configurar clique no Reviews
        findViewById<View>(R.id.nav_reviews_home)?.setOnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }

        // Configurar clique no Datas (Calendário)
        findViewById<View>(R.id.nav_datas_home)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }

        // Configurar clique no ícone do Terminal (Brackets)
        findViewById<View>(R.id.circleTerminal)?.setOnClickListener {
            val intent = Intent(this, ChatBoxActivity::class.java)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }

        // Configurar clique no botão Requisitar Livro
        findViewById<Button>(R.id.btnRequisitarLivro)?.setOnClickListener {
            val intent = Intent(this, RequisitarLivroCustodioActivity::class.java)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }
    }

    private fun openProfile(isAdmin: Boolean) {
        val destination = if (isAdmin) perfiladm::class.java else perfiluser::class.java
        val intent = Intent(this, destination)
        intent.putExtra("IS_ADMIN", isAdmin)
        startActivity(intent)
    }

    private fun setupBookClick(viewId: Int, title: String, imageRes: Int, rating: String) {
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        findViewById<View>(viewId)?.setOnClickListener {
            val intent = Intent(this, DetalhesLivroActivity::class.java)
            intent.putExtra("BOOK_TITLE", title)
            intent.putExtra("BOOK_IMAGE", imageRes)
            intent.putExtra("BOOK_RATING", rating)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
        }
    }
}
