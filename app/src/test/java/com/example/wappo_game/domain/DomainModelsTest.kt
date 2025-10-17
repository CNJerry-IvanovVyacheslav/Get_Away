package com.example.wappo_game.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DomainModelsTest {

    private fun createTestState(): GameState {
        return createLevel(
            name = "Test Level",
            rows = 6,
            cols = 6,
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(5, 0)),
            traps = listOf(Pos(1, 1)),
            exit = Pos(5, 5),
            walls = setOf(Pos(2, 2) to Pos(2, 3))
        )
    }

    @Test
    fun `manhattan distance calculates correctly`() {
        val a = Pos(0, 0)
        val b = Pos(3, 4)
        assertThat(a.manhattan(b)).isEqualTo(7)
    }

    @Test
    fun `inBounds returns true for positions inside the grid`() {
        val state = createTestState()
        assertThat(state.inBounds(Pos(0, 0))).isTrue()
        assertThat(state.inBounds(Pos(5, 5))).isTrue()
    }

    @Test
    fun `inBounds returns false for positions outside the grid`() {
        val state = createTestState()
        assertThat(state.inBounds(Pos(-1, 0))).isFalse()
        assertThat(state.inBounds(Pos(0, 6))).isFalse()
    }

    @Test
    fun `tileAt returns correct tile or null`() {
        val state = createTestState()
        val tile = state.tileAt(Pos(0, 0))
        assertThat(tile).isNotNull()
        assertThat(tile?.pos).isEqualTo(Pos(0, 0))
        assertThat(state.tileAt(Pos(10, 10))).isNull()
    }

    @Test
    fun `isBlocked returns true for walls`() {
        val state = createTestState()
        assertThat(state.isBlocked(Pos(2, 2), Pos(2, 3))).isTrue()
        assertThat(state.isBlocked(Pos(2, 3), Pos(2, 2))).isTrue()
    }

    @Test
    fun `isBlocked returns false for non-walls`() {
        val state = createTestState()
        assertThat(state.isBlocked(Pos(0, 0), Pos(0, 1))).isFalse()
    }

    @Test
    fun `createLevel sets correct initial positions`() {
        val state = createTestState()
        assertThat(state.playerPos).isEqualTo(Pos(0, 0))
        assertThat(state.enemyPositions.first()).isEqualTo(Pos(5, 0))
        assertThat(state.initialPlayerPos).isEqualTo(Pos(0, 0))
        assertThat(state.initialEnemyPositions.first()).isEqualTo(Pos(5, 0))
    }

    @Test
    fun `traps and exit tiles are set correctly by createLevel`() {
        val state = createTestState()
        assertThat(state.tileAt(Pos(1, 1))?.type).isEqualTo(TileType.TRAP)
        assertThat(state.tileAt(Pos(5, 5))?.type).isEqualTo(TileType.EXIT)
    }
}