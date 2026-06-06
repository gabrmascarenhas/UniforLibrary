package com.example.chatbox

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LibraryHomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_home)

        // Configurar clique no ícone de perfil do topo (círculo)
        val profileIconTop = findViewById<View>(R.id.circleProfile)
        profileIconTop.setOnClickListener {

        }

        // Configurar clique no ícone de perfil da Bottom Nav
        val profileIconBottom = findViewById<View>(R.id.nav_perfil_home)
        profileIconBottom.setOnClickListener {

        }

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
        val terminalIcon = findViewById<View>(R.id.circleTerminal)
        terminalIcon.setOnClickListener {
            val intent = Intent(this, ChatBoxActivity::class.java)
            startActivity(intent)
        }

        // Configurar clique no botão Requisitar Livro
        findViewById<Button>(R.id.btnRequisitarLivro)?.setOnClickListener {
            val intent = Intent(this, RequisitarLivroCustodioActivity::class.java)
            startActivity(intent)
        }

        // Configurar o clique para cada livr

        fun setupBookClick(viewId: Int, title: String, imageRes: Int, rating: String) {
            findViewById<View>(viewId)?.setOnClickListener {
                val intent = Intent(this, DetalhesLivroActivity::class.java)
                intent.putExtra("BOOK_TITLE", title)
                intent.putExtra("BOOK_IMAGE", imageRes)
                intent.putExtra("BOOK_RATING", rating)
                startActivity(intent)
            }
        }

        fun openProfile() {
            val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
            if (isAdmin) {
                val intent = Intent(this, perfiladm::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, perfiluser::class.java)
                startActivity(intent)
            }
        }
    }
}
