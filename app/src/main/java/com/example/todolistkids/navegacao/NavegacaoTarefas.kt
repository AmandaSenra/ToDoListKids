package com.example.todolistkids.navegacao

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.todolistkids.ui.telas.addedit.TelaAdicEditTarefas
import com.example.todolistkids.ui.telas.exibir.TelaExibirTarefas
import kotlinx.serialization.Serializable

@Serializable
object RotaTarefa

@Serializable
data class RotaAdicEditTarefa(val id: Long? = null)

@Composable
fun NavegacaoTarefas() {
    val navegacaoControle = rememberNavController()
    NavHost(navController = navegacaoControle, startDestination = RotaTarefa) {
        composable<RotaTarefa> {
            TelaExibirTarefas(
                navegacaoAdicEditTarefa = { id ->
                    navegacaoControle.navigate(RotaAdicEditTarefa(id = id))
                }
            )
        }
        composable<RotaAdicEditTarefa> { backStackEntry ->
            val adicEditRota = backStackEntry.toRoute<RotaAdicEditTarefa>()
            TelaAdicEditTarefas(
                id = adicEditRota.id,
                navigateUp = {
                    navegacaoControle.popBackStack()
                }
            )
        }

    }
}