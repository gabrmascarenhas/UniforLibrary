package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginAdminActivity : AppCompatActivity() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseDatabase.getInstance().getReference("users") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)

        val etMatricula = findViewById<EditText>(R.id.etMatricula)
        val etSenha = findViewById<EditText>(R.id.etSenha)

        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val matricula = etMatricula.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            if (matricula.isEmpty() || senha.isEmpty()) return@setOnClickListener toast("Preencha tudo")

            // 1. Busca admin pela matrícula
            db.orderByChild("matricula").equalTo(matricula).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.children.firstOrNull() ?: return toast("Admin não encontrado")

                    val email = user.child("email").getValue(String::class.java) ?: ""
                    val isAdmin = user.child("admin").getValue(Boolean::class.java) ?: false


                    // 2. Bloqueia usuário comum (Só entra se isAdmin for true)
                    if (!isAdmin) return toast("Acesso Negado: Você não é administrador")

                    // 3. Autentica
                    auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val intent = Intent(this@LoginAdminActivity, LibraryHomeActivity::class.java)
                            intent.putExtra("IS_ADMIN", true)
                            startActivity(intent)
                            finish()
                        } else toast("Senha incorreta")
                    }
                }
                override fun onCancelled(error: DatabaseError) = toast(error.message)
            })
        }

        findViewById<Button>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvForgotPassword).setOnClickListener { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }
        findViewById<TextView>(R.id.tvCreateAccount).setOnClickListener { startActivity(Intent(this, TelasGabrActivity::class.java)) }
    }

    private fun toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
}