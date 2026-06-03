package com.example.chatbox

class RepositorioLoja {
    fun obterItens(): List<PontoItemCustodio> {
        return listOf(
            PontoItemCustodio("Mochila UNIFOR", "4000 pontos"),
            PontoItemCustodio("Camisa UNIFOR", "3000 pontos"),
            PontoItemCustodio("Caneca UNIFOR", "2000 pontos"),
            PontoItemCustodio("Comprar livro", "10000 pontos"),
            PontoItemCustodio("Digitalizar livro", "1000 pontos"),
            PontoItemCustodio("Cafézinho", "100 pontos")
        )
    }
}