package com.example.wappo_game.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wappo_game.presentation.GameViewModel
import com.example.wappo_game.presentation.MenuScreen

class MainActivity : ComponentActivity() {

    // activity-scoped ViewModel — будем передавать в GameScreen
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    AppNavHost(gameViewModel = gameViewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavHost(gameViewModel: GameViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "menu"
    ) {
        composable("menu") {
            MenuScreen(
                onPlayClick = { navController.navigate("game") },
                onEditorClick = { navController.navigate("editor") },
                onExitClick = { /* optionally finish activity via callback */ }
            )
        }

        composable("game") {
            // передаём activity-scoped viewModel в экран
            GameScreen(vm = gameViewModel, onBackToMenu = { navController.popBackStack() })
        }

        composable("editor") {
            EditorScreen(
                onBack = { navController.popBackStack() },
                onSave = { newState ->
                    gameViewModel.loadCustomMap(newState)
                },
                rows = 6,
                cols = 6
            )
        }
    }
}