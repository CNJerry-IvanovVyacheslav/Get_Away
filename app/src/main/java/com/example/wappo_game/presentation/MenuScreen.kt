package com.example.wappo_game.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.ui.BoardPreview

@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onEditorClick: () -> Unit,
    onExitClick: () -> Unit,
    onMapsClick: () -> Unit,
    previewState: GameState? = null
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = "Wappo-like Game",
            fontSize = 36.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        previewState?.let {
            Text(
                text = it.name,
                fontSize = 24.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Box(modifier = Modifier.padding(8.dp)) {
                BoardPreview(it, sizeDp = 200.dp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onPlayClick) { Text("Play") }
            Button(onClick = onEditorClick) { Text("Editor") }
            Button(onClick = onMapsClick) { Text("Saved Maps") }
            Button(onClick = onExitClick) { Text("Exit") }
        }
    }
}