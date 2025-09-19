package com.example.wappo_game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.domain.Pos
import com.example.wappo_game.domain.Tile
import com.example.wappo_game.domain.TileType

@Composable
fun EditorScreen(
    onBack: () -> Unit,
    onSave: (GameState) -> Unit,
    rows: Int = 6,
    cols: Int = 6
) {
    // tilesGrid: immutable List<List<Tile>> wrapped in state;
    // при изменении переопределяем whole structure — это просто и корректно для Compose.
    var tilesGrid by remember {
        mutableStateOf(
            List(rows) { r ->
                List(cols) { c ->
                    Tile(Pos(r, c), TileType.EMPTY)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text("Map Editor", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Column {
            for (r in 0 until rows) {
                Row {
                    for (c in 0 until cols) {
                        val tile = tilesGrid[r][c]
                        val color = when (tile.type) {
                            TileType.EMPTY -> Color(0xFFEEEEEE)
                            TileType.TRAP -> Color(0xFF9C27B0)
                            TileType.EXIT -> Color(0xFFFFEB3B)
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .padding(2.dp)
                                .background(color, RoundedCornerShape(6.dp))
                                .clickable {
                                    // обновляем конкретную клетку: переключаем тип
                                    tilesGrid = tilesGrid.mapIndexed { rr, row ->
                                        if (rr != r) row
                                        else row.mapIndexed { cc, t ->
                                            if (cc != c) t
                                            else t.copy(type = nextType(t.type))
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            when (tile.type) {
                                TileType.TRAP -> Text("T", fontSize = 12.sp, color = Color.White)
                                TileType.EXIT -> Text("Exit", fontSize = 12.sp, color = Color.Black)
                                else -> {}
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = { onBack() }) {
                Text("Back")
            }

            Button(onClick = {
                // При сохранении формируем GameState с tiles = flatten list
                val tilesList: List<Tile> = tilesGrid.flatten()
                val state = GameState(
                    rows = rows,
                    cols = cols,
                    tiles = tilesList,
                    playerPos = Pos(0, 0),              // стартовые позиции можно сделать на выбор позже
                    enemyPos = Pos(0, cols - 1),
                    walls = emptySet(),                // на старте без стен; можно расширить
                    enemyFrozenTurns = 0,
                    turn = com.example.wappo_game.domain.Turn.PLAYER,
                    result = com.example.wappo_game.domain.GameResult.Ongoing,
                    // если в твоём GameState есть playerMoves - укажи 0
                    playerMoves = 0
                )
                onSave(state)
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