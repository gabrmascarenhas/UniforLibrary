package com.example.chatbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class ResenhaAdapter(
    private var resenhas: List<Resenha>,
    private val isAdmin: Boolean,
    private var capaUrl: String? = null,
    private val onDeleteClick: (Resenha) -> Unit
) : RecyclerView.Adapter<ResenhaAdapter.ResenhaViewHolder>() {

    class ResenhaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCapa: ImageView = view.findViewById(R.id.ivCapaLivroResenha)
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
        val tvData: TextView = view.findViewById(R.id.tvData)
        val tvNota: TextView = view.findViewById(R.id.tvNotaResenha)
        val tvComentario: TextView = view.findViewById(R.id.tvComentario)
        val btnDelete: ImageView = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResenhaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resenha, parent, false)
        return ResenhaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResenhaViewHolder, position: Int) {
        val resenha = resenhas[position]
        holder.tvUsuario.text = if (resenha.nomeUsuario.isNotEmpty()) resenha.nomeUsuario else "Usuário"
        
        // Pega texto ou comentario, o que estiver preenchido
        val comentarioFinal = if (resenha.texto.isNotEmpty()) resenha.texto else resenha.comentario
        holder.tvComentario.text = comentarioFinal

        holder.tvNota.text = String.format(Locale.US, "%.1f", resenha.nota)
        
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        holder.tvData.text = sdf.format(Date(resenha.data))

        // Carregar capa do livro se disponível
        if (!capaUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(capaUrl)
                .placeholder(R.drawable.image_icon)
                .error(R.drawable.image_icon)
                .into(holder.ivCapa)
        }

        // Só mostra o botão de apagar se for Admin
        holder.btnDelete.visibility = if (isAdmin) View.VISIBLE else View.GONE
        holder.btnDelete.setOnClickListener { onDeleteClick(resenha) }
    }

    override fun getItemCount() = resenhas.size

    fun updateList(novaLista: List<Resenha>) {
        resenhas = novaLista
        notifyDataSetChanged()
    }

    fun updateCapa(url: String?) {
        capaUrl = url
        notifyDataSetChanged()
    }
}
