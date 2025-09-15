package com.example.wappo_game.domain

import kotlin.math.abs
import kotlin.math.sign

// --- Player ---

fun movePlayer(state: GameState, to: Pos): GameState {
    if (!state.inBounds(to)) return state
    if (state.isBlocked(state.playerPos, to)) return state

    val tile = state.tileAt(to) ?: return state

    return when (tile.type) {
        TileType.TRAP -> {
            state.copy(
                playerPos = to,
                result = GameResult.PlayerLost
            )
        }
        TileType.EXIT -> {
            state.copy(
                playerPos = to,
                result = GameResult.PlayerWon
            )
        }
        else -> {
            state.copy(
                playerPos = to,
                turn = Turn.ENEMY
            )
        }
    }
}

// --- Enemy ---

fun moveEnemy(state: GameState): GameState {
    if (state.enemyFrozenTurns > 0) {
        return state.copy(
            enemyFrozenTurns = state.enemyFrozenTurns - 1,
            turn = Turn.PLAYER
        )
    }

    var cur = state.enemyPos
    var st = state

    repeat(2) {
        val dir = when {
            st.playerPos.r < cur.r -> Pos(cur.r - 1, cur.c)
            st.playerPos.r > cur.r -> Pos(cur.r + 1, cur.c)
            st.playerPos.c < cur.c -> Pos(cur.r, cur.c - 1)
            st.playerPos.c > cur.c -> Pos(cur.r, cur.c + 1)
            else -> cur
        }

        if (!st.inBounds(dir)) return st.copy(turn = Turn.PLAYER)
        if (st.isBlocked(cur, dir)) return st.copy(turn = Turn.PLAYER)
        cur = dir
        val tile = st.tileAt(cur)

        st = when (tile?.type) {
            TileType.TRAP -> {
                st.copy(
                    enemyPos = cur,
                    enemyFrozenTurns = 3,
                    turn = Turn.PLAYER
                )
            }
            else -> {
                st.copy(enemyPos = cur)
            }
        }

        if (cur == st.playerPos) {
            return st.copy(result = GameResult.PlayerLost)
        }
    }

    return st.copy(turn = Turn.PLAYER)
}

// --- Helper ---

fun stepTowardPlayer(from: Pos, target: Pos): Pos? {
    val dr = target.r - from.r
    val dc = target.c - from.c
    return when {
        abs(dr) > abs(dc) -> Pos(from.r + dr.sign, from.c)
        dc != 0 -> Pos(from.r, from.c + dc.sign)
        else -> null
    }
}

fun GameState.canMove(from: Pos, to: Pos): Boolean { // need to add canMove()
    if (!inBounds(to)) return false
    if ((from to to) in walls || (to to from) in walls) return false
    return true
}