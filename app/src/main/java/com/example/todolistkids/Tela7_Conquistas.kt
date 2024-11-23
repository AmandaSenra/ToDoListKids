package com.example.todolistkids

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Tela7_Conquistas : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var voceTemTextView: TextView


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
        voceTemTextView = findViewById(R.id.voceTem)

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
        carregarEstrelas()
        observarEstrelas()

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
            firestore.collection("usuarios").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val estrelas = documentSnapshot.getLong("estrelas") ?: 0

                    if (estrelas < 1) {
                        Toast.makeText(this, "Você não tem estrelas suficientes para doar.", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    // Recupera a lista de amigos do Firestore
                    firestore.collection("usuarios").document(userId).collection("amigos").get()
                        .addOnSuccessListener { result ->
                            val nomesAmigos = result.documents.mapNotNull { it.getString("name") }
                            if (nomesAmigos.isNotEmpty()) {
                                val dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                                dialogBuilder.setTitle("✨ Escolha um amigo para doar:")
                                dialogBuilder.setItems(nomesAmigos.toTypedArray()) { _, which ->
                                    val amigoSelecionado = nomesAmigos[which]
                                    realizarDoacao(amigoSelecionado, estrelas)
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
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar estrelas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun realizarDoacao(amigo: String, estrelasDisponiveis: Long) {
        if (estrelasDisponiveis >= 1) {
            Toast.makeText(this, "1 estrela doada para $amigo!", Toast.LENGTH_SHORT).show()

            val userId = auth.currentUser?.uid
            if (userId != null) {
                val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                val historico = mapOf(
                    "tipo" to "doacao",
                    "data" to dataAtual,
                    "detalhes" to mapOf(
                        "amigo" to amigo,
                        "estrelasDoadas" to 1
                    )
                )

                // Salvar no Firestore no campo único "historico"
                firestore.collection("usuarios").document(userId).collection("historico")
                    .add(historico)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Histórico de doação salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao salvar histórico: ${e.message}", Toast.LENGTH_LONG).show()
                    }

                // Atualizar o número de estrelas e o campo DoaçãoEstrelas
                val userRef = firestore.collection("usuarios").document(userId)
                firestore.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val doacaoEstrelas = snapshot.getLong("DoaçãoEstrelas") ?: 0
                    val estrelasAtualizadas = estrelasDisponiveis - 1

                    transaction.update(userRef, mapOf(
                        "DoaçãoEstrelas" to doacaoEstrelas + 1 // Incrementa 1 estrela no campo DoaçãoEstrelas
                    ))
                }.addOnSuccessListener {
                    Log.d("Firestore", "Estrelas e DoaçãoEstrelas atualizadas após doação.")
                }.addOnFailureListener { e ->
                    Log.e("Firestore", "Erro ao atualizar estrelas: ${e.message}")
                }
            } else {
                Toast.makeText(this, "Usuário não autenticado. Não foi possível salvar o histórico.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Você não possui estrelas suficientes para doar.", Toast.LENGTH_SHORT).show()
        }
    }


    private fun exibirDialogResgatarEstrelas() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = firestore.collection("usuarios").document(userId)

            userRef.get().addOnSuccessListener { documentSnapshot ->
                val estrelas = documentSnapshot.getLong("estrelas") ?: 0
                val resgateEstrelas = documentSnapshot.getLong("ResgateEstrelas") ?: 0
                val estrelasDisponiveis = estrelas - resgateEstrelas

                if (estrelasDisponiveis <= 0) {
                    Toast.makeText(this, "Você não tem estrelas suficientes para resgatar.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                builder.setTitle("✨ Quantas estrelas você deseja resgatar?")

                // Campo de input para o número de estrelas
                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_NUMBER
                builder.setView(input)

                builder.setPositiveButton("Resgatar") { _, _ ->
                    val quantidadeEstrelas = input.text.toString()
                    if (quantidadeEstrelas.isNotEmpty() && quantidadeEstrelas.toIntOrNull() != null) {
                        val quantidade = quantidadeEstrelas.toInt()
                        if (quantidade in 1..estrelasDisponiveis) {
                            // Atualiza o histórico e o campo ResgateEstrelas
                            salvarHistoricoResgate(userId, quantidade, estrelas, resgateEstrelas)
                            Toast.makeText(this, "$quantidade estrelas resgatadas com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Por favor, insira um número válido entre 1 e $estrelasDisponiveis.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Por favor, insira um número válido.", Toast.LENGTH_SHORT).show()
                    }
                }

                builder.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }

                builder.show()
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao carregar estrelas: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun salvarHistoricoResgate(userId: String, quantidadeEstrelas: Int, estrelas: Long, resgateEstrelas: Long) {
        val dataAtual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        val historico = mapOf(
            "tipo" to "resgate",
            "data" to dataAtual,
            "detalhes" to mapOf(
                "quantidadeEstrelas" to quantidadeEstrelas
            )
        )

        // Salvar no Firestore no campo único "historico"
        firestore.collection("usuarios").document(userId).collection("historico")
            .add(historico)
            .addOnSuccessListener {
                Toast.makeText(this, "Histórico de resgate salvo!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao salvar histórico: ${e.message}", Toast.LENGTH_LONG).show()
            }


        // Atualizar o campo ResgateEstrelas
        val userRef = firestore.collection("usuarios").document(userId)
        userRef.update(
            mapOf(
                "ResgateEstrelas" to resgateEstrelas + quantidadeEstrelas
            )
        ).addOnSuccessListener {
            Log.d("Firestore", "ResgateEstrelas atualizado com sucesso.")
            carregarEstrelas() // Atualiza a interface após a mudança
        }.addOnFailureListener { e ->
            Log.e("Firestore", "Erro ao atualizar ResgateEstrelas: ${e.message}")
        }
    }

    private fun carregarEstrelas() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("usuarios").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val estrelas = documentSnapshot.getLong("estrelas") ?: 0
                    val resgateEstrelas = documentSnapshot.getLong("ResgateEstrelas") ?: 0
                    val doacaoEstrelas = documentSnapshot.getLong("DoaçãoEstrelas") ?: 0

                    val totalEstrelas = estrelas - resgateEstrelas - doacaoEstrelas

                    voceTemTextView.text = "Você tem $totalEstrelas \nestrelas disponíveis!"
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar estrelas: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }


    private fun observarEstrelas() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("usuarios").document(userId)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("Firestore", "Erro ao escutar mudanças: ${e.message}")
                        return@addSnapshotListener
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val estrelas = snapshot.getLong("estrelas") ?: 0
                        val resgateEstrelas = snapshot.getLong("ResgateEstrelas") ?: 0
                        val doacaoEstrelas = snapshot.getLong("DoaçãoEstrelas") ?: 0

                        val totalEstrelas = estrelas - resgateEstrelas - doacaoEstrelas

                        voceTemTextView.text = "Você tem $totalEstrelas estrelas disponíveis!"
                    }
                }
        }
    }
}
