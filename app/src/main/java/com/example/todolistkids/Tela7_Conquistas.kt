package com.example.todolistkids

import android.content.Intent
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
}
