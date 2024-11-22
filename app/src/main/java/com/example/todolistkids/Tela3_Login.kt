package com.example.todolistkids

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class Tela3_Login : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firestore: FirebaseFirestore

    companion object {
        private const val RC_SIGN_IN = 9001  // Definindo o código de solicitação para o login com Google
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela3_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tela3)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Referências aos elementos da interface
        val emailTexto = findViewById<EditText>(R.id.emailTexto)
        val senhaTexto = findViewById<EditText>(R.id.senhaTexto)
        val entrarButton = findViewById<Button>(R.id.entrar)
        val loginGoogleButton = findViewById<Button>(R.id.loginGoogle)
        val criarContaButton = findViewById<Button>(R.id.criarConta)

        // Evento de clique para autenticação com e-mail e senha
        entrarButton.setOnClickListener {
            val email = emailTexto.text.toString()
            val senha = senhaTexto.text.toString()
            if (email.isNotEmpty() && senha.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            // Redireciona para a tela principal do app
                            startActivity(Intent(this, Tela5_Home::class.java))
                        } else {
                            Toast.makeText(this, "Falha no login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }

        // Evento de clique para autenticação com o Google
        loginGoogleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // Redirecionamento para a tela de criação de conta
        criarContaButton.setOnClickListener {
            startActivity(Intent(this, Tela4_CriarConta::class.java))
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API...")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.result
                account?.let {
                    val credential = GoogleAuthProvider.getCredential(it.idToken, null)
                    auth.signInWithCredential(credential)
                        .addOnCompleteListener { authTask ->
                            if (authTask.isSuccessful) {
                                val user = auth.currentUser
                                val userId = user?.uid

                                if (userId != null) {
                                    val userRef = firestore.collection("usuarios").document(userId)
                                    userRef.get().addOnSuccessListener { document: DocumentSnapshot ->
                                        if (document.exists()) {
                                            startActivity(Intent(this, Tela5_Home::class.java))
                                        } else {
                                            val userData = hashMapOf(
                                                "name" to it.displayName,
                                                "email" to it.email,
                                                "dataNascimento" to "16/07/1998",
                                                "photoUrl" to it.photoUrl.toString()
                                            )
                                            userRef.set(userData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(this, "Dados do Google salvos com sucesso!", Toast.LENGTH_SHORT).show()
                                                    startActivity(Intent(this, Tela5_Home::class.java))
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_LONG).show()
                                                }
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(this, "Falha no login com Google", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            } else {
                Toast.makeText(this, "Erro ao realizar login com Google", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
