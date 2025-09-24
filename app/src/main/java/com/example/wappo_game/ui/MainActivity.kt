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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wappo_game.presentation.GameViewModel
import com.example.wappo_game.presentation.MapsScreen
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
    val lastMap by gameViewModel.lastMapState.collectAsState()
    val activity = LocalActivity.current

    NavHost(navController = navController, startDestination = "menu") {

        composable("menu") {
            MenuScreen(
                onPlayClick = { navController.navigate("game") },
                onCreateMapClick = { navController.navigate("create_map") },
                onExitClick = { activity?.finish() },
                onMapsClick = { navController.navigate("saved_maps") },
                previewState = lastMap
            )
        }

        composable("game") {
            GameScreen(vm = gameViewModel, onBackToMenu = { navController.popBackStack() })
        }

        composable("create_map") {
            EditorScreen(
                viewModel = gameViewModel,
                initialState = null,
                onGoToMenu = {
                    navController.navigate("menu") {
                        popUpTo("create_map") { inclusive = true }
                    }
                }
            )
        }


        composable("editor_for_edit/{mapName}") { backStackEntry ->
            val mapName = backStackEntry.arguments?.getString("mapName")
            val map = gameViewModel.savedMaps.value.find { it.name == mapName }
            if (map != null) {
                EditorScreen(
                    viewModel = gameViewModel,
                    initialState = map,
                    onGoToMenu = {
                        navController.navigate("menu") {
                            popUpTo("editor_for_edit/$mapName") { inclusive = true }
                        }
                    }
                )
            }
        }


        composable("saved_maps") {
            MapsScreen(
                viewModel = gameViewModel,
                onBack = { navController.popBackStack() },
                onLoadMap = { map ->
                    gameViewModel.loadCustomMap(map)
                    navController.popBackStack()
                },
                onEditMap = { map ->
                    navController.navigate("editor_for_edit/${map.name}")
                }
            )
        }
    }
}