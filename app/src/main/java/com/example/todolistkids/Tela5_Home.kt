package com.example.todolistkids

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistkids.databinding.ActivityTela5HomeBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Tela5_Home : AppCompatActivity(), TaskItemClickListener {

    private lateinit var dataText: TextView
    private lateinit var calendario: ImageView
    private lateinit var binding: ActivityTela5HomeBinding
    private lateinit var modeloTarefas: ModeloTarefas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTela5HomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        modeloTarefas = ViewModelProvider(this).get(ModeloTarefas::class.java)
        binding.novaTarefaBotao.setOnClickListener{
            Fragment1_NovaPlanilhaTarefas(null).show(supportFragmentManager, "TagNovaTarefa")
        }

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

        setRecyclerView()


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
    }

    private fun setRecyclerView() {
        val telaTela5_Home = this
        modeloTarefas.taskItems.observe(this){
           binding.todolistRecyclerView.apply {
               layoutManager =  LinearLayoutManager(applicationContext)
               adapter = TaskItemAdapter(it, telaTela5_Home)
           }
        }
    }

    override fun editTaskItem(taskItem: TaskItem) {
        Fragment1_NovaPlanilhaTarefas(taskItem).show(supportFragmentManager, "TagNovaTarefa")
    }

    override fun completeTaskItem(taskItem: TaskItem) {
        modeloTarefas.completado(taskItem)
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




