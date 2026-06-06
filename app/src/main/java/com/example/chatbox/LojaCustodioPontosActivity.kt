package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LojaCustodioPontosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_loja_custodio_pontos)

        findViewById<View>(R.id.nav_datas_pontos)?.setOnClickListener {
            val intent = Intent(this, CalendarActivity::class.java)
            startActivity(intent)
        }

        val rv = findViewById<RecyclerView>(R.id.rvPontos)
        val dados = listOf(
            PontoItemCustodio("Mochila UNIFOR", "4000 pontos"),
            PontoItemCustodio("Camisa UNIFOR", "3000 pontos"),
            PontoItemCustodio("Caneca UNIFOR", "2000 pontos"),
            PontoItemCustodio("Comprar livro", "9999 pontos"),
            PontoItemCustodio("Digitalizar livro", "1000 pontos"),
            PontoItemCustodio("Cafézinho", "100 pontos")
        ).sortedByDescending { it.pontos.filter { char -> char.isDigit() }.toIntOrNull() ?: 0 }

        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = PontosAdapterCustodio(dados)
    }
}

class PontosAdapterCustodio(private val lista: List<PontoItemCustodio>) : RecyclerView.Adapter<PontosAdapterCustodio.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nome: TextView = view.findViewById(R.id.tvNomeItem)
        val pontos: TextView = view.findViewById(R.id.tvValorPonto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ponto_custodio, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.nome.text = item.nome
        holder.pontos.text = item.pontos
    }

    override fun getItemCount() = lista.size
}
