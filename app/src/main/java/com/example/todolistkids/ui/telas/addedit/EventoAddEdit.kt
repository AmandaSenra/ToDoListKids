package com.example.todolistkids.ui.telas.addedit

sealed interface EventoAddEdit {
    data class AlterarTitulo(val titulo: String): EventoAddEdit
    data class AlterarDescricao(val descricao: String): EventoAddEdit
    data object Salvar: EventoAddEdit

}