package com.example.chatbox

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch

class ChatBoxActivity : AppCompatActivity() {

    // ⚠️ COLOQUE SUA API KEY DO GOOGLE AI STUDIO AQUI
    private val API_KEY = "AQ.Ab8RN6LjcPt-XORz9IOt--9Ax0lZVUZlUlnu3GjuojbDDwOmlg"

    private val generativeModel = GenerativeModel(
        // ALTERADO: Mudamos para o Gemini 1.5 Pro para testar se a rota resolve o seu problema
        modelName = "gemini-1.5-pro",
        apiKey = API_KEY,
        systemInstruction = content {
            text(
                "Você é o SabedorIA, um bibliotecário virtual inteligente da Unifor Library. " +
                        "Responda sobre livros, recomende leituras e explique temas literários de forma educada e clara."
            )
        }
    )

    private val chat = generativeModel.startChat()
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var adapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chatbox)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rvChat = findViewById<RecyclerView>(R.id.rvChat)
        val etMessage = findViewById<EditText>(R.id.editTextTextMultiLine)
        val btnSend = findViewById<ImageButton>(R.id.imageButton4)

        adapter = ChatAdapter(messageList)
        rvChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        rvChat.adapter = adapter

        addMessage("Olá! Sou o SabedorIA, seu bibliotecário virtual. Como posso te ajudar?", false)

        findViewById<ImageButton>(R.id.btnBackChat).setOnClickListener {
            startActivity(Intent(this, LibraryHomeActivity::class.java))
            finish()
        }

        btnSend.setOnClickListener {
            val message = etMessage.text.toString().trim()

            if (message.isEmpty()) return@setOnClickListener

            if (API_KEY == "SUA_NOVA_API_KEY_AQUI" || API_KEY.isEmpty()) {
                Toast.makeText(this, "Por favor, insira uma API KEY válida.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            sendMessage(message, etMessage, rvChat)
        }
    }

    private fun sendMessage(message: String, etInput: EditText, rv: RecyclerView) {
        addMessage(message, true)
        etInput.text.clear()

        lifecycleScope.launch {
            try {
                val response = chat.sendMessage(message)
                val aiResponse = response.text ?: "Não consegui gerar resposta no momento."

                addMessage(aiResponse, false)
                rv.scrollToPosition(messageList.size - 1)

            } catch (e: Exception) {
                Log.e("GEMINI_ERROR", "Mensagem: ${e.message}", e)

                val erroDetalhado = e.localizedMessage ?: e.message ?: "Erro desconhecido"
                addMessage(
                    "❌ ERRO REAL: $erroDetalhado",
                    false
                )
                rv.scrollToPosition(messageList.size - 1)
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        messageList.add(ChatMessage(text, isUser))
        adapter.notifyItemInserted(messageList.size - 1)

        findViewById<RecyclerView>(R.id.rvChat)
            .scrollToPosition(messageList.size - 1)
    }
}