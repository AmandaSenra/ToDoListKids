package com.example.todolistkids.ui.telas.addedit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todolistkids.dados.TarefaProvedorBancoDados
import com.example.todolistkids.dados.Tarefa_ImplementReposit
import com.example.todolistkids.ui.EventoUI
import com.example.todolistkids.ui.theme.ToDoListKidsTheme

@Composable
fun TelaAdicEditTarefas(
    id: Long?,
    navigateUp: () -> Unit,
) {
    val conteudo = LocalContext.current.applicationContext
    val bancodados = TarefaProvedorBancoDados.provide(conteudo)
    val repositorio = Tarefa_ImplementReposit(
        dao = bancodados.TarefaDao
    )
    val viewModel = viewModel<ModeloVizualizacaoAddEdit>{
        ModeloVizualizacaoAddEdit(
            id = id,
            repositorio = repositorio
        )
    }

    val titulo = viewModel.titulo
    val descricao = viewModel.descricao

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(Unit) {
        viewModel.eventoUI.collect { evento ->
            when (evento) {
                is EventoUI.Notificacao -> {
                    snackbarHostState.showSnackbar(
                        message = evento.mensagem,
                    )
                }
                EventoUI.NavegBack -> {
                    navigateUp()
                }
                is EventoUI.Navegacao<*> -> {
                }
            }
        }

    }

    ConteudoAdicEditTarefas(
        titulo = titulo,
        descricao = descricao,
        snackbarHostState = snackbarHostState,
        alteraEvento = viewModel::alteraEvento
    )
    
}

@Composable
fun ConteudoAdicEditTarefas(
    titulo: String,
    descricao: String?,
    snackbarHostState: SnackbarHostState,
    alteraEvento: (EventoAddEdit) -> Unit,
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    alteraEvento(EventoAddEdit.Salvar)
                },
                modifier = Modifier
                    .fillMaxWidth(0.36f) // Ajusta a largura do botão
                    .height(40.dp), // Define a altura do botão
                containerColor = Color(0xFF77C277)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp), // Ajusta o espaçamento interno
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Salvar"
                    )
                    Spacer(modifier = Modifier.width(7.dp))
                    Text(text = "Salvar")
                }
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) {paddingValues ->
        Column(
            modifier = Modifier
                .consumeWindowInsets(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFFAFAD2)) // Define a cor de fundo
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "✍️ Nova Tarefa   ",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontFamily = FontFamily.Serif
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 1.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            // Linha verde de 3dp
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Color(0xFF77C277)) // Cor verde
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = titulo,
                onValueChange = {
                    alteraEvento(
                        EventoAddEdit.AlterarTitulo(it)
                    )
                },
                placeholder = {
                    Text(text = "Título da tarefa")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = descricao ?: "",
                onValueChange = {
                    alteraEvento(
                        EventoAddEdit.AlterarDescricao(it)
                    )
                },
                placeholder = {
                    Text(text = "Descrição (Opcional)")
                }
            )
        }
    }
}


@Preview
@Composable
private fun MostrarConteudoAdicEditTarefas() {
    ToDoListKidsTheme {
        ConteudoAdicEditTarefas(
            titulo = "",
            descricao = null,
            snackbarHostState = SnackbarHostState(),
            alteraEvento = {},
        )

    }
    
}