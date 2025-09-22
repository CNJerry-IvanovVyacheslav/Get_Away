package com.example.wappo_game.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wappo_game.data.DataStoreManager
import com.example.wappo_game.data.InMemoryGameRepository
import com.example.wappo_game.domain.GameResult
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.domain.Pos
import com.example.wappo_game.domain.TileType
import com.example.wappo_game.domain.Turn
import com.example.wappo_game.domain.createDefaultGameState
import com.example.wappo_game.domain.enemyPath
import com.example.wappo_game.domain.movePlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GameViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = InMemoryGameRepository(createDefaultGameState())
    private val dataStore = DataStoreManager(app)

    val state: StateFlow<GameState> = repo.state

    val savedMaps: StateFlow<List<GameState>> =
        dataStore.loadMaps().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private var currentMap: GameState

    init {
        val defaultMap = createDefaultGameState()
        currentMap = defaultMap
        loadCustomMap(defaultMap)
    }

    private fun tryMovePlayer(to: Pos) {
        val cur = repo.state.value

        if (cur.result !is GameResult.Ongoing) return

        val afterPlayer = movePlayer(cur, to).copy(
            playerMoves = cur.playerMoves + 1
        )

        repo.setState(afterPlayer)

        if (afterPlayer.result is GameResult.PlayerLost &&
            afterPlayer.tileAt(afterPlayer.playerPos)?.type == TileType.TRAP
        ) {
            viewModelScope.launch {
                delay(350)
                val newTiles = afterPlayer.tiles.map {
                    if (it.pos == afterPlayer.playerPos) it.copy(type = TileType.EMPTY) else it
                }
                repo.setState(afterPlayer.copy(tiles = newTiles))
            }
            return
        }

        if (afterPlayer.turn == Turn.ENEMY && afterPlayer.result is GameResult.Ongoing) {
            viewModelScope.launch {
                moveEnemyStepByStep(afterPlayer)
            }
        }
    }

    fun moveUp() {
        val cur = state.value
        tryMovePlayer(Pos(cur.playerPos.r - 1, cur.playerPos.c))
    }

    fun moveDown() {
        val cur = state.value
        tryMovePlayer(Pos(cur.playerPos.r + 1, cur.playerPos.c))
    }

    fun moveLeft() {
        val cur = state.value
        tryMovePlayer(Pos(cur.playerPos.r, cur.playerPos.c - 1))
    }

    fun moveRight() {
        val cur = state.value
        tryMovePlayer(Pos(cur.playerPos.r, cur.playerPos.c + 1))
    }


    fun loadCustomMap(state: GameState) {
        val resetState = state.copy(
            playerPos = state.initialPlayerPos,
            enemyPos = state.initialEnemyPos,
            tiles = state.tiles.map { it.copy() }.toMutableList(),
            playerMoves = 0,
            result = GameResult.Ongoing,
            enemyFrozenTurns = 0,
            turn = Turn.PLAYER
        )
        currentMap = state
        repo.setState(resetState)
        Log.d("GameViewModel", "Loaded map: $resetState")
    }

    fun saveCustomMap(state: GameState) {
        viewModelScope.launch { dataStore.saveMap(state) }
    }

    fun deleteMap(name: String) {
        viewModelScope.launch { dataStore.deleteMap(name) }
    }

    fun resetGame() {
        currentMap.let { map ->
            loadCustomMap(map)
        }
    }

    fun clearAllMaps() {
        viewModelScope.launch { dataStore.clearMaps() }
    }

    fun moveEnemyStepByStep(afterPlayer: GameState) {
        viewModelScope.launch {
            if (afterPlayer.result != GameResult.Ongoing) return@launch

            val path = enemyPath(afterPlayer)
            var curState = afterPlayer

            for ((i, step) in path.withIndex()) {
                delay(100)

                if (curState.result != GameResult.Ongoing) return@launch

                curState = curState.copy(enemyPos = step)

                if (curState.tileAt(step)?.type == TileType.TRAP) {
                    curState = curState.copy(
                        enemyPos = step,
                        enemyFrozenTurns = 3,
                        turn = Turn.PLAYER
                    )
                    repo.setState(curState)

                    viewModelScope.launch {
                        delay(100)
                        val newTiles = curState.tiles.map {
                            if (it.pos == step) it.copy(type = TileType.EMPTY) else it
                        }
                        repo.setState(curState.copy(tiles = newTiles))
                    }

                    return@launch
                }

                if (step == curState.playerPos) {
                    curState = curState.copy(result = GameResult.PlayerLost)
                    repo.setState(curState)
                    return@launch
                }

                repo.setState(curState)

                if (i == path.lastIndex) {
                    curState = curState.copy(turn = Turn.PLAYER)
                    repo.setState(curState)
                }
            }
        }
    }
}
