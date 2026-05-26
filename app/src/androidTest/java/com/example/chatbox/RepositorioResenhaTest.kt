package com.example.chatbox

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RepositorioResenhaTest {

    private val repositorio = RepositorioResenha()

    @Test
    fun testarAdicaoDeResenha() = runBlocking {
        // 1. Criar uma resenha de teste
        val resenhaTeste = Resenha(
            livroId = "livro_teste_id",
            userId = "usuario_teste_id",
            nomeUsuario = "Usuario Teste",
            texto = "Esta é uma resenha de teste."
        )

        // 2. Adicionar ao banco
        try {
            repositorio.adicionarResenha(resenhaTeste)
            
            // 3. Buscar as resenhas do livro para ver se ela aparece lá
            val resenhas = repositorio.listarResenhasDeLivro("livro_teste_id")
            
            // 4. Verificar se existe a resenha que criamos
            val encontrou = resenhas.any { it.texto == resenhaTeste.texto }
            
            assertTrue("A resenha deveria ter sido encontrada no banco de dados", encontrou)
            println("Sucesso! Resenha inserida e verificada.")
            
        } catch (e: Exception) {
            assertTrue("Erro ao interagir com Firebase: ${e.message}", false)
        }
    }
}
