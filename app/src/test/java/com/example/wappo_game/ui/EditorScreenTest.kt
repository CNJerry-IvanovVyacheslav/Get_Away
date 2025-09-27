package com.example.wappo_game.ui

import com.example.wappo_game.domain.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Test

class EditorScreenTest {

    private lateinit var viewModel: GameViewModelFake

    @Before
    fun setUp() {
        viewModel = GameViewModelFake()
    }

    class GameViewModelFake {
        val savedMaps: StateFlow<List<GameState>> = MutableStateFlow(emptyList())
        private val _maps = mutableListOf<GameState>()

        fun saveCustomMap(state: GameState) {
            _maps.add(state)
            (savedMaps as MutableStateFlow).value = _maps.toList()
        }
    }

    @Test
    fun `nextType cycles correctly`() {
        assertThat(nextType(TileType.EMPTY)).isEqualTo(TileType.TRAP)
        assertThat(nextType(TileType.TRAP)).isEqualTo(TileType.EXIT)
        assertThat(nextType(TileType.EXIT)).isEqualTo(TileType.EMPTY)
    }

    @Test
    fun `areNeighbors detects adjacent cells correctly`() {
        val a = Pos(0, 0)
        val b = Pos(0, 1)
        val c = Pos(1, 0)
        val d = Pos(1, 1)
        assertThat(areNeighbors(a, b)).isTrue()
        assertThat(areNeighbors(a, c)).isTrue()
        assertThat(areNeighbors(a, d)).isFalse()
    }

    @Test
    fun `toggleWall adds and removes correctly`() {
        val a = Pos(0, 0)
        val b = Pos(0, 1)
        var walls = emptySet<Pair<Pos, Pos>>()

        // add
        walls = toggleWall(walls, a, b)
        assertThat(walls).contains(a to b)

        // remove
        walls = toggleWall(walls, a, b)
        assertThat(walls).doesNotContain(a to b)
    }

    @Test
    fun `changing mode updates mode correctly`() {
        var mode = EditorMode.TILES
        mode = EditorMode.PLAYER_START
        assertThat(mode).isEqualTo(EditorMode.PLAYER_START)
        mode = EditorMode.ENEMY_START
        assertThat(mode).isEqualTo(EditorMode.ENEMY_START)
    }

    @Test
    fun `clicking tile in TILES mode cycles tile type`() {
        var tilesGrid = List(2) { r ->
            List(2) { c -> Tile(Pos(r, c), TileType.EMPTY) }
        }

        val r = 0
        val c = 0
        val current = tilesGrid[r][c]
        tilesGrid = tilesGrid.mapIndexed { rr, row ->
            if (rr != r) row else row.mapIndexed { cc, t ->
                if (cc != c) t else t.copy(type = nextType(t.type))
            }
        }
        assertThat(tilesGrid[r][c].type).isEqualTo(TileType.TRAP)
    }

    @Test
    fun `clicking tile in WALLS mode toggles wall between neighbors`() {
        var walls = emptySet<Pair<Pos, Pos>>()
        val first = Pos(0, 0)
        val second = Pos(0, 1)

        walls = toggleWall(walls, first, second)
        assertThat(walls).contains(first to second)

        walls = toggleWall(walls, first, second)
        assertThat(walls).doesNotContain(first to second)
    }

    @Test
    fun `clicking tile sets playerStart and enemyStart`() {
        var playerStart = Pos(0, 0)
        var enemyStart = Pos(0, 1)

        playerStart = Pos(1, 1)
        enemyStart = Pos(1, 0)

        assertThat(playerStart).isEqualTo(Pos(1, 1))
        assertThat(enemyStart).isEqualTo(Pos(1, 0))
    }

    @Test
    fun `saving map adds it to viewModel`() {
        val map = GameState(
            rows = 2,
            cols = 2,
            tiles = listOf(Tile(Pos(0, 0)), Tile(Pos(0, 1))),
            playerPos = Pos(0, 0),
            enemyPos = Pos(0, 1),
            name = "CustomMap"
        )
        viewModel.saveCustomMap(map)
        assertThat(viewModel.savedMaps.value.any { it.name == "CustomMap" }).isTrue()
    }
}
