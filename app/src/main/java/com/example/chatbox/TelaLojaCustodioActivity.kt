package com.example.chatbox

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TelaLojaCustodioActivity : AppCompatActivity() {

    private val REQUEST_NOTIFICATION_PERMISSION = 1001
    private val repositorioLoja = RepositorioLoja()
    private val db = Firebase.database.reference
    private val auth = Firebase.auth
    private var pontosAtuais: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_loja_custodio)

        // Funcionalidade da branch antiga
        NotificationHelper.createNotificationChannel(this)
        checkNotificationPermission()

        findViewById<View>(R.id.nav_home_loja)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.nav_perfil_loja)?.setOnClickListener {
            startActivity(Intent(this, perfiluser::class.java))
        }

        findViewById<View>(R.id.nav_reviews_loja)?.setOnClickListener {
            startActivity(Intent(this, ReviewsActivity::class.java))
        }

        findViewById<View>(R.id.nav_data_loja)?.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        carregarDadosIniciais()

        // Compatibilidade com layouts antigos
        setupStoreItemsFallback()
    }

    private fun carregarDadosIniciais() {
        carregarPontosDoUsuario()
        carregarItensDaLoja()
    }

    private fun carregarPontosDoUsuario() {
        val userId = auth.currentUser?.uid ?: return

        lifecycleScope.launch {
            try {
                val snapshot = db.child("users")
                    .child(userId)
                    .get()
                    .await()

                pontosAtuais = snapshot.child("pontos").getValue(Int::class.java) ?: 0
                atualizarExibicaoPontos(pontosAtuais)

                // Preserva e atualiza os dados do usuário para as notificações
                UserManager.userName = snapshot.child("nome").getValue(String::class.java)
                UserManager.userMatricula = snapshot.child("matricula").getValue(String::class.java)

            } catch (e: Exception) {
                pontosAtuais = 0
                atualizarExibicaoPontos(0)
            }
        }
    }

    private fun atualizarExibicaoPontos(pontos: Int) {
        findViewById<TextView>(R.id.tvPontosValor)?.text = "$pontos pontos!"
        findViewById<TextView>(R.id.tvPontosHeader)?.text = "$pontos pts"
    }

    private fun carregarItensDaLoja() {
        val container = findViewById<LinearLayout>(R.id.containerItens) ?: return

        lifecycleScope.launch {
            try {
                val itens = repositorioLoja.obterItens()

                // Se houver itens na loja dinâmica, removemos os fallbacks estáticos
                if (itens.isNotEmpty()) {
                    container.removeAllViews()

                    itens.forEach { item ->
                        val itemView = LayoutInflater.from(this@TelaLojaCustodioActivity)
                            .inflate(R.layout.item_loja_venda, container, false)

                        val btnItem = itemView.findViewById<Button>(R.id.btnItemLoja)
                        btnItem.text = "${item.nome} - ${item.pontos}"
                        btnItem.setOnClickListener {
                            showPurchaseConfirmationDialog(item)
                        }
                        container.addView(itemView)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@TelaLojaCustodioActivity, "Erro ao carregar loja: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Compatibilidade com a branch antiga.
     * Caso o XML ainda possua os botões antigos.
     */
    private fun setupStoreItemsFallback() {

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

                Toast.makeText(
                    this,
                    "Este item agora é carregado pela loja dinâmica.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_NOTIFICATION_PERMISSION
                )
            }
        }
    }

    private fun showPurchaseConfirmationDialog(item: PontoItemCustodio) {

        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_confirm_purchase)

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)
        val btnClose = dialog.findViewById<ImageView>(R.id.btnClose)

        dialog.findViewById<TextView>(R.id.tvTitle)?.text =
            "Confirmar compra?"

        dialog.findViewById<TextView>(R.id.tvMessage)?.text =
            "Deseja comprar ${item.nome} por ${item.pontos}?"

        btnConfirm.setOnClickListener {

            val precoItem =
                item.pontos
                    .filter { it.isDigit() }
                    .toIntOrNull()
                    ?: 0

            if (pontosAtuais >= precoItem) {

                efetuarCompra(item, precoItem)

                dialog.dismiss()

            } else {

                Toast.makeText(
                    this,
                    "Pontos insuficientes!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun efetuarCompra(
        item: PontoItemCustodio,
        preco: Int
    ) {

        val userId = auth.currentUser?.uid ?: return

        val novosPontos = pontosAtuais - preco

        lifecycleScope.launch {

            try {

                db.child("users")
                    .child(userId)
                    .child("pontos")
                    .setValue(novosPontos)
                    .await()

                pontosAtuais = novosPontos

                atualizarExibicaoPontos(pontosAtuais)

                val userName =
                    UserManager.userName ?: "Usuário"

                val userMatricula =
                    UserManager.userMatricula ?: "N/A"

                NotificationHelper.showNotification(
                    this@TelaLojaCustodioActivity,
                    "Nova Compra Realizada",
                    "Item: ${item.nome} | Usuário: $userName | Matrícula: $userMatricula"
                )

                showPurchaseSuccessDialog()

            } catch (e: Exception) {

                Toast.makeText(
                    this@TelaLojaCustodioActivity,
                    "Erro ao processar compra",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showPurchaseSuccessDialog() {

        val dialog = Dialog(this)

        dialog.setContentView(
            R.layout.dialog_purchase_success
        )

        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        dialog.findViewById<ImageView>(
            R.id.btnCloseSuccess
        )?.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()
    }
}