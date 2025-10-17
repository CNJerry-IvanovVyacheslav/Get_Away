package com.example.wappo_game.ui

import com.example.wappo_game.data.InMemoryGameRepository
import com.example.wappo_game.domain.GameResult
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.domain.Pos
import com.example.wappo_game.domain.TileType
import com.example.wappo_game.domain.createLevel
import com.example.wappo_game.domain.movePlayer
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Test

class ViewModelAndUITests {

    private lateinit var viewModel: GameViewModelFake
    private lateinit var initialMap: GameState

    class GameViewModelFake {
        private val _initialState = createLevel(name = "Initial", playerPos = Pos(3, 3))
        private val repo = InMemoryGameRepository(_initialState)

        val state: StateFlow<GameState> = repo.state

        private val _savedMaps = MutableStateFlow<List<GameState>>(emptyList())
        val savedMaps: StateFlow<List<GameState>> = _savedMaps

        private lateinit var currentMap: GameState

        init {
            loadCustomMap(_initialState)
        }

        fun moveUp() = tryMovePlayer(Pos(state.value.playerPos.r - 1, state.value.playerPos.c))
        fun moveDown() = tryMovePlayer(Pos(state.value.playerPos.r + 1, state.value.playerPos.c))
        fun moveLeft() = tryMovePlayer(Pos(state.value.playerPos.r, state.value.playerPos.c - 1))
        fun moveRight() = tryMovePlayer(Pos(state.value.playerPos.r, state.value.playerPos.c + 1))

        private fun tryMovePlayer(to: Pos) {
            val cur = repo.state.value
            if (cur.result !is GameResult.Ongoing) return

            val afterPlayer = movePlayer(cur, to).copy(playerMoves = cur.playerMoves + 1)
            repo.setState(afterPlayer)
        }

        fun loadCustomMap(level: GameState) {
            val resetState = level.copy(
                playerPos = level.initialPlayerPos,
                enemyPositions = level.initialEnemyPositions,
                tiles = level.tiles.map { it.copy() },
                playerMoves = 0,
                result = GameResult.Ongoing,
                enemyFrozenTurns = level.enemyPositions.map { 0 }
            )
            currentMap = level
            repo.setState(resetState)
        }

        fun saveCustomMap(map: GameState) {
            val current = _savedMaps.value.toMutableList()
            current.removeAll { it.name == map.name }
            current.add(map)
            _savedMaps.value = current
        }

        fun deleteMap(name: String) {
            val current = _savedMaps.value.toMutableList()
            current.removeAll { it.name == name }
            _savedMaps.value = current
        }

        fun clearAllMaps() {
            _savedMaps.value = emptyList()
        }
    }

    @Before
    fun setUp() {
        viewModel = GameViewModelFake()
        initialMap = createLevel(
            name = "Initial",
            playerPos = Pos(3, 3),
            enemyPositions = listOf(Pos(5,5))
        )
        viewModel.loadCustomMap(initialMap)
    }

    @Test
    fun `initial state is correct`() {
        val current = viewModel.state.value
        assertThat(current.playerPos).isEqualTo(Pos(3, 3))
        assertThat(current.enemyPositions.first()).isEqualTo(Pos(5, 5))
    }

    @Test
    fun `move player right increases column`() {
        val before = viewModel.state.value.playerPos.c
        viewModel.moveRight()
        val after = viewModel.state.value.playerPos.c
        assertThat(after).isEqualTo(before + 1)
    }

    @Test
    fun `loadCustomMap resets player and enemy positions`() {
        val newMap = createLevel(name = "New Map", playerPos = Pos(1, 1), enemyPositions = listOf(Pos(2, 2)))
        viewModel.loadCustomMap(newMap)
        val state = viewModel.state.value
        assertThat(state.name).isEqualTo("New Map")
        assertThat(state.playerPos).isEqualTo(newMap.initialPlayerPos)
        assertThat(state.enemyPositions).isEqualTo(newMap.initialEnemyPositions)
        assertThat(state.playerMoves).isEqualTo(0)
    }

    @Test
    fun `save map adds to saved maps`() {
        val map = createLevel(name = "Map1")
        viewModel.saveCustomMap(map)
        assertThat(viewModel.savedMaps.value.any { it.name == "Map1" }).isTrue()
    }

    @Test
    fun `delete map removes from saved maps`() {
        val map = createLevel(name = "MapToDelete")
        viewModel.saveCustomMap(map)
        viewModel.deleteMap("MapToDelete")
        assertThat(viewModel.savedMaps.value.any { it.name == "MapToDelete" }).isFalse()
    }

    @Test
    fun `clear all maps removes all maps`() {
        viewModel.saveCustomMap(createLevel(name = "Map1"))
        viewModel.saveCustomMap(createLevel(name = "Map2"))
        viewModel.clearAllMaps()
        assertThat(viewModel.savedMaps.value).isEmpty()
    }

    private fun nextType(type: TileType) = when (type) {
        TileType.EMPTY -> TileType.TRAP
        TileType.TRAP -> TileType.EXIT
        TileType.EXIT -> TileType.EMPTY
    }

    @Test
    fun `nextType cycles correctly for editor`() {
        assertThat(nextType(TileType.EMPTY)).isEqualTo(TileType.TRAP)
        assertThat(nextType(TileType.TRAP)).isEqualTo(TileType.EXIT)
        assertThat(nextType(TileType.EXIT)).isEqualTo(TileType.EMPTY)
    }

    @Test
    fun `player moves increment counter`() {
        val state = viewModel.state.value
        assertThat(state.playerMoves).isEqualTo(0)
        viewModel.moveDown()
        assertThat(viewModel.state.value.playerMoves).isEqualTo(1)
    }
}