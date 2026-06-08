package com.example.chatbox

import android.net.Uri
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
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
    private val auth = Firebase.auth

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

    suspend fun atualizarLivro(livro: Livro) {
        db.child("livros").child(livro.id).setValue(livro).await()
    }

    suspend fun removerLivro(livroId: String) {
        db.child("livros").child(livroId).removeValue().await()
    }

    suspend fun listarLivros(): List<Livro> = suspendCancellableCoroutine { continuation ->
        db.child("livros").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val livros = snapshot.children.mapNotNull { it.getValue(Livro::class.java) }
                continuation.resume(livros)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    suspend fun buscarLivroPorId(id: String): Livro? = suspendCancellableCoroutine { continuation ->
        db.child("livros").child(id).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val livro = snapshot.getValue(Livro::class.java)
                continuation.resume(livro)
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }

    suspend fun salvarAvaliacao(avaliacao: Avaliacao) {
        val currentUser = auth.currentUser 
        if (currentUser == null) {
            throw Exception("Usuário não autenticado. Faça login para avaliar.")
        }
        
        val userId = currentUser.uid
        
        // Buscar nome do usuário no banco
        val userSnapshot = db.child("users").child(userId).get().await()
        val nome = userSnapshot.child("nome").getValue(String::class.java) ?: "Usuário"
        
        val avaliacaoComUser = avaliacao.copy(usuarioId = userId, nomeUsuario = nome)
        
        // Alterado para 'resenhas' para coincidir com as regras do Firebase
        val ref = db.child("resenhas").push()
        val avaliacaoComId = avaliacaoComUser.copy(id = ref.key ?: "")
        ref.setValue(avaliacaoComId).await()
        
        // Atualizar a média do livro
        atualizarMediaLivro(avaliacao.livroId)
    }

    private suspend fun atualizarMediaLivro(livroId: String) {
        // Busca todas as resenhas que possuem o livroId correspondente
        val snapshot = db.child("resenhas")
            .orderByChild("livroId")
            .equalTo(livroId)
            .get().await()
            
        val resenhas = snapshot.children.mapNotNull { it.getValue(Avaliacao::class.java) }
        if (resenhas.isNotEmpty()) {
            val media = resenhas.map { it.nota }.average()
            db.child("livros").child(livroId).child("mediaNota").setValue(media).await()
        }
    }
    
    suspend fun buscarMediaLivro(livroId: String): Double = suspendCancellableCoroutine { continuation ->
        db.child("livros").child(livroId).child("mediaNota").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val media = snapshot.getValue(Double::class.java) ?: 0.0
                continuation.resume(media)
            }
            override fun onCancelled(error: DatabaseError) {
                continuation.resume(0.0)
            }
        })
    }
}
