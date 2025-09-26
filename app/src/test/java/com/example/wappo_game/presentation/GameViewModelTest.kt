package com.example.wappo_game.presentation

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.example.wappo_game.data.DataStoreManager
import com.example.wappo_game.domain.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private lateinit var tempFile: File
    private lateinit var manager: DataStoreManager
    private lateinit var viewModel: GameViewModelFake

    @Before
    fun setUp() {
        tempFile = File.createTempFile("test_prefs_${UUID.randomUUID()}", ".preferences_pb")
        val dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO),
            produceFile = { tempFile }
        )
        manager = DataStoreManager(dataStore)

        viewModel = GameViewModelFake(manager)

        val middleMap = createDefaultGameState().copy(
            playerPos = Pos(3, 3),
            initialPlayerPos = Pos(3, 3)
        )
        viewModel.loadCustomMap(middleMap, saveLast = false)
    }

    class GameViewModelFake(dataStoreManager: DataStoreManager) {

        val repo = com.example.wappo_game.data.InMemoryGameRepository(createDefaultGameState())
        val dataStore = dataStoreManager

        val state = repo.state
        val savedMaps = dataStore.loadMaps()
        private val _lastMapState = kotlinx.coroutines.flow.MutableStateFlow<GameState?>(null)
        val lastMapState = _lastMapState

        private lateinit var currentMap: GameState

        init {
            currentMap = createDefaultGameState()
            loadCustomMap(currentMap, saveLast = false)
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

        fun loadCustomMap(state: GameState, saveLast: Boolean = true) {
            val resetState = state.copy(
                playerPos = state.initialPlayerPos,
                enemyPos = state.initialEnemyPos,
                tiles = state.tiles.map { it.copy() },
                playerMoves = 0,
                result = GameResult.Ongoing
            )
            currentMap = state
            repo.setState(resetState)
            _lastMapState.value = state
        }
    }

    @Test
    fun `initial state is default map`() = runTest {
        val current = viewModel.state.value
        assertThat(current.playerPos).isEqualTo(Pos(3,3))
        assertThat(current.enemyPos).isEqualTo(Pos(0,5))
    }

    @Test
    fun `move player up decreases row`() = runTest {
        val before = viewModel.state.value.playerPos.r
        viewModel.moveUp()
        val after = viewModel.state.value.playerPos.r
        assertThat(after).isEqualTo(before - 1)
    }

    @Test
    fun `move player down increases row`() = runTest {
        val before = viewModel.state.value.playerPos.r
        viewModel.moveDown()
        val after = viewModel.state.value.playerPos.r
        assertThat(after).isEqualTo(before + 1)
    }

    @Test
    fun `move player left decreases column`() = runTest {
        val before = viewModel.state.value.playerPos.c
        viewModel.moveLeft()
        val after = viewModel.state.value.playerPos.c
        assertThat(after).isEqualTo(before - 1)
    }

    @Test
    fun `move player right increases column`() = runTest {
        val before = viewModel.state.value.playerPos.c
        viewModel.moveRight()
        val after = viewModel.state.value.playerPos.c
        assertThat(after).isEqualTo(before + 1)
    }

    @Test
    fun `load custom map resets player and enemy positions`() = runTest {
        val map = GameState(
            tiles = listOf(Tile(Pos(0,0))),
            playerPos = Pos(3,3),
            enemyPos = Pos(5,5),
            name = "TestMap"
        )
        viewModel.loadCustomMap(map)
        val state = viewModel.state.value
        assertThat(state.playerPos).isEqualTo(map.initialPlayerPos)
        assertThat(state.enemyPos).isEqualTo(map.initialEnemyPos)
    }

    @Test
    fun `save and load map via DataStore`() = runTest {
        val map = GameState(
            tiles = listOf(Tile(Pos(0,0))),
            playerPos = Pos(0,0),
            enemyPos = Pos(0,1),
            name = "MyMap"
        )
        viewModel.dataStore.saveOrUpdateMap(map)
        val maps = viewModel.savedMaps.first()
        assertThat(maps.any { it.name == "MyMap" }).isTrue()
    }
}
