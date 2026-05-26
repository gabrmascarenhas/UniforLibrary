package com.example.chatbox

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class DetalhesLivroActivity : AppCompatActivity() {

    private val repositorioLivros = RepositorioLivros()
    private var livroId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_livro)

        livroId = intent.getStringExtra("LIVRO_ID")

        if (livroId != null) {
            carregarDetalhesDoLivro(livroId!!)
        }

        findViewById<ImageView>(R.id.ivDeleteDetailed).setOnClickListener {
            showDeleteConfirmationDialog()
        }

        findViewById<Button>(R.id.btnDetails).setOnClickListener {
            showBookInfoDialog()
        }

        findViewById<Button>(R.id.btnEvaluate).setOnClickListener {
            showBookEvaluationDialog()
        }

        findViewById<ImageView>(R.id.ivClose).setOnClickListener {
            finish()
        }
    }

    private fun carregarDetalhesDoLivro(id: String) {
        lifecycleScope.launch {
            try {
                val livro = repositorioLivros.obterLivro(id)
                if (livro != null) {
                    findViewById<TextView>(R.id.tvBookTitleDetailed).text = livro.titulo
                    
                    // Fix: Directly find the ImageView by its ID from the layout.
                    // The error was caused by incorrectly casting CardView to ImageView and calling getChildAt().
                    val ivCover = findViewById<ImageView>(R.id.ivBookCoverDetails)
                    
                    ivCover?.let {
                        Glide.with(this@DetalhesLivroActivity)
                            .load(livro.capUrl)
                            .placeholder(R.drawable.noite_na_taverna)
                            .into(it)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetalhesLivroActivity, "Erro ao carregar livro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_confirm_delete)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.findViewById<Button>(R.id.btnCancelDelete).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<Button>(R.id.btnConfirmDelete).setOnClickListener {
            lifecycleScope.launch {
                livroId?.let { repositorioLivros.removerLivro(it) }
                dialog.dismiss()
                finish()
            }
        }
        dialog.show()
    }

    private fun showBookInfoDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_book_info)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.findViewById<ImageView>(R.id.btnCloseInfo).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun showBookEvaluationDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_book_evaluation)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.findViewById<ImageView>(R.id.btnCloseEval).setOnClickListener { dialog.dismiss() }
        dialog.findViewById<Button>(R.id.btnSubmitEval).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
