package com.example.wappo_game.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UseCasesTest {

    @Test
    fun `player cannot move out of bounds`() {
        val state = createDefaultGameState()
        val newState = movePlayer(state, Pos(-1, 0))
        assertThat(newState).isEqualTo(state)
    }

    @Test
    fun `player cannot move more than 1 cell`() {
        val state = createDefaultGameState()
        val newState = movePlayer(state, Pos(0, 2))
        assertThat(newState).isEqualTo(state)
    }

    @Test
    fun `player cannot move through wall`() {
        val state = createDefaultGameState()
        val blockedTo = Pos(0, 1)
        val newState = movePlayer(state, blockedTo)
        assertThat(newState).isEqualTo(state)
    }

    @Test
    fun `player loses if moves onto enemy`() {
        val testState = GameState(
            rows = 6,
            cols = 6,
            tiles = List(36) { idx ->
                val r = idx / 6
                val c = idx % 6
                Tile(Pos(r, c), TileType.EMPTY)
            },
            playerPos = Pos(0, 0),
            enemyPos = Pos(0, 1),
            walls = emptySet()
        )

        val newState = movePlayer(testState, Pos(0, 1))

        println("Player moves from ${testState.playerPos} to ${Pos(0,1)}, enemy at ${testState.enemyPos}")

        assertThat(newState.result).isInstanceOf(GameResult.PlayerLost::class.java)
    }

    @Test
    fun `player loses if steps on trap`() {
        val trapPos = Pos(0, 4)
        val state = createDefaultGameState().copy(playerPos = Pos(0, 3))
        val newState = movePlayer(state, trapPos)
        assertThat(newState.result).isInstanceOf(GameResult.PlayerLost::class.java)
    }

    @Test
    fun `player wins if reaches exit`() {
        val exitPos = Pos(5, 5)
        val state = createDefaultGameState().copy(playerPos = Pos(5, 4))
        val newState = movePlayer(state, exitPos)
        assertThat(newState.result).isInstanceOf(GameResult.PlayerWon::class.java)
    }

    @Test
    fun `enemy prefers horizontal move`() {
        val state = createDefaultGameState().copy(playerPos = Pos(0, 0), enemyPos = Pos(0, 2))
        val next = stepTowardPlayerWithPriority(state, state.enemyPos)
        assertThat(next).isEqualTo(Pos(0, 1))
    }

    @Test
    fun `enemy falls back to vertical if horizontal blocked`() {
        val state = createDefaultGameState().copy(
            playerPos = Pos(1, 2),
            enemyPos = Pos(0, 2),
            walls = setOf(Pos(0, 2) to Pos(0, 1))
        )
        val next = stepTowardPlayerWithPriority(state, state.enemyPos)
        assertThat(next).isEqualTo(Pos(1, 2))
    }

    @Test
    fun `enemy stays if blocked everywhere`() {
        val pos = Pos(0, 2)
        val state = createDefaultGameState().copy(
            playerPos = Pos(1, 2),
            enemyPos = pos,
            walls = setOf(
                pos to Pos(0, 1),
                pos to Pos(1, 2)
            )
        )
        val next = stepTowardPlayerWithPriority(state, state.enemyPos)
        assertThat(next).isEqualTo(pos)
    }

    @Test
    fun `enemy path gives up to 2 steps`() {
        val state = createDefaultGameState().copy(
            playerPos = Pos(0, 0),
            enemyPos = Pos(0, 3)
        )
        val path = enemyPath(state)
        assertThat(path.size).isAtMost(2)
    }

    @Test
    fun `enemy path empty if frozen`() {
        val state = createDefaultGameState().copy(enemyFrozenTurns = 2)
        val path = enemyPath(state)
        assertThat(path).isEmpty()
    }

    @Test
    fun `enemy stops on trap`() {
        val trapPos = Pos(0, 4)
        val state = createDefaultGameState().copy(enemyPos = Pos(0, 3), playerPos = Pos(5, 5))
        val path = enemyPath(state)
        assertThat(path.last()).isEqualTo(trapPos)
    }
}
