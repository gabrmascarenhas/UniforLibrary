package com.example.chatbox

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Componentes
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val btnSendCode = findViewById<Button>(R.id.btnSendCode)

        btnSendCode.setOnClickListener {

            val email = etEmail.text.toString().trim()

            // Verifica se o e-mail está vazio
            if (email.isEmpty()) {
                Toast.makeText(
                    this,
                    "Digite seu e-mail",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Verifica formato do e-mail
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(
                    this,
                    "E-mail inválido",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Desabilita botão enquanto envia
            btnSendCode.isEnabled = false

            // Envia e-mail de recuperação
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->

                    btnSendCode.isEnabled = true

                    if (task.isSuccessful) {

                        Toast.makeText(
                            this,
                            "Link de recuperação enviado para o e-mail",
                            Toast.LENGTH_LONG
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this,
                            "Erro: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
}