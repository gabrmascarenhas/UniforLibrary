package com.example.chatbox

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PontoItemCustodio(
    val nome: String = "",
    val pontos: String = ""
)
