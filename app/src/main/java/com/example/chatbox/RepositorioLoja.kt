package com.example.chatbox

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RepositorioLoja {
    private val db = FirebaseDatabase.getInstance().getReference("loja")

    suspend fun obterItens(): List<PontoItemCustodio> {
        return try {
            val snapshot = db.get().await()
            val itens = mutableListOf<PontoItemCustodio>()
            
            if (snapshot.exists()) {
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(PontoItemCustodio::class.java)
                    if (item != null) {
                        itens.add(item)
                    }
                }
            }
            
            val listaFinal = if (itens.isEmpty()) {
                obterItensPadrao()
            } else {
                itens
            }

            // Organiza em ordem decrescente de valor em pontos
            listaFinal.sortedByDescending { extrairValorNumerico(it.pontos) }

        } catch (e: Exception) {
            obterItensPadrao().sortedByDescending { extrairValorNumerico(it.pontos) }
        }
    }

    private fun extrairValorNumerico(pontosStr: String): Int {
        return pontosStr.filter { it.isDigit() }.toIntOrNull() ?: 0
    }

    private fun obterItensPadrao(): List<PontoItemCustodio> {
        return listOf(
            PontoItemCustodio("Comprar livro", "10000 pontos"),
            PontoItemCustodio("Mochila UNIFOR", "4000 pontos"),
            PontoItemCustodio("Camisa UNIFOR", "3000 pontos"),
            PontoItemCustodio("Caneca UNIFOR", "2000 pontos"),
            PontoItemCustodio("Digitalizar livro", "1000 pontos"),
            PontoItemCustodio("Cafézinho", "100 pontos")
        )
    }
}
