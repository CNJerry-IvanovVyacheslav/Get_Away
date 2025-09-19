package com.example.wappo_game.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onEditorClick: () -> Unit,
    onExitClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onPlayClick) { Text("Play") }
            Button(onClick = onEditorClick) { Text("Editor") }
            Button(onClick = onExitClick) { Text("Exit") }
        }
    }
}