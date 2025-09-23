package com.example.wappo_game.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.domain.Pos
import com.example.wappo_game.domain.TileType
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BoardPreview(state: GameState, sizeDp: Dp) {
    BoxWithConstraints(modifier = Modifier.size(sizeDp)) {
        val cellSize = sizeDp / state.cols

        Box(modifier = Modifier.fillMaxSize()) {
            // Grid tiles
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in 0 until state.rows) {
                    Row(modifier = Modifier.height(cellSize)) {
                        for (c in 0 until state.cols) {
                            val pos = Pos(r, c)
                            val tile = state.tileAt(pos)!!
                            val isPlayer = state.playerPos == pos
                            val isEnemy = state.enemyPos == pos
                            val bg = when {
                                isPlayer -> Color(0xFF4CAF50)
                                isEnemy -> Color(0xFFF44336)
                                tile.type == TileType.TRAP -> Color(0xFF9C27B0)
                                tile.type == TileType.EXIT -> Color(0xFFFFEB3B)
                                else -> Color(0xFFEEEEEE)
                            }

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .padding(1.dp)
                                    .background(bg, RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    isPlayer -> Text(
                                        "P",
                                        fontSize = (cellSize / 3).value.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    isEnemy -> Text(
                                        "E",
                                        fontSize = (cellSize / 3).value.sp,
                                        textAlign = TextAlign.Center
                                    )

                                    tile.type == TileType.TRAP -> Text(
                                        "T",
                                        fontSize = (cellSize / 3).value.sp
                                    )

                                    tile.type == TileType.EXIT -> Text(
                                        "EXIT",
                                        fontSize = (cellSize / 6).value.sp
                                    )

                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            // Walls
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

