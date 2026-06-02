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
    // URL explicitamente definida para garantir a conexão
    private val db = Firebase.database("https://uniforlibrary-30c5c-default-rtdb.firebaseio.com/").reference
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

    suspend fun listarLivros(): List<Livro> {
        val snapshot = db.child("livros").get().await()
        return snapshot.children.mapNotNull { it.getValue(Livro::class.java) }
    }

    suspend fun obterLivro(id: String): Livro? {
        val snapshot = db.child("livros").child(id).get().await()
        return snapshot.getValue(Livro::class.java)
    }
}