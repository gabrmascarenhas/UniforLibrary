package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
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
            val intent = Intent(this, TelasGabrActivity::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val matricula = etMatricula.text.toString()
            val senha = etSenha.text.toString()

            if (matricula == "2516230" && senha == "12345678") {
                val intent = Intent(this, LibraryHomeActivity::class.java)
                intent.putExtra("IS_ADMIN", false)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, LoginErrorPasswordActivity::class.java)
                startActivity(intent)
            }
        }

        btnAdmin.setOnClickListener {
            val intent = Intent(this, LoginAdminActivity::class.java)
            startActivity(intent)
        }
        
        // Outros listeners (Login, Criar Conta, etc) podem ser adicionados aqui
    }
}
