package com.example.chatbox

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class LojaCustodioPontosActivity : AppCompatActivity() {
    
    private val repositorioLoja = RepositorioLoja()
    private lateinit var adapter: PontosAdapterCustodio
    private val listaItens = mutableListOf<PontoItemCustodio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loja_custodio_pontos)

        configurarNavegacao()
        configurarRecyclerView()
        carregarItens()

        findViewById<View>(R.id.btn_adicionar)?.setOnClickListener {
            showAddItemDialog()
        }
    }

    private fun configurarNavegacao() {
        findViewById<View>(R.id.nav_datas_pontos)?.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
        
        findViewById<View>(R.id.nav_home_pontos)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.nav_perfil_pontos)?.setOnClickListener {
            startActivity(Intent(this, perfiladm::class.java))
        }
    }

    private fun configurarRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rvPontos)
        adapter = PontosAdapterCustodio(listaItens) { item ->
            removerItemLoja(item)
        }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    private fun carregarItens() {
        repositorioLoja.observarItens { itens ->
            listaItens.clear()
            listaItens.addAll(itens)
            adapter.notifyDataSetChanged()
        }
    }

    private fun showAddItemDialog() {
        val dialog = Dialog(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_add_item_loja, null)
        dialog.setContentView(view)
        
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val etNome = view.findViewById<EditText>(R.id.etNomeItem)
        val etPontos = view.findViewById<EditText>(R.id.etPontosItem)
        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmar)
        val btnCancelar = view.findViewById<Button>(R.id.btnCancelar)

        btnCancelar.setOnClickListener { dialog.dismiss() }

        btnConfirmar.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val pontos = etPontos.text.toString().trim()

            if (nome.isEmpty() || pontos.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val sucesso = repositorioLoja.adicionarItem(nome, pontos)
                if (sucesso) {
                    Toast.makeText(this@LojaCustodioPontosActivity, "Item adicionado!", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    Toast.makeText(this@LojaCustodioPontosActivity, "Erro ao salvar item", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun removerItemLoja(item: PontoItemCustodio) {
        lifecycleScope.launch {
            val sucesso = repositorioLoja.removerItem(item.id)
            if (sucesso) {
                Toast.makeText(this@LojaCustodioPontosActivity, "Item removido!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@LojaCustodioPontosActivity, "Erro ao remover item", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

class PontosAdapterCustodio(
    private val lista: List<PontoItemCustodio>,
    private val onDeleteClick: (PontoItemCustodio) -> Unit
) : RecyclerView.Adapter<PontosAdapterCustodio.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.tvNomeItem)
        val pontos: TextView = view.findViewById(R.id.tvValorPonto)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ponto_custodio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.nome.text = item.nome
        holder.pontos.text = item.pontos
        
        holder.btnDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = lista.size
}
