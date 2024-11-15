package com.example.todolistkids

import android.content.Context
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

class TaskItem (
    var nome: String,
    var descricao: String,
    var duracao: LocalTime?,
    var completDate: LocalDate?,
    var id: UUID = UUID.randomUUID()
)

{
    fun isCompleted() = completDate != null
    fun imageResource(): Int = if(isCompleted()) R.drawable.checked_24 else R.drawable.unchecked_24
    fun imagecolor(context: Context): Int = if(isCompleted()) purple(context) else black(context)

    private fun purple(context: Context) = ContextCompat.getColor(context, R.color.purple_500)
    private fun black(context: Context) = ContextCompat.getColor(context, R.color.black_custom)
}

