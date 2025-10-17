package com.example.wappo_game.domain

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class UseCasesTest {

    private fun createTestState(
        playerPos: Pos = Pos(0, 0),
        enemyPositions: List<Pos> = listOf(Pos(5, 5)),
        walls: Set<Pair<Pos, Pos>> = emptySet(),
        enemyFrozenTurns: List<Int> = listOf(0)
    ): GameState {
        return createLevel(
            name = "UseCases Test",
            playerPos = playerPos,
            enemyPositions = enemyPositions,
            walls = walls,
        ).copy(enemyFrozenTurns = enemyFrozenTurns)
    }

    @Test
    fun `player cannot move out of bounds`() {
        val state = createTestState(playerPos = Pos(0,0))
        val newState = movePlayer(state, Pos(-1, 0))
        assertThat(newState.playerPos).isEqualTo(state.playerPos)
    }

    @Test
    fun `player cannot move more than 1 cell`() {
        val state = createTestState(playerPos = Pos(0,0))
        val newState = movePlayer(state, Pos(0, 2))
        assertThat(newState.playerPos).isEqualTo(state.playerPos)
    }

    @Test
    fun `player cannot move through wall`() {
        val state = createTestState(playerPos = Pos(0, 0), walls = setOf(Pos(0,0) to Pos(0,1)))
        val blockedTo = Pos(0, 1)
        val newState = movePlayer(state, blockedTo)
        assertThat(newState.playerPos).isEqualTo(state.playerPos)
    }

    @Test
    fun `player loses if moves onto enemy`() {
        val enemyPos = Pos(0, 1)
        val state = createTestState(playerPos = Pos(0, 0), enemyPositions = listOf(enemyPos))
        val newState = movePlayer(state, enemyPos)
        assertThat(newState.result).isInstanceOf(GameResult.PlayerLost::class.java)
    }

    @Test
    fun `player loses if steps on trap`() {
        val trapPos = Pos(0, 1)
        val state = createLevel(name = "Trap Test", playerPos = Pos(0, 0), traps = listOf(trapPos))
        val newState = movePlayer(state, trapPos)
        assertThat(newState.result).isInstanceOf(GameResult.PlayerLost::class.java)
    }

    @Test
    fun `player wins if reaches exit`() {
        val exitPos = Pos(0, 1)
        val state = createLevel(name = "Exit Test", playerPos = Pos(0, 0), exit = exitPos)
        val newState = movePlayer(state, exitPos)
        assertThat(newState.result).isInstanceOf(GameResult.PlayerWon::class.java)
    }

    @Test
    fun `enemy prefers horizontal move`() {
        val state = createTestState(playerPos = Pos(0, 0), enemyPositions = listOf(Pos(0, 2)))
        val next = stepTowardPlayerWithPriority(state, state.enemyPositions.first())
        assertThat(next).isEqualTo(Pos(0, 1))
    }

    @Test
    fun `enemy falls back to vertical if horizontal blocked`() {
        val state = createTestState(
            playerPos = Pos(1, 2),
            enemyPositions = listOf(Pos(0, 2)),
            walls = setOf(Pos(0, 2) to Pos(0, 1))
        )
        val next = stepTowardPlayerWithPriority(state, state.enemyPositions.first())
        assertThat(next).isEqualTo(Pos(1, 2))
    }

    @Test
    fun `enemy path gives up to 2 steps`() {
        val state = createTestState(playerPos = Pos(0, 0), enemyPositions = listOf(Pos(0, 3)))
        val paths = enemyPaths(state)
        assertThat(paths.first().size).isAtMost(2)
        assertThat(paths.first()).containsExactly(Pos(0, 2), Pos(0, 1)).inOrder()
    }

    @Test
    fun `enemy path is empty if frozen`() {
        val state = createTestState(enemyFrozenTurns = listOf(2))
        val paths = enemyPaths(state)
        assertThat(paths.first()).isEmpty()
    }
}