package com.example.wappo_game.domain

fun movePlayer(state: GameState, to: Pos): GameState {
    if (state.result != GameResult.Ongoing) return state
    if (!state.inBounds(to)) return state
    if (state.isBlocked(state.playerPos, to)) return state
    if (state.playerPos.manhattan(to) != 1) return state

    val newState = state.copy(playerPos = to)

    if (newState.enemyPositions.contains(to)) return newState.copy(result = GameResult.PlayerLost)
    if (newState.tileAt(to)?.type == TileType.TRAP) return newState.copy(result = GameResult.PlayerLost)
    if (newState.tileAt(to)?.type == TileType.EXIT) return newState.copy(result = GameResult.PlayerWon)

    val newFrozen = newState.enemyFrozenTurns.map { (it - 1).coerceAtLeast(0) }

    return newState.copy(
        enemyFrozenTurns = newFrozen,
        turn = Turn.ENEMY
    )
}

fun stepTowardPlayerWithPriority(state: GameState, from: Pos): Pos {
    val player = state.playerPos

    val horizStep = when {
        player.c < from.c -> Pos(from.r, from.c - 1)
        player.c > from.c -> Pos(from.r, from.c + 1)
        else -> null
    }

    if (horizStep != null && state.inBounds(horizStep) && !state.isBlocked(
            from,
            horizStep
        )
    ) return horizStep

    val vertStep = when {
        player.r < from.r -> Pos(from.r - 1, from.c)
        player.r > from.r -> Pos(from.r + 1, from.c)
        else -> null
    }

    if (vertStep != null && state.inBounds(vertStep) && !state.isBlocked(
            from,
            vertStep
        )
    ) return vertStep

    return from
}

fun enemyPaths(state: GameState): List<List<Pos>> {
    return state.enemyPositions.mapIndexed { idx, pos ->
        if (state.enemyFrozenTurns.getOrElse(idx) { 0 } > 0 || state.result != GameResult.Ongoing) {
            emptyList()
        } else {
            val steps = mutableListOf<Pos>()
            var cur = pos
            repeat(2) {
                val next = stepTowardPlayerWithPriority(
                    state.copy(enemyPositions = state.enemyPositions),
                    cur
                )
                if (next == cur || !state.inBounds(next) || state.isBlocked(
                        cur,
                        next
                    )
                ) return@repeat
                steps.add(next)
                cur = next
            }
            steps
        }
    }
}
