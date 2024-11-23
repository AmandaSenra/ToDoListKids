package com.example.todolistkids

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class Tela9_Historico: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var historicoLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela9_historico)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tela9)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Inicializa Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referências a views
        nomeTextView = findViewById(R.id.exibirnomebanco4)
        historicoLayout = findViewById(R.id.historicoLayout)


        // Intents para navegação
        findViewById<ImageView>(R.id.logo5).setOnClickListener {
            startActivity(Intent(this, Tela5_Home::class.java))
            finish()
        }
        findViewById<ImageView>(R.id.hamburguer4).setOnClickListener {
            startActivity(Intent(this, Tela6_Menu::class.java))
            finish()
        }

        carregarNome()
        carregarHistorico()
    }

    private fun carregarNome() {
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("usuarios").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val nome = documentSnapshot.getString("name")
                    nomeTextView.text = "Olá, $nome"
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao recuperar o nome: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun carregarHistorico() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Define o formato original da data (dd/MM/yyyy)
            val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            // Define o formato de exibição desejado (d 'de' MMMM 'de' yyyy)
            val displayFormat = SimpleDateFormat(" 'Dia' d 'de' MMMM 'de' yyyy", Locale("pt", "BR"))

            // Busca o histórico completo na subcoleção "historico" do usuário
            firestore.collection("usuarios").document(userId).collection("historico")
                .get()
                .addOnSuccessListener { snapshot ->
                    // Verifica se há documentos na subcoleção "historico"
                    if (snapshot.isEmpty) {
                        Toast.makeText(this, "Nenhum histórico encontrado.", Toast.LENGTH_SHORT).show()
                    } else {
                        snapshot.documents.forEach { document ->
                            val data = document.getString("data") ?: "Data desconhecida"
                            val tipo = document.getString("tipo") ?: ""
                            val detalhes = document.get("detalhes") as? Map<String, Any> ?: emptyMap()

                            // Converte a data do formato original para o formato desejado
                            val formattedDate = try {
                                val parsedDate = originalFormat.parse(data)
                                displayFormat.format(parsedDate)
                            } catch (e: Exception) {
                                "Data inválida"
                            }

                            // Verifica o tipo de histórico e prepara a mensagem
                            val mensagem = when (tipo) {
                                "resgate" -> {
                                    val quantidadeEstrelas = (detalhes["quantidadeEstrelas"] as? Long) ?: 0
                                    "Resgatou $quantidadeEstrelas estrelas."
                                }
                                "doacao" -> {
                                    val amigo = detalhes["amigo"] as? String ?: "Amigo desconhecido"
                                    val estrelasDoadas = (detalhes["estrelasDoadas"] as? Long) ?: 0
                                    "Doou $estrelasDoadas estrela para $amigo."
                                }
                                else -> "Histórico desconhecido"
                            }

                            // Adiciona o item ao layout
                            adicionarItemHistorico(mensagem, formattedDate)
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar histórico: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }



    private fun adicionarItemHistorico(mensagem: String, data: String) {
        val itemView = LayoutInflater.from(this).inflate(R.layout.historico_item, historicoLayout, false)

        itemView.findViewById<TextView>(R.id.historicoData).text = data
        itemView.findViewById<TextView>(R.id.historicoMensagem).text = mensagem

        historicoLayout.addView(itemView)
    }
}