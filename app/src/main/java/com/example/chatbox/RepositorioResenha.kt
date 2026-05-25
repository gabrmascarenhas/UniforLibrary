package com.example.chatbox

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class Resenha(
    val id: String = "",
    val livroId: String = "",
    val userId: String = "",
    val nomeUsuario: String = "",
    val texto: String = "",
    val data: Long = System.currentTimeMillis()
)

class RepositorioResenha {

    private val db = Firebase.database.reference

    // ─── ADICIONAR RESENHA ───────────────────────────────────────────
    suspend fun adicionarResenha(resenha: Resenha) {
        val novoRef = db.child("resenhas").push()
        val resenhaComId = resenha.copy(id = novoRef.key ?: "")
        novoRef.setValue(resenhaComId).await()
    }

    // ─── REMOVER RESENHA (só admin) ──────────────────────────────────
    suspend fun removerResenha(resenhaId: String) {
        db.child("resenhas").child(resenhaId).removeValue().await()
    }

    // ─── LISTAR RESENHAS DE UM LIVRO ─────────────────────────────────
    suspend fun listarResenhasDeLivro(livroId: String): List<Resenha> =
        suspendCancellableCoroutine { cont ->
            db.child("resenhas")
                .orderByChild("livroId")
                .equalTo(livroId)
                .addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val lista = snapshot.children.mapNotNull { snap ->
                                snap.getValue(Resenha::class.java)
                            }
                            cont.resume(lista)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            cont.resumeWithException(error.toException())
                        }
                    }
                )
        }
}