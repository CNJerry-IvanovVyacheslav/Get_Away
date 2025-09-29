package com.example.wappo_game.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.wappo_game.data.LevelRepository
import com.example.wappo_game.domain.*
import com.example.wappo_game.presentation.GameViewModel
import kotlin.math.abs

@Composable
fun GameScreen(vm: GameViewModel, onBackToMenu: () -> Unit) {
    val state by vm.state.collectAsState()
    val unlockedLevels by vm.unlockedLevels.collectAsState()
    val currentIndex = vm.currentLevelIndex
    val config = LocalConfiguration.current
    val isLandscape = config.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    if (state.result is GameResult.PlayerWon || state.result is GameResult.PlayerLost) {
        val message = if (state.result is GameResult.PlayerWon) "🎉 Victory!" else "💀 Defeat!"

        AlertDialog(
            onDismissRequest = { },
            title = { Text(message, fontSize = 22.sp, fontWeight = FontWeight.Bold) },
            text = { Text("Total moves: ${state.playerMoves}") },
            confirmButton = {
                Button(onClick = { vm.resetGame() }) { Text("Restart") }
            },
            dismissButton = {
                Row {
                    Button(onClick = onBackToMenu) { Text("Menu") }
                    if (state.result is GameResult.PlayerWon && currentIndex != null) {
                        val nextIndex = currentIndex + 1
                        if (nextIndex < LevelRepository.levels.size) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                val nextLevel = LevelRepository.levels[nextIndex]
                                vm.loadCustomMap(nextLevel)
                            }) { Text("Next Level") }
                        }
                    }
                }
            }
        )
    }

    if (isLandscape) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    Button(onClick = onBackToMenu, modifier = Modifier.align(Alignment.CenterStart)) { Text("Menu") }
                    Text("Moves: ${state.playerMoves}", modifier = Modifier.align(Alignment.Center), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Button(onClick = { vm.resetGame() }, modifier = Modifier.align(Alignment.CenterEnd)) { Text("Reset") }
                }
                SwipeBoard(state, vm, modifier = Modifier.weight(1f))
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(64.dp))
                Text("Moves: ${state.playerMoves}", modifier = Modifier.padding(vertical = 16.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                SwipeBoard(state, vm, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), horizontalArrangement = Arrangement.Center) {
                    Button(onClick = { vm.resetGame() }) { Text("Reset") }
                }
            }
            Button(onClick = onBackToMenu, modifier = Modifier.align(Alignment.TopStart).padding(8.dp)) { Text("Menu") }
        }
    }
}

@Composable
internal fun SwipeBoard(state: GameState, vm: GameViewModel, modifier: Modifier = Modifier) {
    var totalDx by remember { mutableFloatStateOf(0f) }
    var totalDy by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .fillMaxWidth()
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
        BoardView(state)
    }
}

@Composable
fun BoardView(state: GameState, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val boardSize = min(maxWidth, maxHeight)
        val cellSizeDp: Dp = boardSize / state.cols
        val density = LocalDensity.current
        val cellSizePx = with(density) { cellSizeDp.toPx() }

        val playerX by animateDpAsState(targetValue = cellSizeDp * state.playerPos.c)
        val playerY by animateDpAsState(targetValue = cellSizeDp * state.playerPos.r)

        val enemies = if (state.enemyPositions.isEmpty()) listOf(Pos(state.rows - 1, 0)) else state.enemyPositions

        val enemyOffsets = enemies.map { pos ->
            val x by animateDpAsState(targetValue = cellSizeDp * pos.c)
            val y by animateDpAsState(targetValue = cellSizeDp * pos.r)
            x to y
        }

        Box(modifier = Modifier.size(boardSize)) {
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
                                    TileType.EXIT -> Text("EXIT", fontSize = (cellSizeDp / 6).value.sp)
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            Canvas(modifier = Modifier.matchParentSize()) {
                val stroke = (cellSizePx * 0.1f).coerceAtLeast(6f)
                for ((a, b) in state.walls) {
                    if (a.r == b.r && abs(a.c - b.c) == 1) {
                        val rowTop = a.r * cellSizePx
                        val rowBottom = rowTop + cellSizePx
                        val dividerX = maxOf(a.c, b.c) * cellSizePx
                        drawLine(Color.Black, start = Offset(dividerX, rowTop), end = Offset(dividerX, rowBottom), strokeWidth = stroke)
                    } else if (a.c == b.c && abs(a.r - b.r) == 1) {
                        val colLeft = a.c * cellSizePx
                        val colRight = colLeft + cellSizePx
                        val dividerY = maxOf(a.r, b.r) * cellSizePx
                        drawLine(Color.Black, start = Offset(colLeft, dividerY), end = Offset(colRight, dividerY), strokeWidth = stroke)
                    }
                }
            }

            val paddingFactor = 0.07f
            val playerSize = cellSizeDp * (1f - paddingFactor)
            val enemySize = cellSizeDp * (1f - paddingFactor)

            Box(
                modifier = Modifier
                    .offset(x = playerX + (cellSizeDp - playerSize) / 2, y = playerY + (cellSizeDp - playerSize) / 2)
                    .size(playerSize)
                    .background(Color(0xFF4CAF50), RoundedCornerShape(6.dp)),
                contentAlignment = Alignment.Center
            ) { Text("P", fontSize = (cellSizeDp / 3).value.sp, textAlign = TextAlign.Center) }

            enemyOffsets.forEach { (ex, ey) ->
                Box(
                    modifier = Modifier
                        .offset(x = ex + (cellSizeDp - enemySize) / 2, y = ey + (cellSizeDp - enemySize) / 2)
                        .size(enemySize)
                        .background(Color(0xFFF44336), RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) { Text("E", fontSize = (cellSizeDp / 3).value.sp, textAlign = TextAlign.Center) }
            }
        }
    }
}
