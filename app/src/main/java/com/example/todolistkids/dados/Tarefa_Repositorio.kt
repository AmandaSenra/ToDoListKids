package com.example.todolistkids.dados

import com.example.todolistkids.dominio.Tarefa
import kotlinx.coroutines.flow.Flow

interface TarefaRepositorio {

    suspend fun inserirTarefa(titulo: String, descricao: String?, id: Long? = null)

    suspend fun completarTarefa(id: Long, completado: Boolean)

    suspend fun deletarTarefa(id: Long)

    fun todasTarefas(): Flow<List<Tarefa>>

    suspend fun getBy(id: Long): Tarefa?
}