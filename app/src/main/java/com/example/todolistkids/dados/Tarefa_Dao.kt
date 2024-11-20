package com.example.todolistkids.dados

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TarefaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTarefa(entity: TarefaEntidade)

    @Delete
    suspend fun deletarTarefa(entity: TarefaEntidade)

    @Query("SELECT * FROM tabela_tarefas")
    fun todasTarefas(): Flow<List<TarefaEntidade>>

    @Query("SELECT * FROM tabela_tarefas WHERE id = :id")
    suspend fun getBy(id: Long): TarefaEntidade?
}