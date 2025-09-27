package com.example.wappo_game.ui

import com.example.wappo_game.domain.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class EditorUtilsTest {

    @Test
    fun `nextType cycles through tile types`() {
        assertThat(nextType(TileType.EMPTY)).isEqualTo(TileType.TRAP)
        assertThat(nextType(TileType.TRAP)).isEqualTo(TileType.EXIT)
        assertThat(nextType(TileType.EXIT)).isEqualTo(TileType.EMPTY)
    }

    @Test
    fun `areNeighbors detects neighbors correctly`() {
        val a = Pos(0, 0)
        val b = Pos(0, 1)
        val c = Pos(1, 0)
        val d = Pos(1, 1)
        val e = Pos(2, 2)

        assertThat(areNeighbors(a, b)).isTrue()
        assertThat(areNeighbors(a, c)).isTrue()
        assertThat(areNeighbors(a, d)).isFalse()
        assertThat(areNeighbors(a, e)).isFalse()
    }

    @Test
    fun `toggleWall adds and removes walls correctly`() {
        val a = Pos(0, 0)
        val b = Pos(0, 1)
        val c = Pos(1, 1)
        var walls = emptySet<Pair<Pos, Pos>>()

        walls = toggleWall(walls, a, b)
        assertThat(walls).contains(a to b)

        walls = toggleWall(walls, b, c)
        assertThat(walls).contains(b to c)

        walls = toggleWall(walls, a, b)
        assertThat(walls).doesNotContain(a to b)
        assertThat(walls).contains(b to c)
    }
}

class EditorScreenViewModelFakeTest {

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
    fun `saving map adds to savedMaps`() {
        val map = GameState(
            rows = 6,
            cols = 6,
            tiles = listOf(),
            playerPos = Pos(0, 0),
            enemyPos = Pos(0, 5),
            name = "TestMap"
        )
        viewModel.saveCustomMap(map)
        runBlocking {
            assertThat(viewModel.savedMaps.value.any { it.name == "TestMap" }).isTrue()
        }
    }
}
