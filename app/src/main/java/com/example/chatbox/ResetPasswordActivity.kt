package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {
    
    private val auth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val inputNewPassword = findViewById<EditText>(R.id.inputNewPassword)
        val inputConfirmPassword = findViewById<EditText>(R.id.inputConfirmPassword)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)

        btnConfirm.setOnClickListener {
            val password = inputNewPassword.text.toString().trim()
            val confirmPassword = inputConfirmPassword.text.toString().trim()

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                toast("Preencha todos os campos")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                toast("As senhas não coincidem")
                return@setOnClickListener
            }

            if (password.length < 6) {
                toast("A senha deve ter pelo menos 6 caracteres")
                return@setOnClickListener
            }

            // Altera a senha do usuário logado no Firebase Authentication
            val user = auth.currentUser
            if (user != null) {
                user.updatePassword(password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("Senha alterada com sucesso!")
                        
                        // Redireciona para a Tela Inicial (Login)
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Se falhar (ex: precisa de login recente), o Firebase avisa
                        toast("Erro ao alterar: " + task.exception?.message)
                    }
                }
            } else {
                toast("Erro: Usuário não identificado")
            }
        }
    }

    private fun toast(m: String) = Toast.makeText(this, m, Toast.LENGTH_SHORT).show()
}