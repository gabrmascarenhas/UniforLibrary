package com.example.chatbox

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class TelaLojaCustodioActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_loja_custodio)

        // Botão Home na Bottom Nav da Loja
        findViewById<View>(R.id.nav_home_loja)?.setOnClickListener {
            finish() // Volta para a tela anterior (geralmente a Home)
        }

        findViewById<View>(R.id.nav_reviews_loja)?.setOnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_perfil_loja)?.setOnClickListener {
            // Implementação futura para perfil
        }

        findViewById<View>(R.id.nav_data_loja)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        // Configurar cliques nos itens da loja
        setupStoreItems()
    }

    private fun setupStoreItems() {
        val items = listOf(
            R.id.btnMochila,
            R.id.btnCamisa,
            R.id.btnCaneca,
            R.id.btnComprarLivro,
            R.id.btnDigitalizarLivro,
            R.id.btnCafe
        )

        items.forEach { id ->
            findViewById<View>(id)?.setOnClickListener {
                showPurchaseConfirmationDialog()
            }
        }
    }

    private fun showPurchaseConfirmationDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_confirm_purchase)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        val btnClose = dialog.findViewById<ImageView>(R.id.btnClose)

        btnConfirm.setOnClickListener {
            dialog.dismiss()
            showPurchaseSuccessDialog()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showPurchaseSuccessDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_purchase_success)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnClose = dialog.findViewById<ImageView>(R.id.btnCloseSuccess)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}