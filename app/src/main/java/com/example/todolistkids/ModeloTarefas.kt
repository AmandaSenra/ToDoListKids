package com.example.todolistkids


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class ModeloTarefas(private val reposity: TaskItemReposity): ViewModel(){

    var taskItems: LiveData<List<TaskItem>> = reposity.allTaskItems.asLiveData()

    fun addTaskItem(newTask: TaskItem) = viewModelScope.launch {
        reposity.insertTaskItem(newTask)
    }

    fun updateTaskItem(taskItem: TaskItem) = viewModelScope.launch {
        reposity.updatetTaskItem(taskItem)
    }

    fun deleteTaskItem(deltaskItem: TaskItem) = viewModelScope.launch {
        reposity.deleteTaskItem(deltaskItem)
    }

    fun setCompleted(taskItem: TaskItem) = viewModelScope.launch {
        if(!taskItem.isCompleted())
            taskItem.completedDataString = TaskItem.dateFormatter.format(LocalDate.now())
        reposity.updatetTaskItem(taskItem)
    }
}

class TaskItemModelFactory(private val reposity: TaskItemReposity): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModeloTarefas::class.java))
            return ModeloTarefas(reposity) as T

        throw IllegalArgumentException("Unknow class for View Model")
    }
}
