package com.example.chatbox

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await

class RepositorioLoja {
    private val db = FirebaseDatabase.getInstance().getReference("loja")

    fun observarItens(onDataChange: (List<PontoItemCustodio>) -> Unit) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val itens = mutableListOf<PontoItemCustodio>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(PontoItemCustodio::class.java)
                    if (item != null) {
                        itens.add(item.copy(id = itemSnapshot.key ?: ""))
                    }
                }
                
                val listaFinal = if (itens.isEmpty()) {
                    obterItensPadrao()
                } else {
                    itens
                }

                val listaOrdenada = listaFinal.sortedByDescending { extrairValorNumerico(it.pontos) }
                onDataChange(listaOrdenada)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    suspend fun adicionarItem(nome: String, pontos: String): Boolean {
        return try {
            val key = db.push().key ?: return false
            val novoItem = PontoItemCustodio(id = key, nome = nome, pontos = "$pontos pontos")
            db.child(key).setValue(novoItem).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removerItem(id: String): Boolean {
        return try {
            if (id.isNotEmpty()) {
                db.child(id).removeValue().await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun extrairValorNumerico(pontosStr: String): Int {
        return pontosStr.filter { it.isDigit() }.toIntOrNull() ?: 0
    }

    private fun obterItensPadrao(): List<PontoItemCustodio> {
        return listOf(
            PontoItemCustodio("1", "Comprar livro", "10000 pontos"),
            PontoItemCustodio("2", "Mochila UNIFOR", "4000 pontos"),
            PontoItemCustodio("3", "Camisa UNIFOR", "3000 pontos"),
            PontoItemCustodio("4", "Caneca UNIFOR", "2000 pontos"),
            PontoItemCustodio("5", "Digitalizar livro", "1000 pontos"),
            PontoItemCustodio("6", "Cafézinho", "100 pontos")
        )
    }
}
