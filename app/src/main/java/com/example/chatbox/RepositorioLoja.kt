package com.example.chatbox

import com.google.firebase.Firebase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class RepositorioLoja {
    private val db = Firebase.database.reference

    suspend fun obterItens(): List<PontoItemCustodio> {
        val snapshot = db.child("itensLoja").get().await()
        return snapshot.children.mapNotNull { it.getValue(PontoItemCustodio::class.java) }
    }

    suspend fun adicionarItem(item: PontoItemCustodio) {
        val ref = db.child("itensLoja").push()
        val itemComId = item.copy(id = ref.key ?: "")
        ref.setValue(itemComId).await()
    }
}
