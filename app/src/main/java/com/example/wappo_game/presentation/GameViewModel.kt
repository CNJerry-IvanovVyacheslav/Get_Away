package com.example.wappo_game.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wappo_game.data.DataStoreManager
import com.example.wappo_game.data.InMemoryGameRepository
import com.example.wappo_game.domain.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel

class GameViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = InMemoryGameRepository(createDefaultGameState())
    private val dataStore = DataStoreManager(app)

    val state: StateFlow<GameState> = repo.state
    val savedMaps: StateFlow<List<GameState>> =
        dataStore.loadMaps().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private lateinit var currentMap: GameState
    private val _lastMapState = MutableStateFlow<GameState?>(null)
    val lastMapState: StateFlow<GameState?> = _lastMapState

    private val actionChannel = Channel<suspend () -> Unit>(capacity = Channel.UNLIMITED)

    init {
        viewModelScope.launch {
            for (action in actionChannel) {
                action()
            }
        }

        viewModelScope.launch {
            try {
                val lastName = dataStore.loadLastMapName().firstOrNull()
                val maps = dataStore.loadMaps().firstOrNull() ?: emptyList()
                val chosenMap = maps.find { it.name == lastName } ?: createDefaultGameState()
                currentMap = chosenMap
                loadCustomMap(chosenMap, saveLast = false)
                _lastMapState.value = chosenMap
            } catch (t: Throwable) {
                val defaultMap = createDefaultGameState()
                currentMap = defaultMap
                loadCustomMap(defaultMap, saveLast = false)
                _lastMapState.value = defaultMap
            }
        }
    }

    private fun enqueue(action: suspend () -> Unit) {
        viewModelScope.launch { actionChannel.send(action) }
    }

    private fun tryMovePlayer(to: Pos) {
        enqueue {
            val cur = repo.state.value
            if (cur.result !is GameResult.Ongoing) return@enqueue

            val afterPlayer = movePlayer(cur, to).copy(playerMoves = cur.playerMoves + 1)
            repo.setState(afterPlayer)

            if (afterPlayer.result is GameResult.PlayerLost &&
                afterPlayer.tileAt(afterPlayer.playerPos)?.type == TileType.TRAP
            ) {
                delay(350)
                val newTiles = afterPlayer.tiles.map {
                    if (it.pos == afterPlayer.playerPos) it.copy(type = TileType.EMPTY) else it
                }
                repo.setState(afterPlayer.copy(tiles = newTiles))
                return@enqueue
            }

            if (afterPlayer.turn == Turn.ENEMY && afterPlayer.result is GameResult.Ongoing) {
                moveEnemyStepByStep(afterPlayer)
            }
        }
    }

    fun moveUp() = tryMovePlayer(Pos(state.value.playerPos.r - 1, state.value.playerPos.c))
    fun moveDown() = tryMovePlayer(Pos(state.value.playerPos.r + 1, state.value.playerPos.c))
    fun moveLeft() = tryMovePlayer(Pos(state.value.playerPos.r, state.value.playerPos.c - 1))
    fun moveRight() = tryMovePlayer(Pos(state.value.playerPos.r, state.value.playerPos.c + 1))

    fun loadCustomMap(state: GameState, saveLast: Boolean = true) {
        val resetState = state.copy(
            playerPos = state.initialPlayerPos,
            enemyPos = state.initialEnemyPos,
            tiles = state.tiles.map { it.copy() },
            playerMoves = 0,
            result = GameResult.Ongoing,
            enemyFrozenTurns = 0,
            turn = Turn.PLAYER
        )
        currentMap = state
        repo.setState(resetState)

        _lastMapState.value = state
        if (saveLast) {
            viewModelScope.launch { dataStore.saveLastMapName(state.name) }
        }
    }

    fun saveCustomMap(state: GameState) = viewModelScope.launch {
        dataStore.saveOrUpdateMap(state)
    }
    fun deleteMap(name: String) = viewModelScope.launch { dataStore.deleteMap(name) }
    fun resetGame() = loadCustomMap(currentMap)
    fun clearAllMaps() = viewModelScope.launch { dataStore.clearMaps() }

    private fun moveEnemyStepByStep(afterPlayer: GameState) {
        enqueue {
            if (afterPlayer.result != GameResult.Ongoing) return@enqueue
            val path = enemyPath(afterPlayer)
            var curState = afterPlayer

            for ((i, step) in path.withIndex()) {
                delay(100)
                if (curState.result != GameResult.Ongoing) return@enqueue

                curState = curState.copy(enemyPos = step)

                if (curState.tileAt(step)?.type == TileType.TRAP) {
                    curState = curState.copy(enemyFrozenTurns = 3, turn = Turn.PLAYER)
                    repo.setState(curState)

                    enqueue {
                        delay(100)
                        val newTiles = curState.tiles.map {
                            if (it.pos == step) it.copy(type = TileType.EMPTY) else it
                        }
                        repo.setState(curState.copy(tiles = newTiles))
                    }
                    return@enqueue
                }

                if (step == curState.playerPos) {
                    curState = curState.copy(result = GameResult.PlayerLost)
                    repo.setState(curState)
                    return@enqueue
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