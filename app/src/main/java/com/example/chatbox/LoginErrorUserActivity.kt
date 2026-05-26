package com.example.chatbox

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LoginErrorUserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_error_user)

        val btnRetry = findViewById<Button>(R.id.btnRetry)
        btnRetry.setOnClickListener {
            finish()
        }
    }
}
