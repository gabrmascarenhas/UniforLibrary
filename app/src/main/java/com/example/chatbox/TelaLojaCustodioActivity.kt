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
import androidx.lifecycle.lifecycleScope
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TelaLojaCustodioActivity : AppCompatActivity() {

    private val repositorioLoja = RepositorioLoja()
    
    // URL DEFINIDA MANUALMENTE PARA EVITAR ERRO DE CONEXÃO
    private val databaseUrl = "https://uniforlibrary-30c5c-default-rtdb.firebaseio.com/"
    private val db = Firebase.database(databaseUrl).reference
    private val auth = Firebase.auth
    private var pontosAtuais: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_loja_custodio)

        setupNavigation()
        carregarDadosIniciais()
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.nav_home_loja)?.setOnClickListener { finish() }
        findViewById<View>(R.id.nav_perfil_loja)?.setOnClickListener {
            startActivity(Intent(this, perfiluser::class.java))
        }
        findViewById<View>(R.id.nav_reviews_loja)?.setOnClickListener {
            startActivity(Intent(this, ReviewsActivity::class.java))
        }
        findViewById<View>(R.id.nav_data_loja)?.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
    }

    private fun carregarDadosIniciais() {
        carregarPontosDoUsuario()
        carregarItensDaLoja()
    }

    private fun carregarPontosDoUsuario() {
        val userId = auth.currentUser?.uid ?: return
        lifecycleScope.launch {
            try {
                val snapshot = db.child("users").child(userId).child("pontos").get().await()
                pontosAtuais = snapshot.getValue(Int::class.java) ?: 0
                atualizarExibicaoPontos(pontosAtuais)
            } catch (e: Exception) {
                Toast.makeText(this@TelaLojaCustodioActivity, "Erro ao carregar pontos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun atualizarExibicaoPontos(pontos: Int) {
        findViewById<TextView>(R.id.tvPontosValor).text = "$pontos pontos!"
    }

    private fun carregarItensDaLoja() {
        val container = findViewById<LinearLayout>(R.id.containerItens)
        lifecycleScope.launch {
            try {
                val itens = repositorioLoja.obterItens()
                container.removeAllViews()
                
                if (itens.isEmpty()) {
                    val emptyView = TextView(this@TelaLojaCustodioActivity).apply {
                        text = "Nenhum item disponível."
                        setTextColor(Color.WHITE)
                        gravity = android.view.Gravity.CENTER
                        setPadding(0, 50, 0, 0)
                    }
                    container.addView(emptyView)
                    return@launch
                }

                // Ordenar itens por valor de forma decrescente (do mais caro para o mais barato)
                val itensOrdenados = itens.sortedByDescending { 
                    it.pontos.filter { char -> char.isDigit() }.toIntOrNull() ?: 0 
                }
                
                itensOrdenados.forEach { item ->
                    val itemView = LayoutInflater.from(this@TelaLojaCustodioActivity)
                        .inflate(R.layout.item_loja_venda, container, false)
                    
                    val btnItem = itemView.findViewById<Button>(R.id.btnItemLoja)
                    btnItem.text = "${item.nome} - ${item.pontos}"
                    
                    btnItem.setOnClickListener {
                        showPurchaseConfirmationDialog(item)
                    }
                    
                    container.addView(itemView)
                }
            } catch (e: Exception) {
                Toast.makeText(this@TelaLojaCustodioActivity, "Erro ao carregar loja", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPurchaseConfirmationDialog(item: PontoItemCustodio) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_confirm_purchase)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.findViewById<TextView>(R.id.tvTitle)?.text = "Confirmar compra?"
        dialog.findViewById<TextView>(R.id.tvMessage)?.text = "Deseja comprar ${item.nome} por ${item.pontos}?"

        dialog.findViewById<Button>(R.id.btnConfirm).setOnClickListener {
            val precoItem = item.pontos.filter { it.isDigit() }.toIntOrNull() ?: 0
            
            if (pontosAtuais >= precoItem) {
                efetuarCompra(item, precoItem)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Pontos insuficientes!", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.findViewById<ImageView>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun efetuarCompra(item: PontoItemCustodio, preco: Int) {
        val userId = auth.currentUser?.uid ?: return
        val novosPontos = pontosAtuais - preco

        lifecycleScope.launch {
            try {
                db.child("users").child(userId).child("pontos").setValue(novosPontos).await()
                
                pontosAtuais = novosPontos
                atualizarExibicaoPontos(pontosAtuais)

                showPurchaseSuccessDialog()
            } catch (e: Exception) {
                Toast.makeText(this@TelaLojaCustodioActivity, "Erro ao processar compra", Toast.LENGTH_SHORT).show()
            }
        }
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
