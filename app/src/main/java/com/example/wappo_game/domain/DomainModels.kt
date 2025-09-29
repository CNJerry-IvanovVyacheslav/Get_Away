package com.example.wappo_game.domain

import kotlin.math.abs

data class Pos(val r: Int, val c: Int) {
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
    val enemyPositions: List<Pos> = listOf(Pos(rows - 1, 0)),
    val walls: Set<Pair<Pos, Pos>> = emptySet(),
    val enemyFrozenTurns: List<Int> = enemyPositions.map { 0 },
    val turn: Turn = Turn.PLAYER,
    @Transient val result: GameResult = GameResult.Ongoing,
    val playerMoves: Int = 0,
    val name: String = "Custom Map",
    val initialPlayerPos: Pos = playerPos,
    val initialEnemyPositions: List<Pos> = enemyPositions
) {
    fun tileAt(p: Pos): Tile? = tiles.find { it.pos == p }
    fun inBounds(p: Pos) = p.r in 0 until rows && p.c in 0 until cols
    fun isBlocked(a: Pos, b: Pos): Boolean = (a to b) in walls || (b to a) in walls
}

fun createLevel(
    name: String,
    rows: Int = 6,
    cols: Int = 6,
    traps: List<Pos> = emptyList(),
    exit: Pos = Pos(rows - 1, cols - 1),
    walls: Set<Pair<Pos, Pos>> = emptySet(),
    playerPos: Pos = Pos(0, 0),
    enemyPositions: List<Pos> = listOf(Pos(rows - 1, 0))
): GameState {
    val tiles = List(rows * cols) { idx ->
        val r = idx / cols
        val c = idx % cols
        Tile(Pos(r, c), TileType.EMPTY)
    }.toMutableList()

    traps.forEach { pos ->
        val index = pos.r * cols + pos.c
        tiles[index] = Tile(pos, TileType.TRAP)
    }

    val exitIndex = exit.r * cols + exit.c
    tiles[exitIndex] = Tile(exit, TileType.EXIT)

    return GameState(
        rows = rows,
        cols = cols,
        tiles = tiles,
        playerPos = playerPos,
        enemyPositions = enemyPositions,
        walls = walls,
        name = name,
        initialPlayerPos = playerPos,
        initialEnemyPositions = enemyPositions
    )
}
