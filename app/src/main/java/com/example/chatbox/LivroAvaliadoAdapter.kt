package com.example.chatbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class LivroAvaliadoAdapter(
    private var listaOriginal: List<Livro>,
    private val onItemClick: (Livro) -> Unit
) : RecyclerView.Adapter<LivroAvaliadoAdapter.ViewHolder>() {

    private var listaFiltrada = listaOriginal.toList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCapa: ImageView = view.findViewById(R.id.ivCapaLivro)
        val tvTitulo: TextView = view.findViewById(R.id.tvTituloLivro)
        val tvAutor: TextView = view.findViewById(R.id.tvAutorLivro)
        val rbAvaliacao: RatingBar = view.findViewById(R.id.rbAvaliacao)
        val tvNota: TextView = view.findViewById(R.id.tvNota)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_livro_avaliado, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val livro = listaFiltrada[position]
        holder.tvTitulo.text = livro.titulo
        holder.tvAutor.text = livro.autor
        holder.rbAvaliacao.rating = livro.avaliacao
        holder.tvNota.text = livro.avaliacao.toString()

        val context = holder.itemView.context
        if (livro.capUrl.startsWith("http")) {
            Glide.with(context)
                .load(livro.capUrl)
                .placeholder(R.drawable.unifor_logo01)
                .into(holder.ivCapa)
        } else {
            val resId = context.resources.getIdentifier(livro.capUrl, "drawable", context.packageName)
            holder.ivCapa.setImageResource(if (resId != 0) resId else R.drawable.unifor_logo01)
        }

        holder.itemView.setOnClickListener { onItemClick(livro) }
    }

    override fun getItemCount() = listaFiltrada.size

    fun atualizarLista(novaLista: List<Livro>) {
        listaOriginal = novaLista
        listaFiltrada = novaLista
        notifyDataSetChanged()
    }

    fun filtrar(texto: String) {
        listaFiltrada = if (texto.isEmpty()) {
            listaOriginal
        } else {
            listaOriginal.filter {
                it.titulo.contains(texto, ignoreCase = true) ||
                it.autor.contains(texto, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
