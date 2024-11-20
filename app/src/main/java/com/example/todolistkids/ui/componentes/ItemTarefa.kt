package com.example.todolistkids.ui.componentes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todolistkids.dominio.Tarefa
import com.example.todolistkids.dominio.tarefa1
import com.example.todolistkids.dominio.tarefa2
import com.example.todolistkids.ui.theme.ToDoListKidsTheme

@Composable
fun ItemTarefa(
    tarefa: Tarefa,
    itemConcluido: (Boolean) -> Unit,
    itemClicado: () -> Unit,
    itemDeletado: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = itemClicado,
        modifier = Modifier,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp,
        color = Color(0xFFFAFAD2),
        border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline),
    ){
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarefa.completado,
                onCheckedChange = itemConcluido,
                modifier = Modifier.scale(scaleX = 1.2f, scaleY = 1.2f),
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF77C277), // Define a cor de fundo ao ser marcado
                    uncheckedColor = MaterialTheme.colorScheme.outline // Cor quando desmarcado
                )
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = tarefa.titulo,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                    )
                )

                tarefa.descricao?.let {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = tarefa.descricao,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Serif
                        )
                    )
                }

            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(
                onClick = itemDeletado,
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "deletar"
                )
            }
        }
    }
    
}

@Preview
@Composable
private fun MostraItemLista() {
    ToDoListKidsTheme {
        ItemTarefa(
            tarefa = tarefa1,
            itemConcluido = {},
            itemClicado = {},
            itemDeletado = {}
        )
    }
}

@Preview
@Composable
private fun MostraItemListaCompletado() {
    ToDoListKidsTheme {
        ItemTarefa(
            tarefa = tarefa2,
            itemConcluido = {},
            itemClicado = {},
            itemDeletado = {}
        )
    }
}