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

class CalendarMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendar_main)
        
        val inputField = findViewById<EditText>(R.id.inputField)
        val btnSend = findViewById<Button>(R.id.btnSend)

        btnSend.setOnClickListener {
            val inputText = inputField.text.toString()
            
            if (inputText == "2516230") {
                val intent = Intent(this, ResetPasswordActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Número não reconhecido para redefinição", Toast.LENGTH_SHORT).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}