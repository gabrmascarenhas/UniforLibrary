package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance().getReference("users") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etMatricula = findViewById<EditText>(R.id.etMatricula)
        val etSenha = findViewById<EditText>(R.id.etSenha)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val matricula = etMatricula.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            if (matricula.isEmpty() || senha.isEmpty()) return@setOnClickListener toast("Preencha tudo")

            // 1. Busca usuário pela matrícula
            db.orderByChild("matricula").equalTo(matricula).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.children.firstOrNull() ?: return toast("Usuário não encontrado")
                    
                    val email = user.child("email").getValue(String::class.java) ?: ""
                    val isAdmin = user.child("admin").getValue(Boolean::class.java) ?: false

                    // 2. Bloqueia Admin nesta tela
                    if (isAdmin) return toast("Use o login de administrador")

                    // 3. Autentica
                    auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this@LoginActivity, LibraryHomeActivity::class.java))
                            finish()
                        } else toast("Senha incorreta")
                    }
                }
                override fun onCancelled(error: DatabaseError) = toast(error.message)
            })
        }

        findViewById<TextView>(R.id.tvCreateAccount).setOnClickListener { startActivity(Intent(this, CriarContaActivity::class.java)) }
        findViewById<Button>(R.id.btnAdmin).setOnClickListener { startActivity(Intent(this, LoginAdminActivity::class.java)) }
    }

    private fun toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
}