package com.example.chatbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class SearchAdapter(
    private var books: List<Livro>,
    private val onBookClick: (Livro) -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivCover: ImageView = view.findViewById(R.id.ivBookCover)
        val tvTitle: TextView = view.findViewById(R.id.tvBookTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvBookAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val book = books[position]
        holder.tvTitle.text = book.titulo
        holder.tvAuthor.text = book.autor

        if (book.capUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(book.capUrl)
                .placeholder(R.drawable.bg_book_card)
                .into(holder.ivCover)
        } else {
            holder.ivCover.setImageResource(R.drawable.bg_book_card)
        }

        holder.itemView.setOnClickListener { onBookClick(book) }
    }

    override fun getItemCount() = books.size

    fun updateList(newList: List<Livro>) {
        books = newList
        notifyDataSetChanged()
    }
}