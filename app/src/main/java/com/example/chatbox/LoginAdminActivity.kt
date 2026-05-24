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

class LoginAdminActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)

        val etMatricula = findViewById<EditText>(R.id.etMatricula)
        val etSenha = findViewById<EditText>(R.id.etSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnBack = findViewById<Button>(R.id.btnBack)
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        tvForgotPassword.setOnClickListener {
            val intent = Intent(this, CalendarMainActivity::class.java)
            startActivity(intent)
        }

        tvCreateAccount.setOnClickListener {
            val intent = Intent(this, TelasGabrActivity::class.java)
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

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loginWithMatricula(matricula: String, senha: String) {
        database.child("users").orderByChild("matricula").equalTo(matricula)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        validateAdmin(snapshot.children.first(), senha)
                    } else {
                        val matriculaNum = matricula.toDoubleOrNull()
                        if (matriculaNum != null) {
                            database.child("users").orderByChild("matricula").equalTo(matriculaNum)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snap: DataSnapshot) {
                                        if (snap.exists()) {
                                            validateAdmin(snap.children.first(), senha)
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

    private fun validateAdmin(userSnapshot: DataSnapshot, senhaDigitada: String) {
        val senhaNoDB = userSnapshot.child("senha").getValue(String::class.java)
        val isAdmin = userSnapshot.child("admin").getValue(Boolean::class.java) ?: false
        val email = userSnapshot.child("email").getValue(String::class.java)

        if (senhaNoDB == senhaDigitada) {
            if (isAdmin) {
                if (email != null) {
                    auth.signInWithEmailAndPassword(email, senhaDigitada)
                }
                val intent = Intent(this, LibraryHomeActivity::class.java)
                intent.putExtra("IS_ADMIN", true)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Acesso negado: você não é um administrador", Toast.LENGTH_LONG).show()
            }
        } else {
            showError()
        }
    }

    private fun showError() {
        val intent = Intent(this, LoginErrorPasswordActivity::class.java)
        startActivity(intent)
    }
}
