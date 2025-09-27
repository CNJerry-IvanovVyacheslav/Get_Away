package com.example.wappo_game.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wappo_game.domain.*
import com.example.wappo_game.presentation.GameViewModel
import kotlin.math.abs

enum class EditorMode { TILES, WALLS, PLAYER_START, ENEMY_START }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: GameViewModel,
    initialState: GameState? = null,
    onGoToMenu: () -> Unit,
    rows: Int = 6,
    cols: Int = 6
) {
    val configuration = LocalConfiguration.current
    val isLandscape =
        configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

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
    var mapName by remember { mutableStateOf(initialState?.name ?: "") }

    val snackbarHostState = remember { SnackbarHostState() }
    var showDuplicateSnackbar by remember { mutableStateOf(false) }

    Scaffold(snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { padding ->

        if (!isLandscape) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Map Editor", fontSize = 20.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(onClick = { mode = EditorMode.TILES }) {
                        Text(
                            "Tiles",
                            fontSize = 12.sp
                        )
                    }
                    Button(onClick = { mode = EditorMode.WALLS }) {
                        Text(
                            "Walls",
                            fontSize = 12.sp
                        )
                    }
                    Button(onClick = { mode = EditorMode.PLAYER_START }) {
                        Text(
                            "Set Player",
                            fontSize = 11.sp
                        )
                    }
                    Button(onClick = { mode = EditorMode.ENEMY_START }) {
                        Text(
                            "Set Enemy",
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = mapName,
                    onValueChange = { mapName = it },
                    label = { Text("Map name (optional)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                MapCanvas(
                    tilesGrid = tilesGrid,
                    walls = walls,
                    playerStart = playerStart,
                    enemyStart = enemyStart,
                    mode = mode,
                    onTileClick = { r, c ->
                        val pos = Pos(r, c)
                        when (mode) {
                            EditorMode.TILES -> {
                                tilesGrid = tilesGrid.mapIndexed { rr, row ->
                                    if (rr != r) row else row.mapIndexed { cc, t ->
                                        if (cc != c) t else t.copy(type = nextType(t.type))
                                    }
                                }
                            }

                            EditorMode.WALLS -> {
                                if (selectedCell == null) selectedCell = pos
                                else {
                                    val first = selectedCell!!
                                    if (first == pos) selectedCell = null
                                    else if (areNeighbors(first, pos)) {
                                        walls = toggleWall(walls, first, pos)
                                        selectedCell = null
                                    } else selectedCell = pos
                                }
                            }

                            EditorMode.PLAYER_START -> playerStart = pos
                            EditorMode.ENEMY_START -> enemyStart = pos
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text("Walls: tap cell → tap neighbor to toggle", fontSize = 12.sp)

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { onGoToMenu() }) { Text("Back") }
                    Button(onClick = {
                        val finalName = mapName.ifBlank { "Custom Map" }
                        val tilesList = tilesGrid.flatten()
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
                            name = finalName
                        )

                        val isDuplicate =
                            viewModel.savedMaps.value.any { it.name == finalName && it != initialState }

                        if (isDuplicate) {
                            showDuplicateSnackbar = true
                            return@Button
                        }

                        viewModel.saveCustomMap(resultState)

                        onGoToMenu()
                    }) {
                        Text("Save")
                    }

                    if (showDuplicateSnackbar) {
                        LaunchedEffect(showDuplicateSnackbar) {
                            snackbarHostState.showSnackbar("Map with this name already exists!")
                            showDuplicateSnackbar = false
                        }
                    }
                }
            }

        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(onClick = { mode = EditorMode.TILES }) {
                        Text(
                            "Tiles",
                            fontSize = 12.sp
                        )
                    }
                    Button(onClick = { mode = EditorMode.WALLS }) {
                        Text(
                            "Walls",
                            fontSize = 12.sp
                        )
                    }
                    Button(onClick = { mode = EditorMode.PLAYER_START }) {
                        Text(
                            "Set Player",
                            fontSize = 11.sp
                        )
                    }
                    Button(onClick = { mode = EditorMode.ENEMY_START }) {
                        Text(
                            "Set Enemy",
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(modifier = Modifier) {
                    MapCanvas(
                        tilesGrid = tilesGrid,
                        walls = walls,
                        playerStart = playerStart,
                        enemyStart = enemyStart,
                        mode = mode,
                        onTileClick = { r, c ->
                            val pos = Pos(r, c)
                            when (mode) {
                                EditorMode.TILES -> {
                                    tilesGrid = tilesGrid.mapIndexed { rr, row ->
                                        if (rr != r) row else row.mapIndexed { cc, t ->
                                            if (cc != c) t else t.copy(type = nextType(t.type))
                                        }
                                    }
                                }

                                EditorMode.WALLS -> {
                                    if (selectedCell == null) selectedCell = pos
                                    else {
                                        val first = selectedCell!!
                                        if (first == pos) selectedCell = null
                                        else if (areNeighbors(first, pos)) {
                                            walls = toggleWall(walls, first, pos)
                                            selectedCell = null
                                        } else selectedCell = pos
                                    }
                                }

                                EditorMode.PLAYER_START -> playerStart = pos
                                EditorMode.ENEMY_START -> enemyStart = pos
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Map Editor", fontSize = 20.sp)

                    OutlinedTextField(
                        value = mapName,
                        onValueChange = { mapName = it },
                        label = { Text("Map name (optional)") }
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { onGoToMenu() }) { Text("Back") }
                        Button(onClick = {
                            val finalName = mapName.ifBlank { "Custom Map" }
                            val tilesList = tilesGrid.flatten()
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
                                name = finalName
                            )

                            if (initialState == null && viewModel.savedMaps.value.any { it.name == finalName }) {
                                showDuplicateSnackbar = true
                                return@Button
                            }

                            viewModel.saveCustomMap(resultState)
                            onGoToMenu()
                        }) { Text("Save") }
                    }

                    Text("Walls: tap cell → tap neighbor to toggle", fontSize = 12.sp)
                }
            }

            if (showDuplicateSnackbar) {
                LaunchedEffect(showDuplicateSnackbar) {
                    snackbarHostState.showSnackbar("Map with this name already exists!")
                    showDuplicateSnackbar = false
                }
            }
        }
    }
}

@Composable
fun MapCanvas(
    tilesGrid: List<List<Tile>>,
    walls: Set<Pair<Pos, Pos>>,
    playerStart: Pos,
    enemyStart: Pos,
    mode: EditorMode,
    onTileClick: (r: Int, c: Int) -> Unit
) {
    Box {
        Column {
            for (r in tilesGrid.indices) {
                Row {
                    for (c in tilesGrid[0].indices) {
                        val tile = tilesGrid[r][c]

                        val baseColor = when (tile.type) {
                            TileType.EMPTY -> Color(0xFFEEEEEE)
                            TileType.TRAP -> Color(0xFF9C27B0)
                            TileType.EXIT -> Color(0xFFFFEB3B)
                        }

                        val borderColor = when {
                            playerStart == Pos(r, c) -> Color(0xFF1B5E20)
                            enemyStart == Pos(r, c) -> Color(0xFFB71C1C)
                            else -> Color.Transparent
                        }
                        val borderWidth = if (borderColor != Color.Transparent) 3.dp else 0.dp

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(2.dp)
                                .border(borderWidth, borderColor, RoundedCornerShape(6.dp))
                                .background(baseColor, RoundedCornerShape(6.dp))
                                .clickable { onTileClick(r, c) },
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                playerStart == Pos(r, c) -> Text("P", color = Color.White)
                                enemyStart == Pos(r, c) -> Text("E", color = Color.White)
                                tile.type == TileType.TRAP -> Text("T", color = Color.White)
                                tile.type == TileType.EXIT -> Text("Exit")
                            }
                        }
                    }
                }
            }
        }

        Canvas(modifier = Modifier.matchParentSize()) {
            val cellSize = size.width / tilesGrid[0].size
            val stroke = (cellSize * 0.1f).coerceAtLeast(6f)

            walls.forEach { (a, b) ->
                if (a.r == b.r && abs(a.c - b.c) == 1) {
                    val rowTop = a.r * cellSize
                    val rowBottom = rowTop + cellSize
                    val dividerX = maxOf(a.c, b.c) * cellSize
                    drawLine(
                        Color.Black,
                        start = androidx.compose.ui.geometry.Offset(dividerX, rowTop),
                        end = androidx.compose.ui.geometry.Offset(dividerX, rowBottom),
                        strokeWidth = stroke
                    )
                } else if (a.c == b.c && abs(a.r - b.r) == 1) {
                    val colLeft = a.c * cellSize
                    val colRight = colLeft + cellSize
                    val dividerY = maxOf(a.r, b.r) * cellSize
                    drawLine(
                        Color.Black,
                        start = androidx.compose.ui.geometry.Offset(colLeft, dividerY),
                        end = androidx.compose.ui.geometry.Offset(colRight, dividerY),
                        strokeWidth = stroke
                    )
                }
            }
        }
    }
}

internal fun nextType(current: TileType): TileType = when (current) {
    TileType.EMPTY -> TileType.TRAP
    TileType.TRAP -> TileType.EXIT
    TileType.EXIT -> TileType.EMPTY
}

internal fun areNeighbors(a: Pos, b: Pos): Boolean = a.manhattan(b) == 1

internal fun toggleWall(set: Set<Pair<Pos, Pos>>, a: Pos, b: Pos): Set<Pair<Pos, Pos>> {
    val pair = a to b
    val reverse = b to a
    return if (pair in set || reverse in set) set.filterNot { it == pair || it == reverse }.toSet()
    else set + pair
}
