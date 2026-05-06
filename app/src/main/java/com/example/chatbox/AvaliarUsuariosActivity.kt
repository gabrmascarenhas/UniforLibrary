package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class AvaliarUsuariosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_avaliar_usuarios)

        findViewById<View>(R.id.nav_home_avaliar)?.setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.nav_datas_avaliar)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }
    }
}
