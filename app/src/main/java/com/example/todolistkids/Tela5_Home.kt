package com.example.todolistkids

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Tela5_Home : AppCompatActivity() {

    private lateinit var dataText: TextView
    private lateinit var calendario: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tela5_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tela5)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dataText = findViewById(R.id.data)
        calendario = findViewById(R.id.calendario)

        // Exibir a data atual do sistema ao iniciar
        exibirDataAtual()

        // Defina a ação ao clicar no ImageView (ícone do calendário)
        calendario.setOnClickListener {
            abrirCalendario()
        }

        // Inicializa  icones de NovaTarefa e Menu
        val novaTarefa: Button = findViewById(R.id.nova_tarefa)
        val menu : ImageView = findViewById(R.id.hamburguer)

        // Configura o clique nos icones
        novaTarefa.setOnClickListener {
            val intent = Intent(this, Tela7_NovaTarefa::class.java)
            startActivity(intent)
        }
        menu.setOnClickListener{
            val intent = Intent(this, Tela6_Menu::class.java)
            startActivity(intent)
        }
    }

    // Função para atualizar o TextView com a data desejada
    private fun atualizarDataExibida(data: Date) {
        val dateFormat = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("pt", "BR"))
        val formattedDate = dateFormat.format(data)
        dataText.text = formattedDate
    }

    // Função para exibir a data atual do sistema
    private fun exibirDataAtual() {
        val dataAtual = Date()
        atualizarDataExibida(dataAtual)
    }

    // Função para abrir o DatePickerDialog
    private fun abrirCalendario() {
        val calendario = Calendar.getInstance()
        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)
        // Cria um DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            { _, anoSelecionado, mesSelecionado, diaSelecionado ->
                // Cria uma instância de Calendar com a data selecionada
                val dataSelecionada = Calendar.getInstance().apply {
                    set(Calendar.YEAR, anoSelecionado)
                    set(Calendar.MONTH, mesSelecionado)
                    set(Calendar.DAY_OF_MONTH, diaSelecionado)
                }.time

                // Atualiza o TextView com a data selecionada
                atualizarDataExibida(dataSelecionada)

                // Exibir as tarefas para a data selecionada (caso precise)
                // exibirTarefasParaData(diaSelecionado, mesSelecionado, anoSelecionado)
            },
            ano,
            mes,
            dia
        )

        datePickerDialog.show()
    }

}


