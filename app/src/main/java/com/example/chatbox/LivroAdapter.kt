package com.example.chatbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LivroAdapter(
    private var livros: List<Livro>,
    private val onDeleteClick: (Livro) -> Unit,
    private val onEditClick: (Livro) -> Unit
) : RecyclerView.Adapter<LivroAdapter.LivroViewHolder>() {

    class LivroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitulo: TextView = view.findViewById(R.id.tvLivroTitulo)
        val tvAutor: TextView = view.findViewById(R.id.tvLivroAutor)
        val btnDelete: ImageView = view.findViewById(R.id.btnDeleteLivro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LivroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_livro_admin, parent, false)
        return LivroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LivroViewHolder, position: Int) {
        val livro = livros[position]
        holder.tvTitulo.text = livro.titulo
        holder.tvAutor.text = livro.autor
        holder.btnDelete.setOnClickListener { onDeleteClick(livro) }
        holder.itemView.setOnClickListener { onEditClick(livro) }
    }

    override fun getItemCount() = livros.size

    fun updateList(novaLista: List<Livro>) {
        livros = novaLista
        notifyDataSetChanged()
    }
}