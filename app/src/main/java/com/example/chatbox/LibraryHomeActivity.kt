package com.example.chatbox

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class LibraryHomeActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private val repositorioLivros = RepositorioLivros()
    private var todosOsLivros: List<Livro> = emptyList()
    
    private var searchPopupWindow: PopupWindow? = null
    private lateinit var searchAdapter: SearchAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library_home)

        etSearch = findViewById(R.id.etSearch)

        setupNavigation()
        carregarLivrosDoFirebase()

        // Lógica de sugestões enquanto digita
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    mostrarResultadosBusca(query)
                } else {
                    searchPopupWindow?.dismiss()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Lógica de "Enter" para busca definitiva
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = etSearch.text.toString()
                verificarSeLivroExiste(query)
                hideKeyboard()
                true
            } else {
                false
            }
        }
    }

    private fun setupNavigation() {
        val profileIconTop = findViewById<View>(R.id.circleProfile)
        profileIconTop.setOnClickListener { openProfile() }

        val profileIconBottom = findViewById<View>(R.id.nav_perfil_home)
        profileIconBottom.setOnClickListener { openProfile() }

        findViewById<View>(R.id.nav_unishop_home)?.setOnClickListener {
            val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
            val destination = if (isAdmin) LojaCustodioPontosActivity::class.java else TelaLojaCustodioActivity::class.java
            startActivity(Intent(this, destination))
        }

        findViewById<View>(R.id.nav_reviews_home)?.setOnClickListener {
            startActivity(Intent(this, ReviewsActivity::class.java))
        }

        findViewById<View>(R.id.nav_datas_home)?.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        findViewById<View>(R.id.circleTerminal).setOnClickListener {
            startActivity(Intent(this, ChatBoxActivity::class.java))
        }

        findViewById<Button>(R.id.btnRequisitarLivro)?.setOnClickListener {
            startActivity(Intent(this, RequisitarLivroCustodioActivity::class.java))
        }

        val bookIds = listOf(
            R.id.book_norma, R.id.book_legado, R.id.book_sobotta,
            R.id.book_sentido, R.id.book_red_center, R.id.book_crime,
            R.id.book_noite_taverna, R.id.book_jazz, R.id.book_lines,
            R.id.book_expressionismo, R.id.book_teologia, R.id.book_dadaismo
        )

        bookIds.forEach { id ->
            findViewById<View>(id)?.setOnClickListener {
                if (id == R.id.book_noite_taverna) {
                    val intent = Intent(this, AvaliacoesDetalhesActivity::class.java)
                    intent.putExtra("LIVRO_ID", "noite_na_taverna_id") // ID estático para exemplo
                    startActivity(intent)
                } else {
                    showAccessBookDialog()
                }
            }
        }
    }

    private fun carregarLivrosDoFirebase() {
        lifecycleScope.launch {
            try {
                todosOsLivros = repositorioLivros.listarLivros()
            } catch (e: Exception) {
                // Erro silenciado para manter fluxo
            }
        }
    }

    private fun mostrarResultadosBusca(query: String) {
        val filtrados = todosOsLivros.filter { 
            it.titulo.contains(query, ignoreCase = true) 
        }

        if (filtrados.isEmpty()) {
            searchPopupWindow?.dismiss()
            return
        }

        if (searchPopupWindow == null) {
            val popupView = layoutInflater.inflate(R.layout.popup_search_results, null)
            val rv = popupView.findViewById<RecyclerView>(R.id.rvSearchResults)
            rv.layoutManager = LinearLayoutManager(this)
            
            searchAdapter = SearchAdapter(filtrados) { livro ->
                searchPopupWindow?.dismiss()
                val intent = Intent(this, AvaliacoesDetalhesActivity::class.java)
                intent.putExtra("LIVRO_ID", livro.id)
                startActivity(intent)
            }
            rv.adapter = searchAdapter

            searchPopupWindow = PopupWindow(
                popupView,
                etSearch.width,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                false
            ).apply {
                elevation = 10f
                isOutsideTouchable = true
            }
        } else {
            searchAdapter.updateList(filtrados)
        }

        if (searchPopupWindow?.isShowing == false) {
            searchPopupWindow?.showAsDropDown(etSearch)
        }
    }

    private fun verificarSeLivroExiste(query: String) {
        if (query.isEmpty()) return
        
        val existe = todosOsLivros.any { it.titulo.contains(query, ignoreCase = true) }
        
        if (!existe) {
            searchPopupWindow?.dismiss()
            showBookNotFoundDialog()
        }
    }

    private fun showBookNotFoundDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_book_not_found)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        
        dialog.findViewById<Button>(R.id.btnOkNotFound).setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }

    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun openProfile() {
        val isAdmin = intent.getBooleanExtra("IS_ADMIN", false)
        val intent = if (isAdmin) Intent(this, perfiladm::class.java) else Intent(this, perfiluser::class.java)
        startActivity(intent)
    }

    private fun showAccessBookDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_access_book)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.findViewById<Button>(R.id.btnYes).setOnClickListener {
            startActivity(Intent(this, DetalhesLivroActivity::class.java))
            dialog.dismiss()
        }
        dialog.findViewById<Button>(R.id.btnNo).setOnClickListener { dialog.dismiss() }
        dialog.show()
    }
}
