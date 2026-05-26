package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TelasGabrActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_telas_gabr)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etMatricula = findViewById<EditText>(R.id.editTextText)
        val etEmail = findViewById<EditText>(R.id.editTextText2)
        val etTelefone = findViewById<EditText>(R.id.editTextText4)
        val etCentro = findViewById<EditText>(R.id.editTextText5)
        val btnCreateAccount = findViewById<Button>(R.id.button)

        btnCreateAccount.setOnClickListener {
            val matricula = etMatricula.text.toString()
            val email = etEmail.text.toString()
            val telefone = etTelefone.text.toString()
            val centro = etCentro.text.toString()

            if (matricula.isNotEmpty() && email.isNotEmpty() && telefone.isNotEmpty() && centro.isNotEmpty()) {
                val intent = Intent(this, SignupSuccessActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}