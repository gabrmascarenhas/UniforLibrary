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

        // Lista de IDs dos FrameLayouts dos livros no XML
        val bookIds = listOf(
            R.id.book_norma, R.id.book_legado, R.id.book_sobotta,
            R.id.book_sentido, R.id.book_red_center, R.id.book_crime,
            R.id.book_noite_taverna, R.id.book_jazz, R.id.book_lines,
            R.id.book_expressionismo, R.id.book_teologia, R.id.book_dadaismo
        )

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

    private fun showAccessBookDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_access_book)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYes = dialog.findViewById<Button>(R.id.btnYes)
        val btnNo = dialog.findViewById<Button>(R.id.btnNo)

        btnYes.setOnClickListener {
            val intent = Intent(this, DetalhesLivroActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
