package com.example.todolistkids.dados

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabela_tarefas")
data class TarefaEntidade (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val descricao: String?,
    val completado: Boolean
)
