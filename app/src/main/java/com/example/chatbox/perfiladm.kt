package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class perfiladm : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfiladm)

        findViewById<View>(R.id.nav_unishop_adm)?.setOnClickListener {
            // Admin vai para a Loja de Admin
            val intent = Intent(this, LojaCustodioPontosActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_reviews_adm)?.setOnClickListener {
            val intent = Intent(this, ReviewsActivity::class.java)
            intent.putExtra("IS_ADMIN", true)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_home_adm)?.setOnClickListener {
            // Volta para a Home com a flag de admin
            val intent = Intent(this, LibraryHomeActivity::class.java)
            intent.putExtra("IS_ADMIN", true)
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.nav_data_adm)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            intent.putExtra("IS_ADMIN", true)
            startActivity(intent)
        }

        findViewById<Button>(R.id.button3)?.setOnClickListener {
            val intent = Intent(this, CadastroLivroActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout_adm)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
