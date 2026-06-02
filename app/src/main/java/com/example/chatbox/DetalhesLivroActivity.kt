package com.example.chatbox

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
<<<<<<< Updated upstream
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
=======
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
>>>>>>> Stashed changes

class DetalhesLivroActivity : AppCompatActivity() {

    private val repositorio = RepositorioLivros()
    private var livroId: String = "livro_1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_livro)

<<<<<<< Updated upstream
        val tvTitle = findViewById<TextView>(R.id.tvBookTitleDetailed)
        val ivCover = findViewById<ImageView>(R.id.ivBookCoverDetailed)
        val tvRating = findViewById<TextView>(R.id.tvRatingDetailed)

        // Recebe os dados do Intent
        val bookTitle = intent.getStringExtra("BOOK_TITLE") ?: "Livro"
        val bookImage = intent.getIntExtra("BOOK_IMAGE", R.drawable.noite_na_taverna)
        val bookRating = intent.getStringExtra("BOOK_RATING") ?: "4,5/5"

        tvTitle.text = bookTitle
        ivCover.setImageResource(bookImage)
        tvRating.text = bookRating
=======
        livroId = intent.getStringExtra("LIVRO_ID") ?: "livro_1"
>>>>>>> Stashed changes

        val ivDeleteDetailed = findViewById<ImageView>(R.id.ivDeleteDetailed)
        ivDeleteDetailed.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        val btnDetails = findViewById<Button>(R.id.btnDetails)
        btnDetails.setOnClickListener {
            showBookInfoDialog()
        }

        val btnEvaluate = findViewById<Button>(R.id.btnEvaluate)
        btnEvaluate.setOnClickListener {
            showBookEvaluationDialog()
        }

        val ivClose = findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener {
            finish()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_confirm_delete)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCancel = dialog.findViewById<Button>(R.id.btnCancelDelete)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirmDelete)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnConfirm.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBookInfoDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_book_info)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnClose = dialog.findViewById<ImageView>(R.id.btnCloseInfo)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showBookEvaluationDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_book_evaluation)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnClose = dialog.findViewById<ImageView>(R.id.btnCloseEval)
        val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmitEval)
<<<<<<< Updated upstream
        val etComment = dialog.findViewById<EditText>(R.id.etCommentEval)
=======
        val rbStars = dialog.findViewById<RatingBar>(R.id.rbStarsEval)
>>>>>>> Stashed changes

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {
<<<<<<< Updated upstream
            val comment = etComment.text.toString()
            val user = FirebaseAuth.getInstance().currentUser
            val bookTitle = findViewById<TextView>(R.id.tvBookTitleDetailed).text.toString()

            if (user != null && comment.isNotEmpty()) {
                val evaluation = hashMapOf(
                    "userId" to user.uid,
                    "userEmail" to user.email,
                    "bookTitle" to bookTitle,
                    "comment" to comment,
                    "timestamp" to com.google.firebase.Timestamp.now()
                )

                FirebaseFirestore.getInstance().collection("evaluations")
                    .add(evaluation)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Avaliação salva com sucesso!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Erro ao salvar avaliação", e)
                        Toast.makeText(this, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Por favor, escreva um comentário", Toast.LENGTH_SHORT).show()
=======
            val nota = rbStars.rating.toInt()
            if (nota in 1..5) {
                salvarAvaliacao(nota)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Selecione uma nota entre 1 e 5", Toast.LENGTH_SHORT).show()
>>>>>>> Stashed changes
            }
        }

        dialog.show()
    }

    private fun salvarAvaliacao(nota: Int) {
        lifecycleScope.launch {
            try {
                repositorio.atualizarAvaliacao(livroId, nota.toFloat()) // Use Float se o modelo usar Float
                Toast.makeText(this@DetalhesLivroActivity, "Avaliação salva com sucesso!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@DetalhesLivroActivity, "Erro ao salvar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
