package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginAdminActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val etMatricula = findViewById<EditText>(R.id.etMatricula)
        val etSenha = findViewById<EditText>(R.id.etSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnBack = findViewById<Button>(R.id.btnBack)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, CalendarMainActivity::class.java))
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, TelasGabrActivity::class.java))
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnLogin.setOnClickListener {
            val matriculaText = etMatricula.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            if (matriculaText.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginAsAdmin(matriculaText, senha)
        }
    }

    private fun loginAsAdmin(matricula: String, senha: String) {
        // Conecta ao banco de dados para validar matrícula e verificar status de Admin
        database.child("users")
            .orderByChild("matricula")
            .equalTo(matricula)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userSnapshot = snapshot.children.first()
                        val email = userSnapshot.child("email").getValue(String::class.java)
                        val isAdmin = userSnapshot.child("admin").getValue(Boolean::class.java) ?: false

                        // BLOQUEIO: Se NÃO for admin, não pode logar por esta tela
                        if (!isAdmin) {
                            Toast.makeText(this@LoginAdminActivity, "Acesso Negado: Você não é um administrador", Toast.LENGTH_LONG).show()
                            return
                        }

                        if (!email.isNullOrEmpty()) {
                            // Tenta logar no Authenticator do Firebase
                            auth.signInWithEmailAndPassword(email, senha)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val intent = Intent(this@LoginAdminActivity, LibraryHomeActivity::class.java)
                                        intent.putExtra("IS_ADMIN", true)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        goToErrorScreen()
                                    }
                                }
                        } else {
                            goToErrorScreen()
                        }
                    } else {
                        goToErrorScreen()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@LoginAdminActivity, "Erro de conexão: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun goToErrorScreen() {
        val intent = Intent(this, LoginErrorPasswordActivity::class.java)
        startActivity(intent)
    }
}
