package com.example.chatbox

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.launch

class LibraryHomeActivity : AppCompatActivity() {

    private val db = FirebaseDatabase.getInstance().reference
    private val allLivros = mutableListOf<Livro>()
    private lateinit var adapter: HomeLivroAdapter
    private lateinit var rvLivros: RecyclerView

    // Configuração SabedorIA para Pesquisa Inteligente
    // IMPORTANTE: Insira sua Gemini API Key aqui
    private val API_KEY = "SUA_API_KEY_AQUI" 
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = API_KEY,
        systemInstruction = content {
            text("Você é o SabedorIA, o bibliotecário virtual da Unifor Library. Responda de forma curta e inteligente sobre livros.")
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_home)

        rvLivros = findViewById(R.id.rvLivrosHome)
        rvLivros.layoutManager = GridLayoutManager(this, 3)
        adapter = HomeLivroAdapter(mutableListOf()) { livro ->
            showAccessBookDialog(livro)
        }
        rvLivros.adapter = adapter

        carregarLivrosDoBanco()
        setupNavegacao()
        setupPesquisaInteligente()
    }

    private fun carregarLivrosDoBanco() {
        db.child("livros").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allLivros.clear()
                for (postSnapshot in snapshot.children) {
                    val livro = postSnapshot.getValue(Livro::class.java)
                    if (livro != null) allLivros.add(livro)
                }
                adapter.updateList(allLivros)
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LibraryHomeActivity, "Erro ao carregar banco", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupPesquisaInteligente() {
        val etSearch = findViewById<EditText>(R.id.etSearch)
        val btnSmartSearch = findViewById<ImageButton>(R.id.btnSmartSearch)

        // Filtro em tempo real na RecyclerView
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().lowercase().trim()
                val filtered = allLivros.filter { 
                    it.titulo.lowercase().contains(query) || it.autor.lowercase().contains(query)
                }
                adapter.updateList(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Pesquisa "viajando pelo banco" + Resposta IA
        btnSmartSearch?.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                if (API_KEY == "SUA_API_KEY_AQUI") {
                    Toast.makeText(this, "Configure sua GEMINI API KEY no código da LibraryHomeActivity", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                processarPesquisaIA(query)
            }
        }
    }

    private fun processarPesquisaIA(query: String) {
        val livroRelacionado = allLivros.find { 
            it.titulo.contains(query, ignoreCase = true) || it.autor.contains(query, ignoreCase = true) 
        }
        
        lifecycleScope.launch {
            val prompt = if (livroRelacionado != null) {
                "O usuário pesquisou por '$query' e encontrei o livro '${livroRelacionado.titulo}' no banco. Dados: Autor ${livroRelacionado.autor}, Sinopse: ${livroRelacionado.sinopse}. Responda como SabedorIA motivando-o a ler."
            } else {
                "O usuário pesquisou por '$query'. Não encontrei esse livro exato no banco. Responda como SabedorIA sugerindo algo literário ou peça para ser mais específico."
            }

            try {
                val response = generativeModel.generateContent(prompt)
                showSmartResponseDialog(response.text ?: "O SabedorIA está buscando inspiração...")
            } catch (e: Exception) {
                Toast.makeText(this@LibraryHomeActivity, "Erro ao conectar ao SabedorIA", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSmartResponseDialog(resposta: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_sabedoria_response)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        val tvContent = dialog.findViewById<TextView>(R.id.tvSabedoriaContent)
        val btnFechar = dialog.findViewById<Button>(R.id.btnFecharSabedoria)
        
        tvContent?.text = resposta
        btnFechar?.setOnClickListener { dialog.dismiss() }
        
        dialog.show()
    }

    private fun setupNavegacao() {
        findViewById<View>(R.id.circleProfile).setOnClickListener { openProfile() }
        findViewById<View>(R.id.nav_perfil_home).setOnClickListener { openProfile() }
        findViewById<View>(R.id.circleTerminal).setOnClickListener {
            startActivity(Intent(this, ChatBoxActivity::class.java))
        }
        findViewById<View>(R.id.nav_unishop_home)?.setOnClickListener {
            startActivity(Intent(this, TelaLojaCustodioActivity::class.java))
        }
        findViewById<View>(R.id.nav_reviews_home)?.setOnClickListener {
            startActivity(Intent(this, ReviewsActivity::class.java))
        }
        findViewById<View>(R.id.nav_datas_home)?.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
        findViewById<Button>(R.id.btnRequisitarLivro)?.setOnClickListener {
            startActivity(Intent(this, RequisitarLivroCustodioActivity::class.java))
        }
    }

    private fun openProfile() {
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        val activity = if (isAdmin) perfiladm::class.java else perfiluser::class.java
        startActivity(Intent(this, activity))
    }

    private fun showAccessBookDialog(livro: Livro) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_access_book)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.findViewById<Button>(R.id.btnYes).setOnClickListener {
            val intent = Intent(this, DetalhesLivroActivity::class.java)
            intent.putExtra("LIVRO_ID", livro.id)
            startActivity(intent)
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.btnNo).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}