package com.example.wappo_game.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.wappo_game.presentation.GameViewModel

class MainActivity : ComponentActivity() {

    // ViewModel через делегат activity-ktx
    private val gameViewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface {
                    // Передаем VM в наш экран
                    GameScreen(vm = gameViewModel)
                }
            }
        }
    }
}