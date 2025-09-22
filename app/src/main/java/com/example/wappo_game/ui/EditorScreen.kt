package com.example.wappo_game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wappo_game.domain.*
import kotlin.math.abs

enum class EditorMode { TILES, WALLS, PLAYER_START, ENEMY_START }

@Composable
fun EditorScreen(
    onBack: () -> Unit,
    onSave: (GameState) -> Unit,
    rows: Int = 6,
    cols: Int = 6,
    initialState: GameState? = null
) {
    var tilesGrid by remember {
        mutableStateOf(
            List(rows) { r ->
                List(cols) { c ->
                    val initTile = initialState?.tileAt(Pos(r, c))
                    if (initTile != null) Tile(Pos(r, c), initTile.type)
                    else Tile(Pos(r, c), TileType.EMPTY)
                }
            }
        )
    }

    var walls by remember { mutableStateOf(initialState?.walls ?: emptySet()) }
    var selectedCell by remember { mutableStateOf<Pos?>(null) }

    var playerStart by remember { mutableStateOf(initialState?.playerPos ?: Pos(0, 0)) }
    var enemyStart by remember { mutableStateOf(initialState?.enemyPos ?: Pos(0, cols - 1)) }

    var mode by remember { mutableStateOf(EditorMode.TILES) }

    // New: map name state (pre-fill from initialState.name if present)
    var mapName by remember { mutableStateOf(initialState?.name ?: "") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text("Map Editor", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Button(onClick = { mode = EditorMode.TILES }) { Text("Tiles", fontSize = 12.sp) }
            Button(onClick = { mode = EditorMode.WALLS }) { Text("Walls", fontSize = 12.sp) }
            Button(onClick = { mode = EditorMode.PLAYER_START }) { Text("Set Player", fontSize = 11.sp) }
            Button(onClick = { mode = EditorMode.ENEMY_START }) { Text("Set Enemy", fontSize = 10.sp) }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Map name input
        OutlinedTextField(
            value = mapName,
            onValueChange = { mapName = it },
            label = { Text("Map name (optional)") },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Grid + Canvas for walls
        Box {
            Column {
                for (r in 0 until rows) {
                    Row {
                        for (c in 0 until cols) {
                            val pos = Pos(r, c)
                            val tile = tilesGrid[r][c]

                            val baseColor = when (tile.type) {
                                TileType.EMPTY -> Color(0xFFEEEEEE)
                                TileType.TRAP -> Color(0xFF9C27B0)
                                TileType.EXIT -> Color(0xFFFFEB3B)
                            }

                            val isSelected = selectedCell == pos
                            val borderColor = when {
                                playerStart == pos -> Color(0xFF1B5E20)
                                enemyStart == pos -> Color(0xFFB71C1C)
                                isSelected -> Color.Cyan
                                else -> Color.Transparent
                            }
                            val borderWidth =
                                if (isSelected || playerStart == pos || enemyStart == pos) 3.dp else 0.dp

                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(2.dp)
                                    .border(borderWidth, borderColor, RoundedCornerShape(6.dp))
                                    .background(baseColor, RoundedCornerShape(6.dp))
                                    .clickable {
                                        when (mode) {
                                            EditorMode.TILES -> {
                                                tilesGrid = tilesGrid.mapIndexed { rr, row ->
                                                    if (rr != r) row else row.mapIndexed { cc, t ->
                                                        if (cc != c) t else t.copy(type = nextType(t.type))
                                                    }
                                                }
                                            }

                                            EditorMode.WALLS -> {
                                                if (selectedCell == null) {
                                                    selectedCell = pos
                                                } else {
                                                    val first = selectedCell!!
                                                    if (first == pos) {
                                                        selectedCell = null
                                                    } else if (areNeighbors(first, pos)) {
                                                        walls = toggleWall(walls, first, pos)
                                                        selectedCell = null
                                                    } else {
                                                        selectedCell = pos
                                                    }
                                                }
                                            }

                                            EditorMode.PLAYER_START -> {
                                                playerStart = pos
                                            }

                                            EditorMode.ENEMY_START -> {
                                                enemyStart = pos
                                            }
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                when {
                                    playerStart == pos -> Text("P", color = Color.White)
                                    enemyStart == pos -> Text("E", color = Color.White)
                                    tile.type == TileType.TRAP -> Text("T", color = Color.White)
                                    tile.type == TileType.EXIT -> Text("Exit")
                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }

            // Overlay for walls
            Canvas(
                modifier = Modifier
                    .matchParentSize()
            ) {
                val cellSize = size.width / cols
                val stroke = (cellSize * 0.1f).coerceAtLeast(6f)

                walls.forEach { (a, b) ->
                    if (a.r == b.r && abs(a.c - b.c) == 1) {
                        val rowTop = a.r * cellSize
                        val rowBottom = rowTop + cellSize
                        val dividerX = maxOf(a.c, b.c) * cellSize

                        drawLine(
                            color = Color.Black,
                            start = androidx.compose.ui.geometry.Offset(dividerX, rowTop),
                            end = androidx.compose.ui.geometry.Offset(dividerX, rowBottom),
                            strokeWidth = stroke
                        )
                    } else if (a.c == b.c && abs(a.r - b.r) == 1) {
                        val colLeft = a.c * cellSize
                        val colRight = colLeft + cellSize
                        val dividerY = maxOf(a.r, b.r) * cellSize

                        drawLine(
                            color = Color.Black,
                            start = androidx.compose.ui.geometry.Offset(colLeft, dividerY),
                            end = androidx.compose.ui.geometry.Offset(colRight, dividerY),
                            strokeWidth = stroke
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text("Walls: tap cell → tap neighbor to toggle", fontSize = 12.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = { onBack() }) { Text("Back") }
            Button(onClick = {
                val tilesList: List<Tile> = tilesGrid.flatten()
                val resultState = GameState(
                    rows = rows,
                    cols = cols,
                    tiles = tilesList,
                    playerPos = playerStart,
                    enemyPos = enemyStart,
                    walls = walls,
                    enemyFrozenTurns = 0,
                    turn = Turn.PLAYER,
                    result = GameResult.Ongoing,
                    playerMoves = 0,
                    name = mapName.ifBlank { "Custom Map" }
                )
                onSave(resultState)
                onBack()
            }) {
                Text("Save")
            }
        }
    }
}

private fun nextType(current: TileType): TileType =
    when (current) {
        TileType.EMPTY -> TileType.TRAP
        TileType.TRAP -> TileType.EXIT
        TileType.EXIT -> TileType.EMPTY
    }

private fun areNeighbors(a: Pos, b: Pos): Boolean {
    return a.manhattan(b) == 1
}

private fun toggleWall(set: Set<Pair<Pos, Pos>>, a: Pos, b: Pos): Set<Pair<Pos, Pos>> {
    val pair = a to b
    val reverse = b to a
    return if (pair in set || reverse in set) {
        set.filterNot { it == pair || it == reverse }.toSet()
    } else {
        set + pair
    }
}
