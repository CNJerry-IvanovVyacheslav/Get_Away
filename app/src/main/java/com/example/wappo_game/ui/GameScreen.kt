package com.example.wappo_game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.domain.Pos
import com.example.wappo_game.domain.TileType
import com.example.wappo_game.presentation.GameViewModel
import kotlin.math.abs

@Composable
fun GameScreen(vm: GameViewModel) {
    val state by vm.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Turn: ${state.turn}  Result: ${state.result::class.simpleName}",
            modifier = Modifier.padding(8.dp)
        )

        var totalDx by remember { mutableFloatStateOf(0f) }
        var totalDy by remember { mutableFloatStateOf(0f) }

        Box(
            modifier = Modifier
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
                }
        ) {
            BoardView(state = state)
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { vm.resetGame() }) { Text("Reset") }
    }
}

@Composable
fun BoardView(state: GameState) {
    Box {
        Column {
            for (r in 0 until state.rows) {
                Row {
                    for (c in 0 until state.cols) {
                        val pos = Pos(r, c)
                        val tile = state.tileAt(pos)!!
                        val isPlayer = state.playerPos == pos
                        val isEnemy = state.enemyPos == pos
                        val bg = when {
                            isPlayer -> Color.Green
                            isEnemy -> Color.Red
                            tile.type == TileType.TRAP -> Color.Magenta
                            tile.type == TileType.EXIT -> Color.Yellow
                            else -> Color(0xFFEEEEEE)
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .padding(1.dp)
                                .background(bg, RoundedCornerShape(6.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                isPlayer -> Text("P", fontSize = 18.sp, textAlign = TextAlign.Center)
                                isEnemy -> Text("E", fontSize = 18.sp, textAlign = TextAlign.Center)
                                tile.type == TileType.TRAP -> Text("T", fontSize = 12.sp)
                                tile.type == TileType.EXIT -> Text("EXIT", fontSize = 10.sp)
                                else -> {}
                            }
                        }
                    }
                }
            }
        }

        // Отрисуем стены поверх клеток
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .padding(1.dp)
        ) {
            val cellSize = size.width / state.cols
            for ((a, b) in state.walls) {
                val ax = a.c * cellSize
                val ay = a.r * cellSize
                val bx = b.c * cellSize
                val by = b.r * cellSize

                if (a.r == b.r) {
                    // горизонтальная стена
                    val y = (ay + by) / 2
                    drawLine(
                        Color.Black,
                        start = androidx.compose.ui.geometry.Offset(ax, y),
                        end = androidx.compose.ui.geometry.Offset(bx + cellSize, y),
                        strokeWidth = 6f
                    )
                } else if (a.c == b.c) {
                    // вертикальная стена
                    val x = (ax + bx) / 2
                    drawLine(
                        Color.Black,
                        start = androidx.compose.ui.geometry.Offset(x, ay),
                        end = androidx.compose.ui.geometry.Offset(x, by + cellSize),
                        strokeWidth = 6f
                    )
                }
            }
        }
    }
}
