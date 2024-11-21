package com.example.todolistkids

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Tela7_Conquistas : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela7_conquistas)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tela7)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa as instâncias do FirebaseAuth e FirebaseFirestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        nomeTextView = findViewById(R.id.exibirnomebanco2)

        // Botão de resgatar estrelas
        val resgatarButton = findViewById<Button>(R.id.resgatar)
        resgatarButton.setOnClickListener {
            exibirDialogResgatarEstrelas()
        }

        //Intent para a logo retornar a tela home
        val logo = findViewById<ImageView>(R.id.logo3)
        logo.setOnClickListener {
            val intent = Intent(this, Tela5_Home::class.java)
            startActivity(intent)
            finish()
        }

        //Intent para o hamburguer retornar a tela home
        val hamburguer = findViewById<ImageView>(R.id.hamburguer2)
        hamburguer.setOnClickListener {
            val intent = Intent(this, Tela6_Menu::class.java)
            startActivity(intent)
            finish()
        }

        val imageView = findViewById<ImageView>(R.id.star)

        // Carregar a animação de rotação
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)

        // Aplicar a animação na imagem
        imageView.startAnimation(rotateAnimation)

        // Botão doar
        val doarButton = findViewById<Button>(R.id.doar)
        doarButton.setOnClickListener {
            exibirDialogDeAmigos()
        }

        carregarNome()

    }

    private fun carregarNome() {
        // Recupera o ID do usuário autenticado
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Recupera o nome do usuário do Firestore
            firestore.collection("usuarios").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val nome = documentSnapshot.getString("name")

                    if (nome != null) {
                        // Exibe o nome no TextView
                        nomeTextView.text = "Olá, $nome"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao recuperar o nome: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun exibirDialogDeAmigos() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("usuarios").document(userId).collection("amigos").get()
                .addOnSuccessListener { result ->
                    val nomesAmigos = result.documents.mapNotNull { it.getString("name") }
                    if (nomesAmigos.isNotEmpty()) {
                        val dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                        dialogBuilder.setTitle("Escolha um amigo para doar:")
                        dialogBuilder.setItems(nomesAmigos.toTypedArray()) { _, which ->
                            val amigoSelecionado = nomesAmigos[which]
                            realizarDoacao(amigoSelecionado)
                        }
                        dialogBuilder.setNegativeButton("Cancelar", null)
                        dialogBuilder.create().show()
                    } else {
                        Toast.makeText(this, "Você não possui amigos cadastrados.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar amigos: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun realizarDoacao(amigo: String) {
        Toast.makeText(this, "Doação feita para $amigo!", Toast.LENGTH_SHORT).show()
        // Adicione lógica de doação aqui
    }

    private fun exibirDialogResgatarEstrelas() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        builder.setTitle("Quantas estrelas você deseja resgatar?")

        // Criação do campo para entrada de número de estrelas
        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_NUMBER // Somente números
        builder.setView(input)

        builder.setPositiveButton("Resgatar") { dialog, _ ->
            val quantidadeEstrelas = input.text.toString()
            if (quantidadeEstrelas.isNotEmpty() && quantidadeEstrelas.toIntOrNull() != null) {
                val quantidade = quantidadeEstrelas.toInt()
                // Exibir a mensagem de sucesso
                Toast.makeText(this, "Parabéns, $quantidade estrelas resgatadas com sucesso!", Toast.LENGTH_SHORT).show()

                // Salvar no banco de dados
                salvarHistoricoResgate(quantidade)

                dialog.dismiss()
            } else {
                Toast.makeText(this, "Por favor, insira um número válido.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun salvarHistoricoResgate(quantidadeEstrelas: Int) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

            val historico = mapOf(
                "data" to dataAtual,
                "quantidadeEstrelas" to quantidadeEstrelas
            )

            // Salvar no Firestore
            firestore.collection("usuarios").document(userId).collection("historicoResgates")
                .add(historico)
                .addOnSuccessListener {
                    Toast.makeText(this, "Histórico de resgate salvo!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar histórico: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
