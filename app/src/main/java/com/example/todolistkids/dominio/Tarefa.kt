package com.example.todolistkids.dominio

data class Tarefa(
    val id: Long,
    val titulo: String,
    val descricao: String?,
    val completado: Boolean,

)

//Objetos Fake
val tarefa1 = Tarefa(
    id = 1,
    titulo = "Ir ao Supermercado",
    descricao = "Comprar leite, pão e ovos",
    completado = false,

)

val tarefa2 = Tarefa(
    id = 2,
    titulo = "Estudar para a prova",
    descricao = "Estudar matemática, português e ciências",
    completado = true,
)

val tarefa3 = Tarefa(
    id = 3,
    titulo = "Limpar a casa",
    descricao = "Limpar a casa inteira",
    completado = false,
)
