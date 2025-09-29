package com.example.wappo_game.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
    vm: GameViewModel,
    onPlayClick: () -> Unit,
    onCampaignClick: () -> Unit,
    onCreateMapClick: () -> Unit,
    onMapsClick: () -> Unit,
    onExitClick: () -> Unit,
    previewState: GameState? = null
) {
    val unlockedLevels = vm.unlockedLevels.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Wappo-like Game",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                previewState?.let {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = it.name,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BoardPreview(it, sizeDp = 220.dp)
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = onPlayClick) { Text("Play") }
                Button(onClick = onCampaignClick) { Text("Campaign") }
                Button(onClick = onCreateMapClick) { Text("Create Map") }
                Button(onClick = onMapsClick) { Text("Saved Maps") }
                Button(onClick = onExitClick) { Text("Exit") }
            }
        }
    } else {
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
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                BoardPreview(it, sizeDp = 200.dp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(onClick = onPlayClick) { Text("Play") }
                Button(onClick = onCampaignClick) { Text("Campaign") }
                Button(onClick = onCreateMapClick) { Text("Create Map") }
                Button(onClick = onMapsClick) { Text("Saved Maps") }
                Button(onClick = onExitClick) { Text("Exit") }
            }
        }
    }
}
