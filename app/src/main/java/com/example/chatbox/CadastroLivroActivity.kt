package com.example.chatbox

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class CadastroLivroActivity : AppCompatActivity() {

    private val repositorio = RepositorioLivros()
    private var livroId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_livro)

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val etTitulo = findViewById<EditText>(R.id.etTitulo)
        val etAutor = findViewById<EditText>(R.id.etAutor)
        val etEditora = findViewById<EditText>(R.id.etEditora)
        val etAno = findViewById<EditText>(R.id.etAno)
        val etSinopse = findViewById<EditText>(R.id.etSinopse)
        val etCapaUrl = findViewById<EditText>(R.id.etCapaUrl)
        val etPdfUrl = findViewById<EditText>(R.id.etPdfUrl)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btnBack = findViewById<View>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        // Recuperar dados se for edição
        livroId = intent.getStringExtra("LIVRO_ID")
        if (livroId != null) {
            tvTitle.text = "Editar Livro"
            btnSalvar.text = "Atualizar Livro"
            etTitulo.setText(intent.getStringExtra("TITULO"))
            etAutor.setText(intent.getStringExtra("AUTOR"))
            etEditora.setText(intent.getStringExtra("EDITORA"))
            etAno.setText(intent.getIntExtra("ANO", 0).toString())
            etSinopse.setText(intent.getStringExtra("SINOPSE"))
            etCapaUrl.setText(intent.getStringExtra("CAPA"))
            etPdfUrl.setText(intent.getStringExtra("PDF"))
        }

        btnSalvar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val autor = etAutor.text.toString().trim()
            val editora = etEditora.text.toString().trim()
            val anoStr = etAno.text.toString().trim()
            val sinopse = etSinopse.text.toString().trim()
            val capaUrl = etCapaUrl.text.toString().trim()
            val pdfUrl = etPdfUrl.text.toString().trim()

            if (titulo.isEmpty() || autor.isEmpty() || editora.isEmpty() || anoStr.isEmpty() || sinopse.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ano = anoStr.toIntOrNull() ?: 0
            val livro = Livro(
                id = livroId ?: "",
                titulo = titulo,
                autor = autor,
                editora = editora,
                ano = ano,
                sinopse = sinopse,
                capUrl = capaUrl,
                pdfUrl = pdfUrl
            )

            // Mostrar progresso e desabilitar botão
            btnSalvar.isEnabled = false
            progressBar.visibility = View.VISIBLE

            lifecycleScope.launch {
                try {
                    if (livroId == null) {
                        repositorio.adicionarLivro(livro)
                        Toast.makeText(this@CadastroLivroActivity, "Livro cadastrado com sucesso!", Toast.LENGTH_LONG).show()
                    } else {
                        repositorio.atualizarLivro(livro)
                        Toast.makeText(this@CadastroLivroActivity, "Livro atualizado com sucesso!", Toast.LENGTH_LONG).show()
                    }
                    finish() // Fecha a tela e volta
                } catch (e: Exception) {
                    Toast.makeText(this@CadastroLivroActivity, "Erro ao salvar: ${e.message}", Toast.LENGTH_LONG).show()
                    btnSalvar.isEnabled = true
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}
