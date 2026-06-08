package com.example.chatbox

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

class DetalhesLivroActivity : AppCompatActivity() {

    private val repositorio = RepositorioLivros()
    private var livroAtual: Livro? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_livro)

        val livroId = intent.getStringExtra("LIVRO_ID") ?: ""
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)

        if (livroId.isNotEmpty()) {
            carregarDetalhes(livroId)
        }

        // Configura visibilidade do botão de deletar baseado no admin
        val ivDeleteDetailed = findViewById<ImageView>(R.id.ivDeleteDetailed)
        ivDeleteDetailed.visibility = if (isAdmin) View.VISIBLE else View.GONE
        
        ivDeleteDetailed.setOnClickListener {
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

    private fun carregarDetalhes(id: String) {
        lifecycleScope.launch {
            try {
                val livro = repositorio.buscarLivroPorId(id)
                if (livro != null) {
                    livroAtual = livro
                    exibirDados(livro)
                }
            } catch (ignored: Exception) {
                Toast.makeText(this@DetalhesLivroActivity, getString(R.string.load_book_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun exibirDados(livro: Livro) {
        findViewById<TextView>(R.id.tvBookTitleDetailed).text = livro.titulo
        val ivCapa = findViewById<ImageView>(R.id.ivBookCoverDetailed)
        val tvRating = findViewById<TextView>(R.id.tvBookRatingDetailed)

        lifecycleScope.launch {
            val media = repositorio.buscarMediaLivro(livro.id)
            tvRating.text = String.format("%.1f", media)
        }

        if (livro.capUrl.isNotEmpty()) {
            Glide.with(this)
                .load(livro.capUrl)
                .placeholder(R.drawable.image_icon)
                .error(R.drawable.image_icon)
                .into(ivCapa)
        }
    }

    private fun showDeleteConfirmationDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_confirm_delete)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCancel = dialog.findViewById<Button>(R.id.btnCancelDelete)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirmDelete)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnConfirm.setOnClickListener {
            livroAtual?.let {
                lifecycleScope.launch {
                    try {
                        repositorio.removerLivro(it.id)
                        Toast.makeText(this@DetalhesLivroActivity, getString(R.string.book_removed), Toast.LENGTH_SHORT).show()
                        finish()
                    } catch (ignored: Exception) {
                        Toast.makeText(this@DetalhesLivroActivity, getString(R.string.error_removing), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBookInfoDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_book_info)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        livroAtual?.let {
            dialog.findViewById<TextView>(R.id.tvInfoTitle)?.text = it.titulo
            dialog.findViewById<TextView>(R.id.tvInfoAuthor)?.text = getString(R.string.author_format, it.autor)
            dialog.findViewById<TextView>(R.id.tvInfoPublisher)?.text = getString(R.string.publisher_format, it.editora)
            dialog.findViewById<TextView>(R.id.tvInfoYear)?.text = getString(R.string.year_format, it.ano)
            dialog.findViewById<TextView>(R.id.tvInfoSinopse)?.text = it.sinopse
        }

        dialog.findViewById<ImageView>(R.id.btnCloseInfo).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBookEvaluationDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_book_evaluation)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val livro = livroAtual ?: return

        // Preencher dados do livro no dialog
        val ivCover = dialog.findViewById<ImageView>(R.id.ivEvalBookCover)
        val tvTitle = dialog.findViewById<TextView>(R.id.tvEvalBookTitle)
        val tvAuthor = dialog.findViewById<TextView>(R.id.tvEvalBookAuthor)
        val tvRating = dialog.findViewById<TextView>(R.id.tvEvalBookRating)

        tvTitle.text = "“${livro.titulo}”"
        tvAuthor.text = livro.autor
        
        lifecycleScope.launch {
            val media = repositorio.buscarMediaLivro(livro.id)
            tvRating.text = String.format("%.1f/5", media)
        }

        if (livro.capUrl.isNotEmpty()) {
            Glide.with(this).load(livro.capUrl).into(ivCover)
        }

        // Lógica das estrelas
        var notaSelecionada = 0
        val stars = listOf(
            dialog.findViewById<ImageView>(R.id.ivStar1),
            dialog.findViewById<ImageView>(R.id.ivStar2),
            dialog.findViewById<ImageView>(R.id.ivStar3),
            dialog.findViewById<ImageView>(R.id.ivStar4),
            dialog.findViewById<ImageView>(R.id.ivStar5)
        )

        fun updateStars(rating: Int) {
            notaSelecionada = rating
            for (i in stars.indices) {
                val color = if (i < rating) "#FFD700" else "#FFFFFF" // Gold or White
                stars[i].setColorFilter(android.graphics.Color.parseColor(color))
            }
        }

        stars.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                updateStars(index + 1)
            }
        }

        dialog.findViewById<ImageView>(R.id.btnCloseEval).setOnClickListener {
            dialog.dismiss()
        }

        val etComment = dialog.findViewById<EditText>(R.id.etCommentEval)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmitEval)

        btnSubmit.setOnClickListener {
            val comentario = etComment.text.toString()
            if (notaSelecionada == 0) {
                Toast.makeText(this, "Por favor, selecione uma nota", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val avaliacao = Avaliacao(
                        livroId = livro.id,
                        nota = notaSelecionada,
                        comentario = comentario
                    )
                    repositorio.salvarAvaliacao(avaliacao)
                    Toast.makeText(this@DetalhesLivroActivity, "Avaliação enviada com sucesso!", Toast.LENGTH_SHORT).show()
                    
                    // Atualiza a média na tela principal após avaliar
                    exibirDados(livro)

                    dialog.dismiss()
                } catch (e: Exception) {
                    Toast.makeText(this@DetalhesLivroActivity, "Erro ao salvar avaliação: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }
}
