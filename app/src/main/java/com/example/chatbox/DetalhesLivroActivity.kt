package com.example.chatbox

import android.app.Dialog
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class DetalhesLivroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes_livro)

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

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {
            // Lógica para salvar a avaliação
            dialog.dismiss()
        }

        dialog.show()
    }
}
