package com.example.todolistkids

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class Tela4_CriarConta : AppCompatActivity() {

    // Declaração das variáveis para o Firebase Auth e Database
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela4_criar_conta)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tela4)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa o Firebase Auth e Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Configura o botão de registro
        val registre = findViewById<Button>(R.id.registre)
        registre.setOnClickListener {
            val nome = findViewById<EditText>(R.id.nomeTextoR).text.toString()
            val email = findViewById<EditText>(R.id.emailtextoR).text.toString()
            val aniversario = findViewById<EditText>(R.id.dataTextoR).text.toString()
            val senha = findViewById<EditText>(R.id.senhatextoR).text.toString()

            // Verifica se os campos estão preenchidos
            if (nome.isNotEmpty() && email.isNotEmpty() && aniversario.isNotEmpty() && senha.isNotEmpty()) {
                registerUser(nome, email, aniversario, senha)
            } else {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun registerUser(nome: String, email: String, aniversario: String, senha: String) {
        // Cria o usuário no Firebase Authentication
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Usuário registrado com sucesso no Firebase Authentication
                    val userId = auth.currentUser?.uid
                    saveUserData(userId, nome, email, aniversario)

                    Toast.makeText(this, "Registrado com sucesso!", Toast.LENGTH_SHORT).show()
                    // Redireciona para a página de login ou home
                    startActivity(Intent(this, Tela3_Login::class.java))
                    finish()
                } else {
                    // Falha no registro
                    Toast.makeText(
                        this,
                        "Erro no registro: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun saveUserData(userId: String?, nome: String, email: String, aniversario: String) {
        if (userId != null) {
            // Estrutura dos dados do usuário
            val userData = hashMapOf(
                "nome" to nome,
                "email" to email,
                "dataNascimento" to aniversario
            )
            // Salva os dados do usuário no Firestore
            firestore.collection("usuarios").document(userId)
                .set(userData)
                .addOnSuccessListener {
                    // Dados salvos com sucesso
                    Toast.makeText(this, "Registrado com sucesso!", Toast.LENGTH_SHORT).show()

                    // Redireciona para a página de login ou home
                    startActivity(Intent(this, Tela3_Login::class.java))
                    finish()
                }
                .addOnFailureListener { e ->
                    // Erro ao salvar os dados
                    Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}