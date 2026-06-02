package com.example.chatbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class HomeLivroAdapter(
    private var livros: List<Livro>,
    private val onBookClick: (Livro) -> Unit
) : RecyclerView.Adapter<HomeLivroAdapter.HomeLivroViewHolder>() {

    class HomeLivroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCapa: ImageView = view.findViewById(R.id.ivCapa)
        val tvTitulo: TextView = view.findViewById(R.id.tvTitulo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeLivroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_livro_home, parent, false)
        return HomeLivroViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeLivroViewHolder, position: Int) {
        val livro = livros[position]
        
        val url = livro.capUrl
        if (url != "" && url != null) {
            Glide.with(holder.itemView.context)
                .load(url)
                .placeholder(R.drawable.image_icon)
                .error(R.drawable.image_icon)
                .into(holder.ivCapa)
            holder.tvTitulo.visibility = View.GONE
        } else {
            holder.ivCapa.setImageResource(R.drawable.image_icon)
            holder.tvTitulo.text = livro.titulo
            holder.tvTitulo.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener { onBookClick(livro) }
    }

    override fun getItemCount() = livros.size

    fun updateList(novaLista: List<Livro>) {
        livros = novaLista
        notifyDataSetChanged()
    }
}