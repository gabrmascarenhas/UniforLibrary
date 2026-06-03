package com.example.chatbox

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class perfiluser : AppCompatActivity() {
    // Definindo a URL explicitamente para garantir a conexão
    private val databaseUrl = "https://uniforlibrary-30c5c-default-rtdb.firebaseio.com/"
    private val db = Firebase.database(databaseUrl).reference
    private val storage = Firebase.storage.reference
    private val auth = Firebase.auth

    private lateinit var ivProfile: ImageView

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            uploadFotoPerfil(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfiluser)

        ivProfile = findViewById(R.id.imageView11)
        val ivEdit = findViewById<ImageView>(R.id.ivEditPhoto)

        setupNavigation()
        carregarDadosUsuario()

        // Clique na imagem ou no ícone de câmera para mudar a foto
        val clickListener = View.OnClickListener {
            pickImageLauncher.launch("image/*")
        }
        ivProfile.setOnClickListener(clickListener)
        ivEdit.setOnClickListener(clickListener)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupNavigation() {
        findViewById<View>(R.id.nav_unishop)?.setOnClickListener {
            startActivity(Intent(this, TelaLojaCustodioActivity::class.java))
        }

        findViewById<View>(R.id.nav_reviews)?.setOnClickListener {
            startActivity(Intent(this, ReviewsActivity::class.java))
        }

        findViewById<View>(R.id.nav_home)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.nav_datas_user)?.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }
    }

    private fun carregarDadosUsuario() {
        val tvPontos = findViewById<TextView>(R.id.textView10)
        val tvNome = findViewById<TextView>(R.id.tvNomeUsuario)
        val tvCentro = findViewById<TextView>(R.id.tvCentroUsuario)
        val tvStats = findViewById<TextView>(R.id.textView11)
        val tvTotal = findViewById<TextView>(R.id.textView12)
        
        val userId = auth.currentUser?.uid ?: return

        lifecycleScope.launch {
            try {
                val snapshot = db.child("users").child(userId).get().await()
                
                if (snapshot.exists()) {
                    val nome = snapshot.child("nome").getValue(String::class.java) ?: "Usuário"
                    val centro = snapshot.child("centro").getValue(String::class.java) ?: "Não informado"
                    val pontos = snapshot.child("pontos").getValue(Int::class.java) ?: 0
                    val fotoUrl = snapshot.child("fotoUrl").getValue(String::class.java)
                    
                    val acessados = snapshot.child("livrosAcessados").getValue(Int::class.java) ?: 0
                    val emEmprestimo = snapshot.child("livrosEmEmprestimo").getValue(Int::class.java) ?: 0
                    val emprestados = snapshot.child("livrosEmprestados").getValue(Int::class.java) ?: 0
                    val avaliacoes = snapshot.child("avaliacoesFeitas").getValue(Int::class.java) ?: 0
                    val total = snapshot.child("totalLivros").getValue(Int::class.java) ?: 0

                    tvNome.text = nome
                    tvCentro.text = centro
                    tvPontos.text = "Sua pontuação total:\n$pontos pontos!"
                    
                    tvStats.text = "Livros Acessados: $acessados\n" +
                                 "Livros Em Empréstimo: $emEmprestimo\n" +
                                 "Livros Emprestados: $emprestados\n" +
                                 "Avaliações Feitas: $avaliacoes"
                    
                    tvTotal.text = "Total de Livros: $total"

                    // Carrega a foto do usuário se existir
                    if (!fotoUrl.isNullOrEmpty()) {
                        Glide.with(this@perfiluser)
                            .load(fotoUrl)
                            .placeholder(R.mipmap.ic_launcher_round)
                            .into(ivProfile)
                    }
                }
            } catch (e: Exception) {
                Log.e("FirebasePerfil", "Erro ao carregar dados do usuário", e)
                Toast.makeText(this@perfiluser, "Erro ao carregar dados", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadFotoPerfil(uri: Uri) {
        val userId = auth.currentUser?.uid ?: return
        val ref = storage.child("perfis/$userId/${UUID.randomUUID()}")

        lifecycleScope.launch {
            try {
                // Upload para o Storage
                ref.putFile(uri).await()
                val url = ref.downloadUrl.await().toString()

                // Salva a URL no Database vinculada ao usuário
                db.child("users").child(userId).child("fotoUrl").setValue(url).await()

                // Atualiza a imagem na tela imediatamente
                Glide.with(this@perfiluser)
                    .load(url)
                    .into(ivProfile)

                Toast.makeText(this@perfiluser, "Foto atualizada com sucesso!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("FirebasePerfil", "Erro no upload da foto", e)
                Toast.makeText(this@perfiluser, "Erro ao atualizar foto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
