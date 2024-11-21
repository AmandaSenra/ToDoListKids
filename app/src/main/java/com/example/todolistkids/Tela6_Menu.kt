package com.example.todolistkids

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.app.Activity
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Tela6_Menu : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var idadeTextView: TextView
    private lateinit var nomeTextView: TextView
    private lateinit var editnome: TextView
    private lateinit var imageView2: ImageView
    private lateinit var editfoto: TextView
    private val PICK_IMAGE_REQUEST = 1 //Abrir Galeria (Solicitação de permissão)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela6_menu)

        // Inicializa as instâncias do FirebaseAuth e FirebaseFirestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Referências aos TextViews
        idadeTextView = findViewById(R.id.idade)
        nomeTextView = findViewById(R.id.exibirnomebanco)
        editnome = findViewById(R.id.nome)
        imageView2 = findViewById(R.id.imageView2)
        editfoto = findViewById(R.id.editfoto)


        // Ajuste para a interface com barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tela6)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Configurar clique no TextView para editar o nome
        editnome.setOnClickListener {
            mostrarDialogEditarNome()
        }

        // Configurar clique para abrir a galeria
        editfoto.setOnClickListener {
            abrirGaleria()
        }

        // Carregar a idade e nome do usuário logado
        carregarIdade()
        carregarNome()
        carregarFotoUsuario()


        //Intent para a logo retornar a tela home
        val logo = findViewById<ImageView>(R.id.logo2)
        logo.setOnClickListener {
            val intent = Intent(this, Tela5_Home::class.java)
            startActivity(intent)
            finish()
        }

        //Intent para o hamburguer retornar a tela menu
        val hamburguer = findViewById<ImageView>(R.id.hamburguer)
        hamburguer.setOnClickListener {
            val intent = Intent(this, Tela6_Menu::class.java)
            startActivity(intent)
            finish()
        }

        //Itent para deslogar do app e ir para tela de login
        val sair = findViewById<TextView>(R.id.sair)
        sair.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Desloga o usuário
            Toast.makeText(this, "Deslogado com sucesso!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Tela3_Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        //Intent para ir para tela de conquistas
        val conquista = this.findViewById<TextView>(R.id.minhasconquistas)
        conquista.setOnClickListener {
            val intent = Intent(this, Tela7_Conquistas::class.java)
            startActivity(intent)
        }
        //Intent para ir para tela de amigos
        val amigo = this.findViewById<TextView>(R.id.meusamigos)
        amigo.setOnClickListener {
            val intent = Intent(this, Tela8_Amigos::class.java)
            startActivity(intent)
        }
        //Intent para ir para tela de históricos
        val historico = this.findViewById<TextView>(R.id.resgate)
        historico.setOnClickListener {
            val intent = Intent(this, Tela9_Historico::class.java)
            startActivity(intent)
        }
    }

    private fun mostrarDialogEditarNome() {
        val builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        builder.setTitle("Editar Nome")

        // Criar um campo de entrada
        val input = EditText(this)
        input.hint = "Digite o novo nome"
        builder.setView(input)

        // Configurar os botões
        builder.setPositiveButton("Salvar") { dialog, _ ->
            val novoNome = input.text.toString()
            if (novoNome.isNotBlank()) {
                atualizarNomeNoBanco(novoNome) // Atualizar o nome no banco
            } else {
                Toast.makeText(this, "O nome não pode estar vazio", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun atualizarNomeNoBanco(novoNome: String) {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val userRef = firestore.collection("usuarios").document(userId)

            // Atualizar o campo "name" no Firestore
            userRef.update("name", novoNome)
                .addOnSuccessListener {
                    // Atualizar o nome no TextView localmente
                    nomeTextView.text = "Olá, $novoNome"
                    Toast.makeText(this, "Nome atualizado com sucesso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao atualizar o nome: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        }
    }


    private fun carregarIdade() {
        // Recupera o ID do usuário autenticado
        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Recupera a data de nascimento do usuário do Firestore
            firestore.collection("usuarios").document(userId).get()
                .addOnSuccessListener { documentSnapshot ->
                    val dataNascimento = documentSnapshot.getString("dataNascimento")

                    if (dataNascimento != null) {
                        val idade = calcularIdade(dataNascimento)
                        // Exibe a idade no TextView
                        idadeTextView.text = "$idade anos"
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao recuperar a idade: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
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

    private fun calcularIdade(dataNascimento: String): Int {
        // Define o formato da data como dd/MM/yyyy
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        try {
            // Converte a string para um objeto Date
            val dataNasc = formato.parse(dataNascimento)

            // Se a data for válida, calcula a idade
            if (dataNasc != null) {
                val dataAtual = Calendar.getInstance()
                val idade = dataAtual.get(Calendar.YEAR) - (dataNasc.year + 1900)

                // Ajusta se a data de nascimento ainda não passou neste ano
                val mesNascimento = dataNasc.month + 1 // Janeiro é 0, então soma 1
                val diaNascimento = dataNasc.date

                if (dataAtual.get(Calendar.MONTH) + 1 < mesNascimento ||
                    (dataAtual.get(Calendar.MONTH) + 1 == mesNascimento && dataAtual.get(Calendar.DAY_OF_MONTH) < diaNascimento)) {
                    return idade - 1
                }

                return idade
            }
        } catch (e: Exception) {
            // Caso a data seja inválida ou outro erro, retorna 0
            e.printStackTrace()
        }
        return 0
    }

    // Função para abrir a galeria
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Função chamada após a seleção da foto
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            val imageUri = data?.data
            imageView2.setImageURI(imageUri) // Exibe a imagem no ImageView
            salvarFotoNoFirebase(imageUri)  // Salva a imagem no Firebase Storage
        }
    }

    // Função para salvar a foto no Firebase Storage
    private fun salvarFotoNoFirebase(imageUri: Uri?) {
        val userId = auth.currentUser?.uid
        if (userId != null && imageUri != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("usuarios/$userId/fotoPerfil.jpg") // Define o caminho no Storage

            imageRef.putFile(imageUri)
                .addOnSuccessListener {
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Salvar a URL da imagem no Firestore
                        salvarUrlFotoNoFirestore(uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao salvar a imagem: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Função para salvar a URL da foto no Firestore
    private fun salvarUrlFotoNoFirestore(fotoUrl: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = firestore.collection("usuarios").document(userId)
            userRef.update("fotoPerfil", fotoUrl)
                .addOnSuccessListener {
                    Toast.makeText(this, "Foto atualizada com sucesso", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao atualizar a foto: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    // Função para carregar a foto do usuário do Firestore (caso já tenha sido salva)
    private fun carregarFotoUsuario() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userRef = firestore.collection("usuarios").document(userId)
            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    val fotoUrl = documentSnapshot.getString("fotoPerfil")
                    if (fotoUrl != null) {
                        Glide.with(this)
                            .load(fotoUrl)  // Usa Glide para carregar a imagem da URL
                            .into(imageView2)
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao carregar a foto: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

}
