package com.example.todolistkids.ui.telas.addedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistkids.dados.TarefaRepositorio
import com.example.todolistkids.ui.EventoUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ModeloVizualizacaoAddEdit(
    private val id: Long? = null,
    private val repositorio: TarefaRepositorio,
) : ViewModel() {

    var titulo by mutableStateOf("")
        private set

    var descricao by mutableStateOf<String?>(null)
        private set

    private val _EventoUI = Channel<EventoUI>()
    val eventoUI = _EventoUI.receiveAsFlow()

    init {
        id?.let {
            viewModelScope.launch {
                val tarefa = repositorio.getBy(it)
                    titulo = tarefa?.titulo ?: ""
                    descricao = tarefa?.descricao
            }
        }
    }

    fun alteraEvento(evento: EventoAddEdit) {
        when (evento) {
            is EventoAddEdit.AlterarTitulo -> {
                titulo = evento.titulo
            }
            is EventoAddEdit.AlterarDescricao -> {
                descricao = evento.descricao
            }
            EventoAddEdit.Salvar -> {
                salvar()
            }
        }
    }

    private fun salvar() {
        viewModelScope.launch {
            if (titulo.isBlank()) {
                _EventoUI.send(EventoUI.Notificacao("O título não pode estar em branco"))
                return@launch
            }
            repositorio.inserirTarefa(titulo, descricao, id)
            _EventoUI.send(EventoUI.NavegBack)

        }
    }

}