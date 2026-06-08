package com.example.chatbox

data class Avaliacao(
    val id: String = "",
    val livroId: String = "",
    val usuarioId: String = "",
    val nota: Int = 0,
    val comentario: String = "",
    val data: Long = System.currentTimeMillis()
)
