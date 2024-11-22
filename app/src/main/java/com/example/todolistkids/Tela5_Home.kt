package com.example.todolistkids

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.todolistkids.navegacao.NavegacaoTarefas
import com.example.todolistkids.ui.theme.ToDoListKidsTheme
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Tela5_Home : AppCompatActivity() {
    private lateinit var dataText: TextView
    private lateinit var calendario: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela5_home)


        // Configura para a cor de fundo preencher a barra de notificações
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                )
        window.statusBarColor = Color.TRANSPARENT // Deixa a barra de notificações transparente


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

        // Inicializa  icone Menu
        val menu : ImageView = findViewById(R.id.hamburguer)
        menu.setOnClickListener{
            val intent = Intent(this, Tela6_Menu::class.java)
            startActivity(intent)
        }


        // Configurar o ComposeView para exibir o NavHost
        val composeNavHost: androidx.compose.ui.platform.ComposeView = findViewById(R.id.composeNavHost)
        composeNavHost.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )

        composeNavHost.setContent {
            ToDoListKidsTheme {
                NavegacaoTarefas()
            }
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
        // Define o Locale para português (Brasil)
        val locale = Locale("pt", "BR")
        val configuracao = resources.configuration
        configuracao.setLocale(locale)
        resources.updateConfiguration(configuracao, resources.displayMetrics)

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

            },
            ano,
            mes,
            dia
        )
        datePickerDialog.show()
    }

}




