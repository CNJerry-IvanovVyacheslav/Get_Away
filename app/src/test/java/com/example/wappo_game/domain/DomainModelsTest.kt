package com.example.wappo_game.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DomainModelsTest {

    @Test
    fun `manhattan distance calculates correctly`() {
        val a = Pos(0, 0)
        val b = Pos(3, 4)
        assertThat(a.manhattan(b)).isEqualTo(7)

        val c = Pos(2, 2)
        val d = Pos(2, 5)
        assertThat(c.manhattan(d)).isEqualTo(3)

        val e = Pos(1, 1)
        val f = Pos(1, 1)
        assertThat(e.manhattan(f)).isEqualTo(0)
    }

    @Test
    fun `inBounds returns true for positions inside the grid`() {
        val state = createDefaultGameState()
        assertThat(state.inBounds(Pos(0, 0))).isTrue()
        assertThat(state.inBounds(Pos(5, 5))).isTrue()
        assertThat(state.inBounds(Pos(3, 2))).isTrue()
    }

    @Test
    fun `inBounds returns false for positions outside the grid`() {
        val state = createDefaultGameState()
        assertThat(state.inBounds(Pos(-1, 0))).isFalse()
        assertThat(state.inBounds(Pos(0, -1))).isFalse()
        assertThat(state.inBounds(Pos(6, 0))).isFalse()
        assertThat(state.inBounds(Pos(0, 6))).isFalse()
    }

    @Test
    fun `tileAt returns correct tile or null`() {
        val state = createDefaultGameState()
        val tile = state.tileAt(Pos(0, 0))
        assertThat(tile).isNotNull()
        assertThat(tile?.pos).isEqualTo(Pos(0, 0))

        val emptyTile = state.tileAt(Pos(10, 10))
        assertThat(emptyTile).isNull()
    }

    @Test
    fun `isBlocked returns true for walls`() {
        val state = createDefaultGameState()
        val wallPair = Pos(0, 0) to Pos(0, 1)
        val reversePair = Pos(0, 1) to Pos(0, 0)
        assertThat(state.isBlocked(wallPair.first, wallPair.second)).isTrue()
        assertThat(state.isBlocked(reversePair.first, reversePair.second)).isTrue()
    }

    @Test
    fun `isBlocked returns false for non-walls`() {
        val state = createDefaultGameState()
        assertThat(state.isBlocked(Pos(0, 0), Pos(0, 2))).isFalse()
        assertThat(state.isBlocked(Pos(1, 1), Pos(2, 1))).isFalse()
    }

    @Test
    fun `default game state has correct initial positions`() {
        val state = createDefaultGameState()
        assertThat(state.playerPos).isEqualTo(Pos(0, 0))
        assertThat(state.enemyPos).isEqualTo(Pos(0, 5))
        assertThat(state.initialPlayerPos).isEqualTo(Pos(0, 0))
        assertThat(state.initialEnemyPos).isEqualTo(Pos(0, 5))
    }

    @Test
    fun `traps and exit tiles are set correctly`() {
        val state = createDefaultGameState()
        val trapPositions = listOf(Pos(0, 4), Pos(3, 2), Pos(4, 5))
        trapPositions.forEach { pos ->
            assertThat(state.tileAt(pos)?.type).isEqualTo(TileType.TRAP)
        }

        val exitPos = Pos(5, 5)
        assertThat(state.tileAt(exitPos)?.type).isEqualTo(TileType.EXIT)
    }

    @Test
    fun `walls set correctly`() {
        val state = createDefaultGameState()
        val walls = listOf(
            Pos(0, 0) to Pos(0, 1),
            Pos(1, 0) to Pos(1, 1),
            Pos(2, 2) to Pos(3, 2),
            Pos(4, 4) to Pos(4, 5)
        )
        walls.forEach { (a, b) ->
            assertThat(state.isBlocked(a, b)).isTrue()
            assertThat(state.isBlocked(b, a)).isTrue()
        }
    }
}