package com.example.wappo_game.ui

import com.example.wappo_game.domain.*
import org.junit.Test
import com.google.common.truth.Truth.assertThat

class GameScreenTest {

    @Test
    fun `player moves increment counter`() {
        val state = createDefaultGameState()
        val movedState = state.copy(playerMoves = state.playerMoves + 1)

        assertThat(movedState.playerMoves).isEqualTo(1)
    }

    @Test
    fun `tileAt returns correct tile`() {
        val state = createDefaultGameState()
        val trapPos = Pos(0, 4)
        val tile = state.tileAt(trapPos)

        assertThat(tile?.type).isEqualTo(TileType.TRAP)
    }

    @Test
    fun `isBlocked detects wall correctly`() {
        val walls = setOf(Pos(0, 0) to Pos(0, 1))
        val state = createDefaultGameState().copy(walls = walls)

        assertThat(state.isBlocked(Pos(0, 0), Pos(0, 1))).isTrue()
        assertThat(state.isBlocked(Pos(0, 1), Pos(0, 0))).isTrue()
        assertThat(state.isBlocked(Pos(0, 0), Pos(1, 0))).isFalse()
    }

    @Test
    fun `player reaches exit`() {
        val state = createDefaultGameState().copy(playerPos = Pos(5,5))
        val result = if (state.tileAt(state.playerPos)?.type == TileType.EXIT)
            GameResult.PlayerWon else GameResult.Ongoing

        assertThat(result).isEqualTo(GameResult.PlayerWon)
    }
}