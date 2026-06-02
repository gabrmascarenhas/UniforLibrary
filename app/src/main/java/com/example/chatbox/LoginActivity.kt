package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    // URL explicitamente definida para garantir a conexão
    private val databaseUrl = "https://uniforlibrary-30c5c-default-rtdb.firebaseio.com/"
    private val db by lazy { FirebaseDatabase.getInstance(databaseUrl).getReference("users") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etMatricula = findViewById<EditText>(R.id.etMatricula)
        val etSenha = findViewById<EditText>(R.id.etSenha)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val matricula = etMatricula.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            if (matricula.isEmpty() || senha.isEmpty()) return@setOnClickListener toast("Preencha tudo")

            db.orderByChild("matricula").equalTo(matricula).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.children.firstOrNull() ?: return toast("Usuário não encontrado")

                    val email = user.child("email").getValue(String::class.java) ?: ""
                    val isAdmin = user.child("admin").getValue(Boolean::class.java) ?: false

                    if (isAdmin) return toast("Use o login de administrador")

                    auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this@LoginActivity, LibraryHomeActivity::class.java))
                            finish()
                        } else toast("Senha incorreta ou erro de conexão: ${task.exception?.message}")
                    }
                }
                override fun onCancelled(error: DatabaseError) = toast("Erro Firebase: ${error.message}")
            })
        }

        findViewById<TextView>(R.id.tvCreateAccount).setOnClickListener { startActivity(Intent(this, CriarContaActivity::class.java)) }
        findViewById<Button>(R.id.btnAdmin).setOnClickListener { startActivity(Intent(this, LoginAdminActivity::class.java)) }
    }

    private fun toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
}
