package com.example.chatbox

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetalhesLivroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_livro)

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
        val etComment = dialog.findViewById<EditText>(R.id.etCommentEval)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {
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
            }
        }

        dialog.show()
    }
}
