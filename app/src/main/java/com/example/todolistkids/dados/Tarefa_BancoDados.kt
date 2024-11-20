package com.example.todolistkids.dados

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [TarefaEntidade::class],
    version = 1,
)
abstract class TarefaBancoDados : RoomDatabase() {

    abstract val TarefaDao: TarefaDao
}

object TarefaProvedorBancoDados {

    @Volatile
    private var INSTANCE: TarefaBancoDados? = null

    fun provide(context: Context): TarefaBancoDados {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                TarefaBancoDados::class.java,
                "aplicativo-todolistkids"
            ).build()
            INSTANCE = instance
            instance
        }
    }
}

