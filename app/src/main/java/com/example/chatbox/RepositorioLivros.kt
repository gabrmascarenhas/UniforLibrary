package com.example.chatbox

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.storage.storage
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.util.UUID
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class RepositorioLivros {
    private val db = Firebase.database.reference
    private val storage = Firebase.storage.reference

    suspend fun uploadArquivo(uri: Uri, pasta: String): String {
        val ref = storage.child("$pasta/${UUID.randomUUID()}")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
    suspend fun adicionarLivro(livro: Livro) {
        val novoRef = db.child("livros").push()
        val livroComId = livro.copy(id = novoRef.key ?: "")
        novoRef.setValue(livroComId).await()
    }

    suspend fun removerLivro(livroId: String) {
        db.child("livros").child(livroId).removeValue().await()
    }

    suspend fun atualizarAvaliacao(livroId: String, novaAvaliacao: Float) {
        db.child("livros").child(livroId).child("avaliacao").setValue(novaAvaliacao).await()
    }

    suspend fun listarLivros(): List<Livro> = suspendCancellableCoroutine { cont ->
        db.child("livros").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lista = snapshot.children.mapNotNull { it.getValue(Livro::class.java) }
                cont.resume(lista)
            }
            override fun onCancelled(error: DatabaseError) {
                cont.resumeWithException(error.toException())
            }
        })
    }
}