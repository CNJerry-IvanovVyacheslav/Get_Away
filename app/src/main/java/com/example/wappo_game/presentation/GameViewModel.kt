package com.example.wappo_game.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.wappo_game.data.DataStoreManager
import com.example.wappo_game.data.InMemoryGameRepository
import com.example.wappo_game.data.LevelRepository
import com.example.wappo_game.domain.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GameViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = InMemoryGameRepository(LevelRepository.levels.first())
    private val dataStore = DataStoreManager(app)

    val state: StateFlow<GameState> = repo.state
    val savedMaps: StateFlow<List<GameState>> =
        dataStore.loadMaps().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _lastMapState = MutableStateFlow<GameState?>(null)
    val lastMapState: StateFlow<GameState?> = _lastMapState

    private val _unlockedLevels = MutableStateFlow(1)
    val unlockedLevels: StateFlow<Int> = _unlockedLevels

    private var _currentLevelIndex: Int = 0
    val currentLevelIndex: Int get() = _currentLevelIndex

    private val actionChannel = Channel<suspend () -> Unit>(Channel.UNLIMITED)
    private lateinit var currentMap: GameState

    private val _nextLevel = MutableStateFlow<GameState?>(null)
    val nextLevel: StateFlow<GameState?> = _nextLevel

    init {
        viewModelScope.launch {
            val savedProgress = dataStore.loadUnlockedLevels().firstOrNull() ?: 1
            _unlockedLevels.value = savedProgress

            val lastName = dataStore.loadLastMapName().firstOrNull()
            val maps = dataStore.loadMaps().firstOrNull() ?: emptyList()

            val lastIndex = LevelRepository.levels.indexOfFirst { it.name == lastName }

            val chosenIndex = when {
                lastIndex in 0 until _unlockedLevels.value -> lastIndex
                else -> _unlockedLevels.value - 1
            }

            val chosenMap = LevelRepository.levels.getOrNull(chosenIndex)
                ?: LevelRepository.levels.first()

            currentMap = chosenMap
            loadCustomMap(chosenMap, saveLast = false)
            _lastMapState.value = chosenMap
        }

        viewModelScope.launch {
            for (action in actionChannel) action()
        }
    }

    private fun enqueue(action: suspend () -> Unit) {
        viewModelScope.launch { actionChannel.send(action) }
    }

    private fun tryMovePlayer(to: Pos) {
        enqueue {
            val cur = repo.getState()
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

            if (afterPlayer.result is GameResult.PlayerWon) {
                unlockNextLevel(afterPlayer.name)
            }
        }
    }

    fun moveUp() = tryMovePlayer(Pos(state.value.playerPos.r - 1, state.value.playerPos.c))
    fun moveDown() = tryMovePlayer(Pos(state.value.playerPos.r + 1, state.value.playerPos.c))
    fun moveLeft() = tryMovePlayer(Pos(state.value.playerPos.r, state.value.playerPos.c - 1))
    fun moveRight() = tryMovePlayer(Pos(state.value.playerPos.r, state.value.playerPos.c + 1))

    fun loadCustomMap(level: GameState, saveLast: Boolean = true) {
        val resetState = level.copy(
            playerPos = level.initialPlayerPos,
            enemyPos = level.initialEnemyPos,
            tiles = level.tiles.map { it.copy() },
            playerMoves = 0,
            result = GameResult.Ongoing,
            enemyFrozenTurns = 0,
            turn = Turn.PLAYER
        )
        currentMap = level
        repo.setState(resetState)
        _lastMapState.value = level
        _nextLevel.value = getNextLevel(level)

        _currentLevelIndex = LevelRepository.levels.indexOfFirst { it.name == level.name }

        if (saveLast) {
            viewModelScope.launch { dataStore.saveLastMapName(level.name) }
        }
    }

    private fun getNextLevel(level: GameState): GameState? {
        val currentIndex = LevelRepository.levels.indexOfFirst { it.name == level.name }
        return if (currentIndex != -1 && currentIndex + 1 < LevelRepository.levels.size) {
            LevelRepository.levels[currentIndex + 1]
        } else null
    }

    private fun unlockNextLevel(currentLevelName: String) {
        val currentIndex = LevelRepository.levels.indexOfFirst { it.name == currentLevelName }
        if (currentIndex != -1 && currentIndex + 1 < LevelRepository.levels.size) {
            val nextIndex = currentIndex + 1
            if (_unlockedLevels.value <= nextIndex) {
                _unlockedLevels.value = nextIndex + 1
                viewModelScope.launch { dataStore.saveUnlockedLevels(_unlockedLevels.value) }
            }
        }
        _nextLevel.value = getNextLevel(LevelRepository.levels[currentIndex])
    }

    fun resetGame() = loadCustomMap(currentMap)

    fun saveCustomMap(level: GameState) = viewModelScope.launch { dataStore.saveOrUpdateMap(level) }
    fun deleteMap(name: String) = viewModelScope.launch { dataStore.deleteMap(name) }
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
                    curState = curState.copy(enemyFrozenTurns = 4, turn = Turn.PLAYER)
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