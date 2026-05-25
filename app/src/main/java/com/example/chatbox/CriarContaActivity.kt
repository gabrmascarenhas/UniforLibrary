package com.example.chatbox


import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CriarContaActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_criar_conta)

        val etNome = findViewById<EditText>(R.id.etNome)
        val etMatricula = findViewById<EditText>(R.id.etMatricula)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etSenha = findViewById<EditText>(R.id.etSenha)
        val etCentro = findViewById<EditText>(R.id.etCentro)
        val btnCriarConta = findViewById<Button>(R.id.btnCriarConta)

        btnCriarConta.setOnClickListener {
            val nome = etNome.text.toString().trim()
            val matricula = etMatricula.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val senha = etSenha.text.toString().trim()
            val centro = etCentro.text.toString().trim()

            if (nome.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty() && matricula.isNotEmpty() && centro.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                val userMap = mapOf(
                                    "nome" to nome,
                                    "matricula" to matricula,
                                    "centro" to centro,
                                    "email" to email,
                                    "senha" to senha,
                                    "admin" to false
                                )

                                database.child("users").child(userId).setValue(userMap)
                                    .addOnSuccessListener {
                                        Log.d("CriarConta", "Dados salvos no Database")
                                        Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, LibraryHomeActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("CriarConta", "Erro ao salvar dados", e)
                                        Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Log.w("CriarConta", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(this, "Falha ao criar conta: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}