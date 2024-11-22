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
            val historicoList = mutableListOf<Map<String, Any>>() // Lista para armazenar dados de ambos os históricos

            // Define o formato original da data (dd/MM/yyyy)
            val originalFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            // Define o formato de exibição desejado (d 'de' MMMM 'de' yyyy)
            val displayFormat = SimpleDateFormat(" 'Dia' d 'de' MMMM 'de' yyyy", Locale("pt", "BR"))

            // Busca os resgates
            firestore.collection("usuarios").document(userId).collection("historicoResgates")
                .get()
                .addOnSuccessListener { resgates ->
                    resgates.documents.forEach { document ->
                        val data = document.getString("data") ?: "Data desconhecida"
                        val quantidade = document.getLong("quantidadeEstrelas") ?: 0

                        // Converte a data do formato original para o formato desejado
                        val formattedDate = try {
                            val parsedDate = originalFormat.parse(data)
                            displayFormat.format(parsedDate)
                        } catch (e: Exception) {
                            "Data inválida"
                        }

                        // Adiciona resgate na lista
                        historicoList.add(mapOf("mensagem" to "Resgatou $quantidade estrelas.", "data" to formattedDate, "tipo" to "resgate"))
                    }

                    // Busca as doações
                    firestore.collection("usuarios").document(userId).collection("historicoDoacoes")
                        .get()
                        .addOnSuccessListener { doacoes ->
                            doacoes.documents.forEach { document ->
                                val data = document.getString("data") ?: "Data desconhecida"
                                val amigo = document.getString("amigo") ?: "Amigo desconhecido"

                                // Converte a data do formato original para o formato desejado
                                val formattedDate = try {
                                    val parsedDate = originalFormat.parse(data)
                                    displayFormat.format(parsedDate)
                                } catch (e: Exception) {
                                    "Data inválida"
                                }

                                // Adiciona doação na lista
                                historicoList.add(mapOf("mensagem" to "Doou 1 estrela para $amigo.", "data" to formattedDate, "tipo" to "doacao"))
                            }

                            // Exibe os dados no layout na ordem original
                            historicoList.forEach { historico ->
                                adicionarItemHistorico(historico["mensagem"].toString(), historico["data"].toString())
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Erro ao carregar histórico de doações: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar histórico de resgates: ${e.message}", Toast.LENGTH_LONG).show()
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