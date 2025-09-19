package com.example.wappo_game.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.ui.BoardPreview

@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onEditorClick: () -> Unit,
    onExitClick: () -> Unit,
    previewState: GameState? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(56.dp))
        Text("Wappo-like Game", modifier = Modifier.padding(8.dp))
        Spacer(modifier = Modifier.height(12.dp))

        previewState?.let {
            // show small preview
            Box(modifier = Modifier.padding(8.dp)) {
                BoardPreview(it, sizeDp = 200.dp)
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onPlayClick) { Text("Play") }
            Button(onClick = onEditorClick) { Text("Editor") }
            Button(onClick = onExitClick) { Text("Exit") }
        }
    }
}