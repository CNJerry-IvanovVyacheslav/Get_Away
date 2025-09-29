package com.example.wappo_game.data

import com.example.wappo_game.domain.GameState
import com.example.wappo_game.domain.Pos
import com.example.wappo_game.domain.createLevel

object LevelRepository {
    val levels: List<GameState> = listOf(
        createLevel(
            name = "Level 1",
            traps = listOf(Pos(2, 2)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(0, 5))
        ),
        createLevel(
            name = "Level 2",
            traps = listOf(Pos(2, 2), Pos(4, 3)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(2, 5))
        ),
        createLevel(
            name = "Level 3",
            traps = listOf(Pos(1, 4), Pos(3, 2)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(5, 0), Pos(0, 5)),
            walls = setOf(Pos(2, 2) to Pos(3, 2), Pos(0, 4) to Pos(0, 5), Pos(4, 4) to Pos(4, 5))
        ),
        createLevel(
            name = "Level 4",
            traps = listOf(Pos(2, 1), Pos(2, 4), Pos(4, 3)),
            exit = Pos(0, 5),
            playerPos = Pos(5, 0),
            enemyPositions = listOf(Pos(3, 5)),
            walls = setOf(Pos(1, 1) to Pos(1, 2), Pos(3, 3) to Pos(4, 3))
        ),
        createLevel(
            name = "Level 5",
            traps = listOf(Pos(1, 0), Pos(2, 2), Pos(3, 4), Pos(5, 1)),
            exit = Pos(0, 5),
            playerPos = Pos(5, 0),
            enemyPositions = listOf(Pos(0, 0), Pos(5, 5)),
            walls = setOf(Pos(0, 3) to Pos(1, 3), Pos(2, 1) to Pos(3, 1), Pos(4, 4) to Pos(5, 4))
        ),
        createLevel(
            name = "Level 6",
            traps = listOf(Pos(1, 1), Pos(3, 4)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(0, 5))
        ),
        createLevel(
            name = "Level 7",
            traps = listOf(Pos(2, 2), Pos(3, 3)),
            exit = Pos(5, 0),
            playerPos = Pos(0, 5),
            enemyPositions = listOf(Pos(5, 5), Pos(0, 0)),
            walls = setOf(Pos(1, 4) to Pos(2, 4), Pos(3, 1) to Pos(4, 1))
        ),
        createLevel(
            name = "Level 8",
            traps = listOf(Pos(1, 3), Pos(4, 2)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(5, 0))
        ),
        createLevel(
            name = "Level 9",
            traps = listOf(Pos(1, 2), Pos(2, 5), Pos(4, 3)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(3, 5), Pos(5, 0))
        ),
        createLevel(
            name = "Level 10",
            traps = listOf(Pos(2, 1), Pos(3, 2), Pos(1, 4)),
            exit = Pos(5, 0),
            playerPos = Pos(0, 5),
            enemyPositions = listOf(Pos(5, 5))
        ),
        createLevel(
            name = "Level 11",
            traps = listOf(Pos(0, 3), Pos(2, 4), Pos(4, 1)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(5, 0)),
            walls = setOf(Pos(1, 1) to Pos(2, 1), Pos(3, 3) to Pos(4, 3))
        ),
        createLevel(
            name = "Level 12",
            traps = listOf(Pos(1, 1), Pos(3, 2)),
            exit = Pos(0, 5),
            playerPos = Pos(5, 0),
            enemyPositions = listOf(Pos(0, 0)),
            walls = setOf(Pos(2, 3) to Pos(3, 3), Pos(4, 4) to Pos(5, 4))
        ),
        createLevel(
            name = "Level 13",
            traps = listOf(Pos(2, 2), Pos(3, 4), Pos(1, 5)),
            exit = Pos(5, 0),
            playerPos = Pos(0, 5),
            enemyPositions = listOf(Pos(5, 5)),
            walls = setOf(Pos(2, 1) to Pos(3, 1), Pos(1, 3) to Pos(2, 3))
        ),
        createLevel(
            name = "Level 14",
            traps = listOf(Pos(0, 2), Pos(4, 3)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(0, 5)),
            walls = setOf(Pos(1, 4) to Pos(2, 4), Pos(3, 2) to Pos(4, 2))
        ),
        createLevel(
            name = "Level 15",
            traps = listOf(Pos(2, 3), Pos(3, 1)),
            exit = Pos(5, 0),
            playerPos = Pos(0, 5),
            enemyPositions = listOf(Pos(5, 5)),
            walls = setOf(Pos(1, 1) to Pos(2, 1), Pos(4, 3) to Pos(5, 3))
        ),
        createLevel(
            name = "Level 16",
            traps = listOf(Pos(0, 4), Pos(3, 2)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(5, 0)),
            walls = setOf(Pos(2, 2) to Pos(3, 2), Pos(4, 1) to Pos(5, 1))
        ),
        createLevel(
            name = "Level 17",
            traps = listOf(Pos(1, 3), Pos(2, 5), Pos(4, 2)),
            exit = Pos(0, 5),
            playerPos = Pos(5, 0),
            enemyPositions = listOf(Pos(0, 0)),
            walls = setOf(Pos(1, 1) to Pos(1, 2), Pos(3, 3) to Pos(4, 3))
        ),
        createLevel(
            name = "Level 18",
            traps = listOf(Pos(2, 2), Pos(4, 4)),
            exit = Pos(5, 0),
            playerPos = Pos(0, 5),
            enemyPositions = listOf(Pos(5, 5)),
            walls = setOf(Pos(1, 2) to Pos(2, 2), Pos(3, 1) to Pos(4, 1))
        ),
        createLevel(
            name = "Level 19",
            traps = listOf(Pos(0, 3), Pos(2, 1), Pos(4, 2)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(5, 0)),
            walls = setOf(Pos(1, 3) to Pos(2, 3), Pos(3, 4) to Pos(4, 4))
        ),
        createLevel(
            name = "Level 20",
            traps = listOf(Pos(1, 2), Pos(3, 3)),
            exit = Pos(0, 5),
            playerPos = Pos(5, 0),
            enemyPositions = listOf(Pos(0, 0)),
            walls = setOf(Pos(2, 2) to Pos(3, 2), Pos(4, 3) to Pos(5, 3))
        ),
        createLevel(
            name = "Level 21",
            traps = listOf(Pos(2, 4), Pos(3, 2), Pos(1, 1)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(5, 0)),
            walls = setOf(Pos(1, 3) to Pos(2, 3), Pos(3, 4) to Pos(4, 4))
        ),
        createLevel(
            name = "Level 22",
            traps = listOf(Pos(0, 2), Pos(4, 1)),
            exit = Pos(5, 0),
            playerPos = Pos(0, 5),
            enemyPositions = listOf(Pos(5, 5)),
            walls = setOf(Pos(2, 2) to Pos(3, 2), Pos(1, 4) to Pos(2, 4))
        ),
        createLevel(
            name = "Level 23",
            traps = listOf(Pos(1, 3), Pos(3, 4)),
            exit = Pos(5, 5),
            playerPos = Pos(0, 0),
            enemyPositions = listOf(Pos(5, 0)),
            walls = setOf(Pos(2, 1) to Pos(3, 1), Pos(4, 3) to Pos(5, 3))
        ),
        createLevel(
            name = "Level 24",
            traps = listOf(Pos(2, 2), Pos(4, 4), Pos(0, 3)),
            exit = Pos(0, 5),
            playerPos = Pos(5, 0),
            enemyPositions = listOf(Pos(0, 0)),
            walls = setOf(Pos(1, 1) to Pos(2, 1), Pos(3, 3) to Pos(4, 3))
        ),
        createLevel(
            name = "Level 25",
            traps = listOf(Pos(1, 4), Pos(3, 2)),
            exit = Pos(5, 0),
            playerPos = Pos(0, 5),
            enemyPositions = listOf(Pos(5, 5)),
            walls = setOf(Pos(2, 2) to Pos(3, 2), Pos(4, 1) to Pos(5, 1))
        )
    )
}
