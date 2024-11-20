package com.example.todolistkids.dados

import com.example.todolistkids.dominio.Tarefa
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class Tarefa_ImplementReposit(
    private val dao: TarefaDao
) : TarefaRepositorio {

    override suspend fun inserirTarefa(titulo: String, descricao: String?, id: Long?) {
        val entidade = id?.let {
            dao.getBy(it)?.copy(
                titulo = titulo,
                descricao = descricao
            )
        } ?: TarefaEntidade(
            titulo = titulo,
            descricao = descricao,
            completado = false
        )
        dao.inserirTarefa(entidade)
    }

    override suspend fun completarTarefa(id: Long, completado: Boolean) {
        val tarefaExistente = dao.getBy(id) ?: return
        val tarefaAtualizada = tarefaExistente.copy(completado = completado)
        dao.inserirTarefa(tarefaAtualizada)
    }

    override suspend fun deletarTarefa(id: Long) {
        val tarefaExistente = dao.getBy(id) ?: return
        dao.deletarTarefa(tarefaExistente)
    }

    override fun todasTarefas(): Flow<List<Tarefa>> {
        return dao.todasTarefas().map {entidades ->
            entidades.map { entidade ->
                Tarefa(
                    id = entidade.id,
                    titulo = entidade.titulo,
                    descricao = entidade.descricao,
                    completado = entidade.completado)
            }
        }
    }

        override suspend fun getBy(id: Long): Tarefa? {
            return dao.getBy(id)?.let { entidade ->
                Tarefa(
                    id = entidade.id,
                    titulo = entidade.titulo,
                    descricao = entidade.descricao,
                    completado = entidade.completado)
            }
        }
    }
