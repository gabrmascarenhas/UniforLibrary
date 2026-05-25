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
            openProfile()
        }

        // Configurar clique no ícone de perfil da Bottom Nav
        val profileIconBottom = findViewById<View>(R.id.nav_perfil_home)
        profileIconBottom.setOnClickListener {
            openProfile()
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

        // Configurar o clique para cada livro
        setupBookClick(R.id.book_norma, "Teoria da Norma Jurídica", R.drawable.book_norma, "4,8/5")
        setupBookClick(R.id.book_legado, "Código Legado", R.drawable.book_legado, "4,6/5")
        setupBookClick(R.id.book_sobotta, "Sobotta", R.drawable.book_sobotta, "4,9/5")
        setupBookClick(R.id.book_sentido, "Em Busca de Sentido", R.drawable.book_sentido, "4,7/5")
        setupBookClick(R.id.book_red_center, "O Homem e Seus Símbolos", R.drawable.book_red_center, "4,5/5")
        setupBookClick(R.id.book_crime, "Crime e Castigo", R.drawable.book_crime, "4,8/5")
        setupBookClick(R.id.book_noite_taverna, "Noite na Taverna", R.drawable.noite_na_taverna, "4,3/5")
        setupBookClick(R.id.book_jazz, "Jazz", R.drawable.book_jazz, "4,4/5")
        setupBookClick(R.id.book_lines, "Lines", R.drawable.book_81h, "4,2/5")
        setupBookClick(R.id.book_expressionismo, "Expressionismo", R.drawable.book_expressionismo, "4,5/5")
        setupBookClick(R.id.book_teologia, "A Teologia do Livro de Apocalipse", R.drawable.book_teologia, "4,6/5")
        setupBookClick(R.id.book_dadaismo, "Dadaismo", R.drawable.book_41v, "4,4/5")
    }

    private fun setupBookClick(viewId: Int, title: String, imageRes: Int, rating: String) {
        findViewById<View>(viewId)?.setOnClickListener {
            val intent = Intent(this, DetalhesLivroActivity::class.java)
            intent.putExtra("BOOK_TITLE", title)
            intent.putExtra("BOOK_IMAGE", imageRes)
            intent.putExtra("BOOK_RATING", rating)
            startActivity(intent)
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
}
