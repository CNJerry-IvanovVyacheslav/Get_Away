package com.example.wappo_game.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wappo_game.R
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.domain.Pos
import com.example.wappo_game.domain.TileType
import kotlin.math.abs

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BoardPreview(state: GameState, sizeDp: Dp) {
    BoxWithConstraints(
        modifier = Modifier
            .size(sizeDp)
            .testTag("BoardPreview")
    ) {
        val cellSize = sizeDp / state.cols
        val paddingFactor = 0.1f
        val playerSize = cellSize * (1f - paddingFactor)
        val enemySize = cellSize * (1.1f - paddingFactor)
        val bottomOffset = cellSize * 0.1f

        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in 0 until state.rows) {
                    Row(modifier = Modifier.height(cellSize)) {
                        for (c in 0 until state.cols) {
                            val pos = Pos(r, c)
                            val tile = state.tileAt(pos)!!
                            val isPlayer = state.playerPos == pos
                            val isEnemy = state.enemyPositions.contains(pos)

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .padding(1.dp)
                                    .background(Color(0xFFB3E5FC), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isPlayer -> {
                                        Image(
                                            painter = painterResource(id = R.drawable.player_right),
                                            contentDescription = "Player",
                                            modifier = Modifier.size(playerSize)
                                        )
                                    }

                                    isEnemy -> {
                                        Image(
                                            painter = painterResource(id = R.drawable.enemy_right),
                                            contentDescription = "Enemy",
                                            modifier = Modifier
                                                .offset(y = bottomOffset * -1)
                                                .size(enemySize)
                                        )
                                    }

                                    tile.type == TileType.TRAP -> {
                                        Image(
                                            painter = painterResource(id = R.drawable.trap),
                                            contentDescription = "Trap",
                                            modifier = Modifier.size(cellSize * 0.9f)
                                        )
                                    }

                                    tile.type == TileType.EXIT -> {
                                        Image(
                                            painter = painterResource(id = R.drawable.exit3),
                                            contentDescription = "Exit",
                                            modifier = Modifier.size(cellSize * 0.9f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Canvas(modifier = Modifier.matchParentSize()) {
                val stroke = (cellSize.toPx() * 0.1f).coerceAtLeast(2f)
                for ((a, b) in state.walls) {
                    if (a.r == b.r && abs(a.c - b.c) == 1) {
                        val rowTop = a.r * cellSize.toPx()
                        val rowBottom = rowTop + cellSize.toPx()
                        val dividerX = maxOf(a.c, b.c) * cellSize.toPx()
                        drawLine(
                            Color.Black,
                            start = Offset(dividerX, rowTop),
                            end = Offset(dividerX, rowBottom),
                            strokeWidth = stroke
                        )
                    } else if (a.c == b.c && abs(a.r - b.r) == 1) {
                        val colLeft = a.c * cellSize.toPx()
                        val colRight = colLeft + cellSize.toPx()
                        val dividerY = maxOf(a.r, b.r) * cellSize.toPx()
                        drawLine(
                            Color.Black,
                            start = Offset(colLeft, dividerY),
                            end = Offset(colRight, dividerY),
                            strokeWidth = stroke
                        )
                    }
                }
            }
        }
    }
}