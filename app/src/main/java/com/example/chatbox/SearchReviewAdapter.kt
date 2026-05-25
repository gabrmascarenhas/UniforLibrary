package com.example.chatbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class SearchReviewAdapter(
    private var list: List<Map<String, Any>>,
    private val onItemClick: (Map<String, Any>) -> Unit
) : RecyclerView.Adapter<SearchReviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tvBookTitleSearch)
        val date: TextView = view.findViewById(R.id.tvLastReviewDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_search_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.title.text = item["bookTitle"] as? String ?: "Sem Título"
        
        val timestamp = item["timestamp"] as? com.google.firebase.Timestamp
        if (timestamp != null) {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            holder.date.text = "Avaliado em: ${sdf.format(timestamp.toDate())}"
        } else {
            holder.date.text = "Data não disponível"
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = list.size

    fun updateList(newList: List<Map<String, Any>>) {
        list = newList
        notifyDataSetChanged()
    }
}
