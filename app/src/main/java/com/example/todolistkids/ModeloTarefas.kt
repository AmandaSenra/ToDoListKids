package com.example.todolistkids


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class ModeloTarefas: ViewModel(){

    var taskItems = MutableLiveData<MutableList<TaskItem>>()

    init{
        taskItems.value = mutableListOf()
    }

    fun addTaskItem(newTask: TaskItem){
        val list = taskItems.value
        list!!.add(newTask)
        taskItems.postValue(list)
    }

    fun updateTaskItem(id: UUID, nome: String, descricao: String, duracao: LocalTime?){
        val list = taskItems.value
        val task = list!!.find { it.id == id } !!
        task.nome = nome
        task.descricao = descricao
        task.duracao = duracao
        taskItems.postValue(list)
    }

    fun completado(taskItem: TaskItem){
        val list = taskItems.value
        val task = list!!.find { it.id == taskItem.id } !!
        if (task.completDate == null)
            task.completDate = LocalDate.now()
        taskItems.postValue(list)
    }
}