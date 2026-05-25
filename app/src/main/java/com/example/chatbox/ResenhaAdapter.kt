package com.example.chatbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ResenhaAdapter(
    private var resenhas: List<Resenha>,
    private val isAdmin: Boolean,
    private val onDeleteClick: (Resenha) -> Unit
) : RecyclerView.Adapter<ResenhaAdapter.ResenhaViewHolder>() {

    class ResenhaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val tvData: TextView = view.findViewById(R.id.tvData)
        val tvComentario: TextView = view.findViewById(R.id.tvComentario)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResenhaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resenha, parent, false)
        return ResenhaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResenhaViewHolder, position: Int) {
        val resenha = resenhas[position]
        holder.tvUsuario.text = resenha.nomeUsuario
        holder.tvComentario.text = resenha.texto
        
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.tvData.text = sdf.format(Date(resenha.data))

        // Só mostra o botão de apagar se for Admin
        holder.btnDelete.visibility = if (isAdmin) View.VISIBLE else View.GONE
        holder.btnDelete.setOnClickListener { onDeleteClick(resenha) }
    }

    override fun getItemCount() = resenhas.size

    fun updateList(novaLista: List<Resenha>) {
        resenhas = novaLista
        notifyDataSetChanged()
    }
}
