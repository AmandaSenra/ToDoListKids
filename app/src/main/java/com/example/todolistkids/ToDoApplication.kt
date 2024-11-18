package com.example.todolistkids

import android.app.Application

class ToDoApplication: Application() {

    private val database by lazy { TaskItemDatabase.getDatabase(this) }
    val reposity by lazy { TaskItemReposity(database.taskItemDao()) }
}