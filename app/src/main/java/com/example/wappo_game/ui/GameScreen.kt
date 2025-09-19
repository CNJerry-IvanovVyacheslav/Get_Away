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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.example.wappo_game.domain.*
import com.example.wappo_game.presentation.GameViewModel
import kotlin.math.abs

@Composable
fun GameScreen(vm: GameViewModel, onBackToMenu: () -> Unit) {
    val state by vm.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Text(
                "Moves: ${state.playerMoves}  Result: ${state.result::class.simpleName}",
                modifier = Modifier
                    .padding(vertical = 16.dp),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

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

        // Menu button in top-left corner (over the board)
        Button(
            onClick = onBackToMenu,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text("Menu")
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
        val cellSizeDp: Dp = boardSize / state.cols
        val density = LocalDensity.current
        val cellSizePx = with(density) { cellSizeDp.toPx() }

        // animated positions (each change of state.playerPos / enemyPos will animate)
        val playerX by animateDpAsState(targetValue = cellSizeDp * state.playerPos.c)
        val playerY by animateDpAsState(targetValue = cellSizeDp * state.playerPos.r)
        val enemyX by animateDpAsState(targetValue = cellSizeDp * state.enemyPos.c)
        val enemyY by animateDpAsState(targetValue = cellSizeDp * state.enemyPos.r)

        Box(modifier = Modifier.size(boardSize)) {
            // Grid cells
            Column(modifier = Modifier.fillMaxSize()) {
                for (r in 0 until state.rows) {
                    Row(modifier = Modifier.height(cellSizeDp)) {
                        for (c in 0 until state.cols) {
                            val pos = Pos(r, c)
                            val tile = state.tileAt(pos)!!
                            val bg = when (tile.type) {
                                TileType.TRAP -> Color(0xFF9C27B0)
                                TileType.EXIT -> Color(0xFFFFEB3B)
                                else -> Color(0xFFEEEEEE)
                            }

                            Box(
                                modifier = Modifier
                                    .size(cellSizeDp)
                                    .padding(2.dp)
                                    .background(bg, RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                when (tile.type) {
                                    TileType.TRAP -> Text("T", fontSize = (cellSizeDp / 3).value.sp)
                                    TileType.EXIT -> Text(
                                        "EXIT",
                                        fontSize = (cellSizeDp / 6).value.sp
                                    )

                                    else -> { /* empty */
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Walls drawn on Canvas (px)
            Canvas(modifier = Modifier.matchParentSize()) {
                val stroke = (cellSizePx * 0.1f).coerceAtLeast(6f)
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
            val paddingFactor = 0.07f
            val playerSize = cellSizeDp * (1f - paddingFactor)
            val enemySize = cellSizeDp * (1f - paddingFactor)

            // Player (animated)
            Box(
                modifier = Modifier
                    .offset(
                        x = playerX + (cellSizeDp - playerSize) / 2,
                        y = playerY + (cellSizeDp - playerSize) / 2
                    )
                    .size(playerSize)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("P", fontSize = (cellSizeDp / 3).value.sp, textAlign = TextAlign.Center)
            }

            // Enemy (animated)
            Box(
                modifier = Modifier
                    .offset(
                        x = enemyX + (cellSizeDp - enemySize) / 2,
                        y = enemyY + (cellSizeDp - enemySize) / 2
                    )
                    .size(enemySize)
                    .background(Color(0xFFF44336), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("E", fontSize = (cellSizeDp / 3).value.sp, textAlign = TextAlign.Center)
            }
        }
    }
}