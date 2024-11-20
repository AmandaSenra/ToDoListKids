package com.example.todolistkids.ui

interface EventoUI {
    data class Notificacao(val mensagem: String): EventoUI
    data object NavegBack: EventoUI
    data class Navegacao<T : Any>(val rota: T): EventoUI

}