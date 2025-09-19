package com.example.wappo_game.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
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

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavHost(gameViewModel: GameViewModel) {
    val navController = rememberNavController()
    val activity = (LocalActivity.current)

    NavHost(
        navController = navController,
        startDestination = "menu"
    ) {
        composable("menu") {
            MenuScreen(
                previewState = gameViewModel.state.value,
                onPlayClick = { navController.navigate("game") },
                onEditorClick = { navController.navigate("editor") },
                onExitClick = { activity?.finish() }
            )
        }

        composable("game") {
            GameScreen(vm = gameViewModel, onBackToMenu = { navController.popBackStack() })
        }

        composable("editor") {
            EditorScreen(
                onBack = { navController.popBackStack() },
                onSave = { newState ->
                    gameViewModel.loadCustomMap(newState)
                    navController.navigate("game")
                },
                rows = 6,
                cols = 6,
                initialState = gameViewModel.state.value
            )
        }
    }
}