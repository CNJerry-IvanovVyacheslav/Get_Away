package com.example.wappo_game.domain

import kotlin.math.abs
import kotlin.math.sign

/**
 * The enemy's step in the player's direction (one square at a time).
 * Returns null if already in place.
 */
fun stepTowardPlayer(from: Pos, target: Pos): Pos? {
    val dr = target.r - from.r
    val dc = target.c - from.c
    return when {
        abs(dr) > abs(dc) -> Pos(from.r + dr.sign, from.c)
        dc != 0 -> Pos(from.r, from.c + dc.sign)
        else -> null
    }
}

/**
 * Player's turn: if it is impossible, we return the state unchanged.
 * If the player gets to TRAP, it's a loss, if he gets to EXIT, it's a victory.
 * After a successful move, the turn goes to the enemy.
 */
fun movePlayer(state: GameState, to: Pos): GameState {
    if (state.result != GameResult.Ongoing) return state

    if (!state.inBounds(to)) return state
    if (state.isBlocked(state.playerPos, to)) return state

    val tile = state.tileAt(to) ?: return state

    return when (tile.type) {
        TileType.TRAP -> {
            state.copy(
                playerPos = to,
                result = GameResult.PlayerLost,
                playerMoves = state.playerMoves + 1
            )
        }
        TileType.EXIT -> {
            state.copy(
                playerPos = to,
                result = GameResult.PlayerWon,
                playerMoves = state.playerMoves + 1
            )
        }
        else -> {
            state.copy(
                playerPos = to,
                turn = Turn.ENEMY,
                playerMoves = state.playerMoves + 1
            )
        }
    }
}

/**
 * Enemy's move: takes up to 2 steps towards the player.
 * The enemy does NOT go around the walls — when it collides with the wall, it stops.
 * If an enemy steps on a TRAP, it gets enemyFrozenTurns = 3.
 * If the enemy is frozen (enemyFrozenTurns > 0) — lowers the counter and returns the turn to the player.
 */
fun moveEnemy(state: GameState): GameState {
    if (state.result !is GameResult.Ongoing) return state
    if (state.turn != Turn.ENEMY) return state

    // if frozen, skips 1 cycle and gives a turn to the player
    if (state.enemyFrozenTurns > 0) {
        return state.copy(enemyFrozenTurns = state.enemyFrozenTurns - 1, turn = Turn.PLAYER)
    }

    var curPos = state.enemyPos
    var st = state

    repeat(2) {
        val step = stepTowardPlayer(curPos, st.playerPos) ?: return@repeat
        if (!st.canMove(curPos, step)) {
            st = st.copy(enemyPos = curPos, turn = Turn.PLAYER)
            return st
        }

        curPos = step
        val tile = st.tileAt(curPos)

        st = when (tile?.type) {
            TileType.TRAP -> st.copy(enemyPos = curPos, enemyFrozenTurns = 3, turn = Turn.PLAYER)
            else -> st.copy(enemyPos = curPos)
        }

        if (curPos == st.playerPos) {
            return st.copy(result = GameResult.PlayerLost)
        }
    }

    return st.copy(turn = Turn.PLAYER)
}
