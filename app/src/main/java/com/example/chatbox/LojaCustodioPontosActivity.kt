package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class LojaCustodioPontosActivity : AppCompatActivity() {
    
    // URL padronizada (removida a barra final para evitar erros de permissão)
    private val databaseUrl = "https://uniforlibrary-30c5c-default-rtdb.firebaseio.com"
    private val db = Firebase.database(databaseUrl).reference
    private val auth = Firebase.auth
    private lateinit var adapter: PontosAdapterCustodio
    private val listaItens = mutableListOf<PontoItemCustodio>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loja_custodio_pontos)

        setupNavigation()

        findViewById<View>(R.id.btn_adicionar)?.setOnClickListener {
            showAdicionarItemDialog()
        }

        val rv = findViewById<RecyclerView>(R.id.rvPontos)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = PontosAdapterCustodio(listaItens) { item ->
            deletarItemDoFirebase(item.id)
        }
        rv.adapter = adapter

        // Verificar se o usuário está logado antes de carregar
        if (auth.currentUser == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        } else {
            carregarItensDoFirebase()
        }
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.nav_home_pontos)?.setOnClickListener {
            val intent = Intent(this, LibraryHomeActivity::class.java)
            intent.putExtra("IS_ADMIN", true)
            startActivity(intent)
            finish()
        }
        findViewById<View>(R.id.nav_perfil_pontos)?.setOnClickListener {
            startActivity(Intent(this, perfiladm::class.java))
        }
        findViewById<View>(R.id.nav_reviews_pontos)?.setOnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            intent.putExtra("IS_ADMIN", true)
            startActivity(intent)
        }
        findViewById<View>(R.id.nav_datas_pontos)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            intent.putExtra("IS_ADMIN", true)
            startActivity(intent)
        }
    }

    private fun carregarItensDoFirebase() {
        // Garantindo que o caminho 'loja' bata com a regra do Firebase
        db.child("loja").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaItens.clear()
                for (postSnapshot in snapshot.children) {
                    val item = postSnapshot.getValue(PontoItemCustodio::class.java)
                    if (item != null) {
                        val itemComId = item.copy(id = postSnapshot.key ?: "")
                        listaItens.add(itemComId)
                    }
                }
                listaItens.sortByDescending { it.pontos.filter { char -> char.isDigit() }.toIntOrNull() ?: 0 }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Aqui aparecerá a mensagem real se a regra falhar
                Toast.makeText(this@LojaCustodioPontosActivity, "Falha: ${error.message}", Toast.LENGTH_LONG).show()
                Log.e("FirebaseLoja", "Erro de permissão ou conexão: ${error.message}")
            }
        })
    }

    private fun showAdicionarItemDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_adicionar_item, null)
        val etNome = dialogView.findViewById<EditText>(R.id.etNomeItem)
        val etValor = dialogView.findViewById<EditText>(R.id.etValorPonto)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Adicionar") { _, _ ->
                val nome = etNome.text.toString()
                val valor = etValor.text.toString()
                if (nome.isNotEmpty() && valor.isNotEmpty()) {
                    salvarItemNoFirebase(nome, "$valor pontos")
                } else {
                    Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun salvarItemNoFirebase(nome: String, pontos: String) {
        val id = db.child("loja").push().key ?: return
        val item = PontoItemCustodio(id, nome, pontos)
        db.child("loja").child(id).setValue(item)
            .addOnSuccessListener {
                Toast.makeText(this, "Item salvo!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar (Verifique se é Admin nas Regras): ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun deletarItemDoFirebase(id: String) {
        if (id.isEmpty()) return
        db.child("loja").child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Item removido!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao remover: ${e.message}", Toast.LENGTH_LONG).show()
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
        holder.btnDelete.setOnClickListener { onDeleteClick(item) }
    }

    override fun getItemCount() = lista.size
}
