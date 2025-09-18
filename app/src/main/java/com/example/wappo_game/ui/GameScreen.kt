package com.example.wappo_game.ui

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import com.example.wappo_game.domain.*
import com.example.wappo_game.presentation.GameViewModel
import kotlin.math.abs

@Composable
fun GameScreen(vm: GameViewModel) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Turn: ${state.playerMoves}  Result: ${state.result::class.simpleName}",
            modifier = Modifier
                .padding(vertical = 16.dp),
            fontSize = 16.sp
        )

        var totalDx by remember { mutableFloatStateOf(0f) }
        var totalDy by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(8.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            totalDx = 0f
                            totalDy = 0f
                        },
                        onDrag = { change, dragAmount ->
                            totalDx += dragAmount.x
                            totalDy += dragAmount.y
                            change.consume()
                        },
                        onDragEnd = {
                            if (abs(totalDx) > abs(totalDy)) {
                                if (totalDx > 0) vm.moveRight() else vm.moveLeft()
                            } else {
                                if (totalDy > 0) vm.moveDown() else vm.moveUp()
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            BoardView(state = state, modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { vm.resetGame() }) { Text("Reset") }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun BoardView(state: GameState, modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val boardSize = min(maxWidth, maxHeight)
        val cellSizeDp = boardSize / state.cols
        val cellSizePx = with(LocalDensity.current) { cellSizeDp.toPx() }

        val playerX by animateDpAsState(targetValue = state.playerPos.c * cellSizeDp)
        val playerY by animateDpAsState(targetValue = state.playerPos.r * cellSizeDp)

        val enemyX by animateDpAsState(targetValue = state.enemyPos.c * cellSizeDp)
        val enemyY by animateDpAsState(targetValue = state.enemyPos.r * cellSizeDp)

        Box(modifier = Modifier.size(boardSize)) {
            // Grid: rows x cols
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in 0 until state.rows) {
                    Row(modifier = Modifier.height(cellSizeDp)) {
                        for (c in 0 until state.cols) {
                            val pos = Pos(r, c)
                            val tile = state.tileAt(pos)!!

                            val bg = when (tile.type) {
                                TileType.TRAP -> Color(0xFF9C27B0) // trap
                                TileType.EXIT -> Color(0xFFFFEB3B) // exit
                                else -> Color(0xFFEEEEEE)
                            }

                            Box(
                                modifier = Modifier
                                    .size(cellSizeDp)
                                    .padding(1.dp)
                                    .background(bg, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                when (tile.type) {
                                    TileType.TRAP -> Text("T", fontSize = (cellSizeDp / 3).value.sp)
                                    TileType.EXIT -> Text("EXIT", fontSize = (cellSizeDp / 6).value.sp)
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            // Walls
            Canvas(modifier = Modifier.matchParentSize()) {
                val stroke = (cellSizePx * 0.12f).coerceAtLeast(6f)
                for ((a, b) in state.walls) {
                    if (a.r == b.r && abs(a.c - b.c) == 1) {
                        val rowTop = a.r * cellSizePx
                        val rowBottom = rowTop + cellSizePx
                        val dividerX = maxOf(a.c, b.c) * cellSizePx

                        drawLine(
                            Color.Black,
                            start = Offset(dividerX, rowTop),
                            end = Offset(dividerX, rowBottom),
                            strokeWidth = stroke
                        )
                    } else if (a.c == b.c && abs(a.r - b.r) == 1) {
                        val colLeft = a.c * cellSizePx
                        val colRight = colLeft + cellSizePx
                        val dividerY = maxOf(a.r, b.r) * cellSizePx

                        drawLine(
                            Color.Black,
                            start = Offset(colLeft, dividerY),
                            end = Offset(colRight, dividerY),
                            strokeWidth = stroke
                        )
                    }
                }
            }

            // Player
            Box(
                modifier = Modifier
                    .offset(playerX, playerY)
                    .size(cellSizeDp)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("P", fontSize = (cellSizeDp / 3).value.sp, textAlign = TextAlign.Center)
            }

            // Enemy
            Box(
                modifier = Modifier
                    .offset(enemyX, enemyY)
                    .size(cellSizeDp)
                    .background(Color(0xFFF44336), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("E", fontSize = (cellSizeDp / 3).value.sp, textAlign = TextAlign.Center)
            }
        }
    }
}