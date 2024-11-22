package com.example.todolistkids.ui.telas.exibir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistkids.dados.TarefaRepositorio
import com.example.todolistkids.navegacao.RotaAdicEditTarefa
import com.example.todolistkids.ui.EventoUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ModeloVizualizacaoExibir(
    private val repositorio: TarefaRepositorio,
): ViewModel() {

    val tarefas = repositorio.todasTarefas()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _EventoUI = Channel<EventoUI>()
    val eventoUI = _EventoUI.receiveAsFlow()


    fun onEvento(evento: EventoExibir) {
        when (evento) {
            is EventoExibir.Deletar -> {
                deletarTarefa(evento.id)
            }
            is EventoExibir.completar -> {
                completarTarefa(evento.id, evento.completado)
            }
            is EventoExibir.AddEditar -> {
                viewModelScope.launch {
                    _EventoUI.send(EventoUI.Navegacao(RotaAdicEditTarefa(evento.id)))
                }
            }
        }
    }

    private fun deletarTarefa(id: Long) {
        viewModelScope.launch {
            repositorio.deletarTarefa(id)
        }
    }

    private fun completarTarefa(id: Long, completado: Boolean) {
        viewModelScope.launch {
            repositorio.completarTarefa(id, completado)
        }
    }

    fun contarTarefasCompletadas(): Flow<Int> {
        return repositorio.todasTarefas().map { tarefas ->
            tarefas.count { it.completado }
        }
    }

}