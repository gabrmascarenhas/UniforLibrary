package com.example.chatbox

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class TelaLojaCustodioActivity : AppCompatActivity() {

    private val REQUEST_NOTIFICATION_PERMISSION = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_loja_custodio)

        // Criar o canal de notificação
        NotificationHelper.createNotificationChannel(this)
        
        // Pedir permissão no Android 13+
        checkNotificationPermission()

        // Botão Home na Bottom Nav da Loja
        findViewById<View>(R.id.nav_home_loja)?.setOnClickListener {
            finish()
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

        setupStoreItems()
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_NOTIFICATION_PERMISSION)
            }
        }
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
            
            val userName = UserManager.userName ?: "Usuário"
            val userMatricula = UserManager.userMatricula ?: "N/A"
            
            // Disparar a notificação
            NotificationHelper.showNotification(
                this,
                "Nova Compra Realizada",
                "Usuário: $userName | Matrícula: $userMatricula"
            )

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