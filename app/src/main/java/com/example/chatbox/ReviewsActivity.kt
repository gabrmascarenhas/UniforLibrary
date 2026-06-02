package com.example.chatbox

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
<<<<<<< Updated upstream
import android.widget.ImageView
import android.widget.TextView
=======
>>>>>>> Stashed changes
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
<<<<<<< Updated upstream
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class ReviewsActivity : AppCompatActivity() {

    private lateinit var adapter: SearchReviewAdapter
    private var allEvaluations = listOf<Map<String, Any>>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
=======
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ReviewsActivity : AppCompatActivity() {

    private lateinit var adapter: LivroAvaliadoAdapter
    private val database = FirebaseDatabase.getInstance().reference
>>>>>>> Stashed changes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

<<<<<<< Updated upstream
        val etSearch = findViewById<EditText>(R.id.etSearch)
        val rvResults = findViewById<RecyclerView>(R.id.rvSearchResults)
        val gridDefault = findViewById<View>(R.id.gridBooksDefault)

        rvResults.layoutManager = LinearLayoutManager(this)
        adapter = SearchReviewAdapter(allEvaluations) { evaluation ->
            showReviewDialog(evaluation)
        }
        rvResults.adapter = adapter

        // Buscar avaliações do Firestore
        fetchEvaluations()

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    gridDefault.visibility = View.GONE
                    rvResults.visibility = View.VISIBLE
                    filterList(query)
                } else {
                    gridDefault.visibility = View.VISIBLE
                    rvResults.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

=======
        configurarRecyclerView()
        configurarBusca()
        configurarNavegacao()
        carregarDadosFirebase()
    }

    private fun configurarRecyclerView() {
        val rv = findViewById<RecyclerView>(R.id.rvLivrosAvaliados)
        adapter = LivroAvaliadoAdapter(emptyList()) { livro ->
            val intent = Intent(this, ListaResenhasActivity::class.java)
            intent.putExtra("LIVRO_ID", livro.id)
            startActivity(intent)
        }
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter
    }

    private fun configurarBusca() {
        val etSearch = findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun carregarDadosFirebase() {
        database.child("livros").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = snapshot.children.mapNotNull { it.getValue(Livro::class.java) }
                    .filter { it.avaliacao > 0 } // Requisito: Apenas livros avaliados
                adapter.atualizarLista(lista)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ReviewsActivity, "Erro ao carregar dados: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun configurarNavegacao() {
>>>>>>> Stashed changes
        findViewById<View>(R.id.nav_home_reviews)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.nav_unishop_reviews)?.setOnClickListener {
            val intent = Intent(this, TelaLojaCustodioActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_datas_reviews)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }
<<<<<<< Updated upstream

        // Configuração dos cliques para Detalhes dos Livros
        setupBookClick(R.id.book_norma_reviews, "Teoria da Norma Jurídica", R.drawable.book_norma)
        setupBookClick(R.id.book_legado_reviews, "Código Legado", R.drawable.book_legado)
        setupBookClick(R.id.book_sobotta_reviews, "Sobotta", R.drawable.book_sobotta)
        setupBookClick(R.id.book_sentido_reviews, "Em Busca de Sentido", R.drawable.book_sentido)
        setupBookClick(R.id.book_red_center_reviews, "O Homem e Seus Símbolos", R.drawable.book_red_center)
        setupBookClick(R.id.book_crime_reviews, "Crime e Castigo", R.drawable.book_crime)
        setupBookClick(R.id.book_jazz_reviews, "Jazz", R.drawable.book_jazz)
        setupBookClick(R.id.book_lines_reviews, "Lines", R.drawable.book_lines)
        setupBookClick(R.id.book_expressionismo_reviews, "Expressionismo", R.drawable.book_expressionismo)
        setupBookClick(R.id.book_teologia_reviews, "A Teologia do Livro de Apocalipse", R.drawable.book_teologia)
        setupBookClick(R.id.book_dadaismo_reviews, "Dadaismo", R.drawable.book_dadaismo)

        findViewById<View>(R.id.book_noite_taverna_reviews)?.setOnClickListener {
            val intent = Intent(this, ListaResenhasActivity::class.java)
            intent.putExtra("LIVRO_ID", "noite_na_taverna_id") // ID de exemplo
            intent.putExtra("IS_ADMIN", true) // Forçando admin para você testar o apagar
            startActivity(intent)
        }
=======
>>>>>>> Stashed changes
    }

    private fun setupBookClick(viewId: Int, title: String, imageRes: Int) {
        findViewById<View>(viewId)?.setOnClickListener {
            val intent = Intent(this, DetalhesLivroActivity::class.java)
            intent.putExtra("BOOK_TITLE", title)
            intent.putExtra("BOOK_IMAGE", imageRes)
            startActivity(intent)
        }
    }

    private fun fetchEvaluations() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("evaluations")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                allEvaluations = documents.map { 
                    val data = it.data.toMutableMap()
                    data["docId"] = it.id
                    data
                }
                adapter.updateList(allEvaluations)
            }
    }

    private fun showReviewDialog(evaluation: Map<String, Any>) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_view_review)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val tvTitle = dialog.findViewById<TextView>(R.id.tvBookTitleDialog)
        val tvContent = dialog.findViewById<TextView>(R.id.tvReviewContentDialog)
        val tvDate = dialog.findViewById<TextView>(R.id.tvReviewDateDialog)
        val btnClose = dialog.findViewById<ImageView>(R.id.btnCloseReview)
        val btnDelete = dialog.findViewById<ImageView>(R.id.ivDeleteReview)

        tvTitle.text = evaluation["bookTitle"] as? String ?: "Sem Título"
        tvContent.text = evaluation["comment"] as? String ?: "Sem comentário"

        val timestamp = evaluation["timestamp"] as? com.google.firebase.Timestamp
        if (timestamp != null) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            tvDate.text = sdf.format(timestamp.toDate())
        }

        btnDelete.setOnClickListener {
            val docId = evaluation["docId"] as? String
            if (docId != null) {
                db.collection("evaluations").document(docId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Avaliação excluída", Toast.LENGTH_SHORT).show()
                        fetchEvaluations() // Atualiza a lista
                        dialog.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao excluir: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun filterList(query: String) {
        val filtered = allEvaluations.filter {
            val title = it["bookTitle"] as? String ?: ""
            title.contains(query, ignoreCase = true)
        }
        adapter.updateList(filtered)
    }
}
