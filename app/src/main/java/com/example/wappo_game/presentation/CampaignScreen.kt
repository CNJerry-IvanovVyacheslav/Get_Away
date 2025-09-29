package com.example.wappo_game.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wappo_game.data.LevelRepository
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.ui.BoardPreview

@Composable
fun CampaignScreen(
    vm: GameViewModel,
    onLevelSelected: (GameState) -> Unit,
    onBack: () -> Unit
) {
    val unlockedLevels = vm.unlockedLevels.collectAsState()
    val levels = LevelRepository.levels

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Campaign Levels", fontSize = 28.sp)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.weight(1f)
        ) {
            itemsIndexed(levels) { index, level ->
                val unlocked = index < unlockedLevels.value
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = unlocked) { if (unlocked) onLevelSelected(level) }
                        .padding(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        BoardPreview(level, sizeDp = 150.dp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = if (unlocked) level.name else "🔒 Locked",
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Back")
        }
    }
}
