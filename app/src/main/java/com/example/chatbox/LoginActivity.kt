package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val etMatricula = findViewById<EditText>(R.id.etMatricula)
        val etSenha = findViewById<EditText>(R.id.etSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, CalendarMainActivity::class.java))
        }

        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, CriarContaActivity::class.java))
        }

        btnAdmin.setOnClickListener {
            startActivity(Intent(this, LoginAdminActivity::class.java))
        }

        btnLogin.setOnClickListener {

            val matriculaTexto = etMatricula.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            if (matriculaTexto.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("LOGIN_DEBUG", "Tentando login com matricula: $matriculaTexto")
            loginWithMatricula(matriculaTexto, senha)
        }
    }

    private fun loginWithMatricula(matricula: String, senha: String) {

        database.child("users")
            .orderByChild("matricula")
            .equalTo(matricula)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("LOGIN_DEBUG", "onDataChange: snapshot.exists() = ${snapshot.exists()}")
                    Log.d("LOGIN_DEBUG", "onDataChange: snapshot.childrenCount = ${snapshot.childrenCount}")

                    if (!snapshot.exists()) {

                        Toast.makeText(
                            this@LoginActivity,
                            "Usuário não encontrado",
                            Toast.LENGTH_SHORT
                        ).show()

                        return
                    }

                    for (userSnapshot in snapshot.children) {

                        val email = userSnapshot.child("email")
                            .getValue(String::class.java)

                        val isAdmin = userSnapshot.child("admin")
                            .getValue(Boolean::class.java) ?: false

                        if (isAdmin) {

                            Toast.makeText(
                                this@LoginActivity,
                                "Use o login de administrador",
                                Toast.LENGTH_LONG
                            ).show()

                            return
                        }

                        if (email.isNullOrEmpty()) {

                            Toast.makeText(
                                this@LoginActivity,
                                "Email não encontrado",
                                Toast.LENGTH_SHORT
                            ).show()

                            return
                        }

                        auth.signInWithEmailAndPassword(email, senha)
                            .addOnCompleteListener { task ->

                                if (task.isSuccessful) {

                                    Toast.makeText(
                                        this@LoginActivity,
                                        "Login realizado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    startActivity(
                                        Intent(
                                            this@LoginActivity,
                                            LibraryHomeActivity::class.java
                                        )
                                    )

                                    finish()

                                } else {

                                    // ERRO COMPLETO NO LOGCAT
                                    Log.e(
                                        "LOGIN_ERROR",
                                        task.exception.toString()
                                    )

                                    // MOSTRA O ERRO REAL NA TELA
                                    Toast.makeText(
                                        this@LoginActivity,
                                        task.exception?.message
                                            ?: "Erro desconhecido",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                    Toast.makeText(
                        this@LoginActivity,
                        "Erro no banco: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    }