package com.example.wappo_game.domain


fun movePlayer(state: GameState, to: Pos): GameState {
    if (state.result != GameResult.Ongoing) return state

    if (!state.inBounds(to)) return state
    if (state.isBlocked(state.playerPos, to)) return state
    if (state.playerPos.manhattan(to) != 1) return state

    var newState = state.copy(playerPos = to)

    if (newState.playerPos == newState.enemyPos) {
        return newState.copy(result = GameResult.PlayerLost)
    }

    if (newState.tileAt(to)?.type == TileType.TRAP) {
        return newState.copy(result = GameResult.PlayerLost)
    }

    if (newState.tileAt(to)?.type == TileType.EXIT) {
        return newState.copy(result = GameResult.PlayerWon)
    }

    val frozen = (newState.enemyFrozenTurns - 1).coerceAtLeast(0)

    return newState.copy(
        enemyFrozenTurns = frozen,
        turn = if (frozen > 0) Turn.PLAYER else Turn.ENEMY
    )
}

/**
 * Enemy's move: takes up to 2 steps towards the player.
 * The enemy does NOT go around the walls — when it collides with the wall, it stops.
 * If an enemy steps on a TRAP, it gets enemyFrozenTurns = 3.
 * If the enemy is frozen (enemyFrozenTurns > 0) — lowers the counter and returns the turn to the player.
 */
fun stepTowardPlayerWithPriority(state: GameState, from: Pos): Pos {
    val player = state.playerPos

    return when {
        // horizontal priority
        player.c < from.c -> Pos(from.r, from.c - 1)
        player.c > from.c -> Pos(from.r, from.c + 1)
        // if it matches horizontally → go vertically
        player.r < from.r -> Pos(from.r - 1, from.c)
        player.r > from.r -> Pos(from.r + 1, from.c)
        else -> from
    }
}

fun enemyPath(state: GameState): List<Pos> {
    if (state.enemyFrozenTurns > 0 || state.result != GameResult.Ongoing) return emptyList()

    var cur = state.enemyPos
    val steps = mutableListOf<Pos>()

    repeat(2) {
        val next = stepTowardPlayerWithPriority(state, cur)
        if (next == cur || !state.inBounds(next) || state.isBlocked(cur, next)) return steps

        steps.add(next)
        cur = next

        if (state.tileAt(cur)?.type == TileType.TRAP) return steps
    }

    return steps
}