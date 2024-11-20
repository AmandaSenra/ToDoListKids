package com.example.todolistkids.ui.telas.exibir

sealed interface EventoExibir {
    data class Deletar(val id: Long) : EventoExibir
    data class completar (val id: Long, val completado: Boolean) : EventoExibir
    data class AddEditar(val id: Long?) : EventoExibir
}