package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etMatricula = findViewById<EditText>(R.id.etMatricula)
        val etSenha = findViewById<EditText>(R.id.etSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, CalendarMainActivity::class.java)
            startActivity(intent)
        }

        tvCreateAccount.setOnClickListener {
            val intent = Intent(this, CriarContaActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val matricula = etMatricula.text.toString().trim()
            val senha = etSenha.text.toString().trim()

            if (matricula.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginWithMatricula(matricula, senha)
        }

        btnAdmin.setOnClickListener {
            val intent = Intent(this, LoginAdminActivity::class.java)
            startActivity(intent)
        }
        
        // Outros listeners (Login, Criar Conta, etc) podem ser adicionados aqui
    }

    private fun loginWithMatricula(matricula: String, senha: String) {
        // Tenta buscar por matrícula (como String)
        database.child("users").orderByChild("matricula").equalTo(matricula)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        validateUser(snapshot.children.first(), senha)
                    } else {
                        // Tenta buscar como número se falhar como String
                        val matriculaNum = matricula.toDoubleOrNull()
                        if (matriculaNum != null) {
                            database.child("users").orderByChild("matricula").equalTo(matriculaNum)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snap: DataSnapshot) {
                                        if (snap.exists()) {
                                            validateUser(snap.children.first(), senha)
                                        } else {
                                            showError()
                                        }
                                    }
                                    override fun onCancelled(error: DatabaseError) { showError() }
                                })
                        } else {
                            showError()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    showError()
                }
            })
    }

    private fun validateUser(userSnapshot: DataSnapshot, senhaDigitada: String) {
        val senhaNoDB = userSnapshot.child("senha").getValue(String::class.java)
        val isAdmin = userSnapshot.child("admin").getValue(Boolean::class.java) ?: false
        val email = userSnapshot.child("email").getValue(String::class.java)

        if (senhaNoDB == senhaDigitada) {
            // Se houver e-mail, faz o login no Auth também para manter a sessão ativa
            if (email != null) {
                auth.signInWithEmailAndPassword(email, senhaDigitada)
            }
            
            val intent = Intent(this, LibraryHomeActivity::class.java)
            intent.putExtra("IS_ADMIN", isAdmin)
            startActivity(intent)
            finish()
        } else {
            showError()
        }
    }

    private fun showError() {
        val intent = Intent(this, LoginErrorPasswordActivity::class.java)
        startActivity(intent)
    }
}
