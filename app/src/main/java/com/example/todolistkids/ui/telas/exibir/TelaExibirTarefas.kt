package com.example.todolistkids.ui.telas.exibir

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistkids.dados.TarefaProvedorBancoDados
import com.example.todolistkids.dados.Tarefa_ImplementReposit
import com.example.todolistkids.dominio.Tarefa
import com.example.todolistkids.dominio.tarefa1
import com.example.todolistkids.dominio.tarefa2
import com.example.todolistkids.dominio.tarefa3
import com.example.todolistkids.navegacao.RotaAdicEditTarefa
import com.example.todolistkids.ui.EventoUI
import com.example.todolistkids.ui.componentes.ItemTarefa
import com.example.todolistkids.ui.telas.addedit.ModeloVizualizacaoAddEdit
import com.example.todolistkids.ui.theme.ToDoListKidsTheme

@Composable
fun TelaExibirTarefas(
    navegacaoAdicEditTarefa: (id: Long?) -> Unit,
) {
    val conteudo = LocalContext.current.applicationContext
    val bancodados = TarefaProvedorBancoDados.provide(conteudo)
    val repositorio = Tarefa_ImplementReposit(
        dao = bancodados.TarefaDao
    )
    val viewModel = viewModel<ModeloVizualizacaoExibir>{
        ModeloVizualizacaoExibir(repositorio = repositorio)
    }

    val tarefas by viewModel.tarefas.collectAsState()

    LaunchedEffect(Unit){
        viewModel.eventoUI.collect{ evento ->
            when(evento){
                is EventoUI.Navegacao<*> -> {
                    when(evento.rota){
                        is RotaAdicEditTarefa -> {
                            navegacaoAdicEditTarefa(evento.rota.id)
                        }
                    }
                }
                EventoUI.NavegBack -> {
                }
                is EventoUI.Notificacao -> {
                }
            }
        }
    }


   ConteudoTarefa(
       tarefas = tarefas,
       onEvento = viewModel::onEvento
       )
}

@Composable
fun ConteudoTarefa(
    tarefas :List<Tarefa>,
    onEvento: (EventoExibir) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onEvento(EventoExibir.AddEditar(null))
                },
                containerColor = Color(0xFF77C277) // Define a cor do botão
            ) {
                Icon(Icons.Default.Add, contentDescription = "adicionar")
            }
        }
    ) { paddingsValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize() // Garante que o LazyColumn ocupe todo o espaço
                .background(Color(0xFFFAFAD2)) // Aplica a cor de fundo no LazyColumn
                .padding(paddingsValues) // Aplica os paddings calculados
                .padding(16.dp) // Padding adicional para o conteúdo
                .consumeWindowInsets(paddingsValues),
            contentPadding = PaddingValues(16.dp)
        ){
            itemsIndexed(tarefas){ index, lista ->
                ItemTarefa(
                    tarefa = lista,
                    itemConcluido = {
                        onEvento(EventoExibir.completar(lista.id, it))
                    },
                    itemClicado = {
                        onEvento(EventoExibir.AddEditar(lista.id))
                    },
                    itemDeletado = {
                        onEvento(EventoExibir.Deletar(lista.id))
                    }
                )
                if (index < tarefas.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

    }
}


@Preview
@Composable
private fun MostraConteudoTarefa() {
    ToDoListKidsTheme {
        ConteudoTarefa(
            tarefas = listOf(
                tarefa1,
                tarefa2,
                tarefa3,
            ),
            onEvento = {},
        )
    }
}