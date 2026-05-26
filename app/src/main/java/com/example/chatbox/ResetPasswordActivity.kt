package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val inputNewPassword = findViewById<EditText>(R.id.inputNewPassword)
        val inputConfirmPassword = findViewById<EditText>(R.id.inputConfirmPassword)
        val btnConfirm = findViewById<Button>(R.id.btnConfirm)

        btnConfirm.setOnClickListener {
            val password = inputNewPassword.text.toString()
            val confirmPassword = inputConfirmPassword.text.toString()

            if (password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                Toast.makeText(this, "Senha redefinida com sucesso!", Toast.LENGTH_SHORT).show()
                
                // Redireciona para a Tela Inicial
                val intent = Intent(this, TelaInicial::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
