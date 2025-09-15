package com.example.wappo_game.domain

import kotlin.math.abs

data class Pos(val r:   Int, val c: Int) {
    fun manhattan(other: Pos) = abs(r - other.r) + abs(c - other.c)
}

enum class TileType { EMPTY, TRAP, EXIT }

data class Tile(val pos: Pos, val type: TileType = TileType.EMPTY)

enum class Turn { PLAYER, ENEMY }

sealed class GameResult {
    object Ongoing : GameResult()
    object PlayerWon : GameResult()
    object PlayerLost : GameResult()
}

data class GameState(
    val rows: Int = 6,
    val cols: Int = 6,
    val tiles: List<Tile>,
    val playerPos: Pos,
    val enemyPos: Pos,
    val walls: Set<Pair<Pos, Pos>> = emptySet(),
    val enemyFrozenTurns: Int = 0,
    val turn: Turn = Turn.PLAYER,
    val result: GameResult = GameResult.Ongoing
) {
    fun tileAt(p: Pos): Tile? = tiles.find { it.pos == p }
    fun inBounds(p: Pos) = p.r in 0 until rows && p.c in 0 until cols
    fun isBlocked(a: Pos, b: Pos): Boolean = (a to b) in walls || (b to a) in walls
}

fun createDefaultGameState(): GameState {
    val rows = 6
    val cols = 6

    // all the cells are empty
    val tiles = List(rows * cols) { idx ->
        val r = idx / cols
        val c = idx % cols
        Tile(Pos(r, c), TileType.EMPTY)
    }.toMutableList()

    // the exit is in the lower right corner
    tiles[rows * cols - 1] = Tile(Pos(rows - 1, cols - 1), TileType.EXIT)

    // Pair of traps
    tiles[10] = Tile(Pos(1, 4), TileType.TRAP)
    tiles[20] = Tile(Pos(3, 2), TileType.TRAP)

    val walls = setOf(
        Pos(0, 0) to Pos(0, 1),
        Pos(2, 2) to Pos(3, 2),
        Pos(4, 4) to Pos(4, 5)
    )

    return GameState(
        rows = rows,
        cols = cols,
        tiles = tiles,
        playerPos = Pos(0, 0),
        enemyPos = Pos(0, 5),
        walls = walls
    )
}
