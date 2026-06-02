package com.example.chatbox

import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class RepositorioLoja {
    private val databaseUrl = "https://uniforlibrary-30c5c-default-rtdb.firebaseio.com/"
    private val db = Firebase.database(databaseUrl).reference

    suspend fun obterItens(): List<PontoItemCustodio> {
        // Alterado para 'loja' para coincidir com suas regras do Firebase
        val snapshot = db.child("loja").get().await()
        return snapshot.children.mapNotNull { it.getValue(PontoItemCustodio::class.java) }
    }

    suspend fun adicionarItem(item: PontoItemCustodio) {
        val ref = db.child("loja").push()
        val itemComId = item.copy(id = ref.key ?: "")
        ref.setValue(itemComId).await()
    }
}
