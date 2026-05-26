package com.example.chatbox

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositorioLivrosTest {

    private val repositorio = RepositorioLivros()

    @Test
    fun testarAdicaoDeLivro() = runBlocking {
        // 1. Criar um livro de teste
        val livroTeste = Livro(
            titulo = "Livro de Teste " + System.currentTimeMillis(),
            autor = "Autor Teste",
            ano = 2024,
            sinopse = "Esta é uma sinopse de teste para verificar o Firebase."
        )

        // 2. Adicionar ao banco
        try {
            repositorio.adicionarLivro(livroTeste)
            
            // 3. Buscar a lista para ver se ele aparece lá
            val livros = repositorio.listarLivros()
            
            // 4. Verificar se existe algum livro com o título que criamos
            val encontrou = livros.any { it.titulo == livroTeste.titulo }
            
            assertTrue("O livro deveria ter sido encontrado no banco de dados", encontrou)
            println("Sucesso! Livro inserido e verificado.")
            
        } catch (e: Exception) {
            assertTrue("Erro ao interagir com Firebase: ${e.message}", false)
        }
    }
}
