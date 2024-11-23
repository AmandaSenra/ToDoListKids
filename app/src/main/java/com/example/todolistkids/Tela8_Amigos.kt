package com.example.todolistkids

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Tela8_Amigos : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var nomeTextView: TextView
    private lateinit var amigosLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela8_amigos)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tela8)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configura para a cor de fundo preencher a barra de notificações
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        window.statusBarColor = Color.TRANSPARENT // Deixa a barra de notificações transparente

        // Inicializa Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referências a views
        nomeTextView = findViewById(R.id.exibirnomebanco3)
        amigosLayout = findViewById(R.id.amigosLayout)

        // Intents para navegação
        findViewById<ImageView>(R.id.logo4).setOnClickListener {
            startActivity(Intent(this, Tela5_Home::class.java))
            finish()
        }
        findViewById<ImageView>(R.id.hamburguer3).setOnClickListener {
            startActivity(Intent(this, Tela6_Menu::class.java))
            finish()
        }

        carregarNome()
        carregarAmigos()

        // Buscar usuários para adicionar amigos
        findViewById<Button>(R.id.buscar).setOnClickListener {
            buscarUsuarios()
        }
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

    private fun buscarUsuarios() {
        val currentUserUid = auth.currentUser?.uid // Pega o uid do usuário logado

        firestore.collection("usuarios").get()
            .addOnSuccessListener { result ->
                // Filtra os nomes para remover o usuário logado, comparando com o UID
                val nomesUsuarios = result.documents
                    .filter { it.id != currentUserUid } // Exclui o documento do usuário logado
                    .mapNotNull { it.getString("name") }

                if (nomesUsuarios.isNotEmpty()) {
                    val dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
                    dialogBuilder.setTitle("Adicione um amigo!")

                    dialogBuilder.setItems(nomesUsuarios.toTypedArray()) { _, which ->
                        val nomeEscolhido = nomesUsuarios[which]
                        adicionarAmigo(nomeEscolhido)
                    }

                    dialogBuilder.setNegativeButton("Cancelar", null)
                    dialogBuilder.create().show()
                } else {
                    Toast.makeText(this, "Nenhum usuário encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao buscar usuários: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun adicionarAmigo(nome: String) {
        if (!isAmigoAdicionado(nome)) {
            val userId = auth.currentUser?.uid
            if (userId != null) {
                // Salvar no Firebase
                val amigoData = mapOf("name" to nome)
                firestore.collection("usuarios").document(userId)
                    .collection("amigos")
                    .add(amigoData)
                    .addOnSuccessListener {
                        adicionarAmigoLayout(nome) // Adiciona ao layout após salvar
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao salvar amigo: ${e.message}", Toast.LENGTH_LONG).show()
                    }
            }
        } else {
            Toast.makeText(this, "$nome já foi adicionado.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removerAmigo(nome: String, amigoView: View) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val amigosRef = firestore.collection("usuarios").document(userId).collection("amigos")
            amigosRef.whereEqualTo("name", nome).get()
                .addOnSuccessListener { querySnapshot ->
                    querySnapshot.documents.forEach { document ->
                        amigosRef.document(document.id).delete()
                            .addOnSuccessListener {
                                amigosLayout.removeView(amigoView)
                                Toast.makeText(this, "$nome removido com sucesso.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao remover amigo: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao buscar amigo: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun carregarAmigos() {
        auth.currentUser?.uid?.let { userId ->
            firestore.collection("usuarios").document(userId)
                .collection("amigos")
                .get()
                .addOnSuccessListener { result ->
                    result.documents.mapNotNull { it.getString("name") }
                        .forEach { nome -> adicionarAmigoLayout(nome) }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar amigos: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun adicionarAmigoLayout(nome: String) {
        val amigoView = LayoutInflater.from(this).inflate(R.layout.amigo_item, amigosLayout, false)

        amigoView.findViewById<TextView>(R.id.nomeAmigo).text = nome
        amigoView.findViewById<ImageView>(R.id.iconUsuario).setImageResource(R.drawable.userr)
        amigoView.findViewById<ImageView>(R.id.iconDelete).setOnClickListener {
            removerAmigo(nome, amigoView)
        }

        amigosLayout.addView(amigoView)
    }

    private fun isAmigoAdicionado(nome: String): Boolean {
        return (0 until amigosLayout.childCount).map { amigosLayout.getChildAt(it) }
            .any { (it.findViewById<TextView>(R.id.nomeAmigo)?.text ?: "") == nome }
    }
}
