package com.example.todolistkids

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.app.Activity
import android.graphics.BitmapFactory
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
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
        configurarIntents()
        carregarFotoPerfil()
        criarConta()
    }

    private fun configurarIntents() {
        findViewById<ImageView>(R.id.logo2).setOnClickListener {
            startActivity(Intent(this, Tela5_Home::class.java))
        }

        findViewById<ImageView>(R.id.hamburguer).setOnClickListener {
            startActivity(Intent(this, Tela6_Menu::class.java))
        }

        findViewById<TextView>(R.id.sair).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Deslogado com sucesso!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Tela3_Login::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        findViewById<TextView>(R.id.minhasconquistas).setOnClickListener {
            startActivity(Intent(this, Tela7_Conquistas::class.java))
        }

        findViewById<TextView>(R.id.meusamigos).setOnClickListener {
            startActivity(Intent(this, Tela8_Amigos::class.java))
        }

        findViewById<TextView>(R.id.resgate).setOnClickListener {
            startActivity(Intent(this, Tela9_Historico::class.java))
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

    // Função para abrir a galeria e selecionar a foto
    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    // Função chamada após a seleção da foto
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            val imageUri = data?.data
            imageUri?.let {
                imageView2.setImageURI(it) // Exibe a imagem selecionada na ImageView
                salvarFotoLocal(it)        // Salva a imagem localmente no dispositivo
            }
        }
    }

    // Função para salvar a foto local com o nome exclusivo para o usuário (usando UID do Firebase)
    private fun salvarFotoLocal(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)

        // Cria um arquivo com o nome exclusivo para o usuário (usando o UID do Firebase)
        val file = File(filesDir, "foto_perfil_${auth.currentUser?.uid}.jpg")
        val outputStream = FileOutputStream(file)

        // Copia os dados da imagem para o arquivo
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        // Salva o nome do arquivo no Firestore, não a URL
        val userId = auth.currentUser?.uid
        userId?.let {
            val usuarioRef = firestore.collection("usuarios").document(userId)
            usuarioRef.update("fotoPerfil", file.name) // Salva apenas o nome do arquivo no Firestore
        }
    }

    // Função para carregar a foto de perfil do usuário (usando o nome salvo no Firestore)
    private fun carregarFotoPerfil() {
        // Obtém o ID do usuário logado
        val userId = auth.currentUser?.uid
        userId?.let {
            // Busca o nome do arquivo da foto no Firestore
            val usuarioRef = firestore.collection("usuarios").document(userId)
            usuarioRef.get().addOnSuccessListener { document ->
                val nomeFoto = document.getString("fotoPerfil") // Recupera o nome do arquivo da foto

                if (!nomeFoto.isNullOrEmpty()) {
                    // Cria um arquivo a partir do nome salvo no Firestore
                    val file = File(filesDir, nomeFoto)

                    // Se o arquivo existe, carrega e exibe a imagem
                    if (file.exists()) {
                        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                        imageView2.setImageBitmap(bitmap) // Exibe a imagem na ImageView
                    } else {
                        // Se o arquivo não for encontrado, exibe a imagem padrão
                        imageView2.setImageResource(R.drawable.padrao)
                    }
                } else {
                    // Se não houver foto, exibe a imagem padrão
                    imageView2.setImageResource(R.drawable.padrao)
                }
            }
        }
    }

    // Função para exibir a imagem padrão ao criar uma nova conta
    private fun criarConta() {
        val userId = auth.currentUser?.uid
        userId?.let {
            val usuarioRef = firestore.collection("usuarios").document(userId)

            // Verifica se o campo "fotoPerfil" existe ou está vazio
            usuarioRef.get().addOnSuccessListener { document ->
                if (document.getString("fotoPerfil") == null) {
                    // Se não houver foto, salva o nome de um arquivo padrão
                    usuarioRef.update("fotoPerfil", "foto_perfil_padrao.jpg")
                }
            }
        }
    }
}
