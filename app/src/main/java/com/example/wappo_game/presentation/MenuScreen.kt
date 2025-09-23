package com.example.wappo_game.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.ui.BoardPreview

@Composable
fun MenuScreen(
    onPlayClick: () -> Unit,
    onCreateMapClick: () -> Unit,
    onMapsClick: () -> Unit,
    onExitClick: () -> Unit,
    previewState: GameState? = null
) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wappo-like Game",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )

            previewState?.let {
                Text(
                    text = it.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                previewState?.let {
                    BoardPreview(it, sizeDp = 220.dp)
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(onClick = onPlayClick) { Text("Play") }
                    Button(onClick = onCreateMapClick) { Text("Create Map") }
                    Button(onClick = onMapsClick) { Text("Saved Maps") }
                    Button(onClick = onExitClick) { Text("Exit") }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            Text(
                text = "Wappo-like Game",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            previewState?.let {
                Text(
                    text = it.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
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
                Button(onClick = onCreateMapClick) { Text("Create Map") }
                Button(onClick = onMapsClick) { Text("Saved Maps") }
                Button(onClick = onExitClick) { Text("Exit") }
            }
        }
    }
}
