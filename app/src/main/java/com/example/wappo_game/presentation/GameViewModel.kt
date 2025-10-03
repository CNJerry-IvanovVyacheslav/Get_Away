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
            val lastIndex = LevelRepository.levels.indexOfFirst { it.name == lastName }
            val chosenIndex = when {
                lastIndex in 0 until _unlockedLevels.value -> lastIndex
                else -> _unlockedLevels.value - 1
            }

            val chosenMap = LevelRepository.levels.getOrNull(chosenIndex) ?: LevelRepository.levels.first()
            currentMap = chosenMap
            loadCustomMap(chosenMap, saveLast = false)
            _lastMapState.value = chosenMap
        }

        viewModelScope.launch { for (action in actionChannel) action() }
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
                moveEnemiesStepByStep(afterPlayer)
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
            enemyPositions = level.initialEnemyPositions,
            tiles = level.tiles.map { it.copy() },
            playerMoves = 0,
            result = GameResult.Ongoing,
            enemyFrozenTurns = level.enemyPositions.map { 0 },
            turn = Turn.PLAYER
        )
        currentMap = level
        repo.setState(resetState)
        _lastMapState.value = level
        _nextLevel.value = getNextLevel(level)

        _currentLevelIndex = LevelRepository.levels.indexOfFirst { it.name == level.name }

        if (saveLast) viewModelScope.launch { dataStore.saveLastMapName(level.name) }
    }

    private fun getNextLevel(level: GameState): GameState? {
        val currentIndex = LevelRepository.levels.indexOfFirst { it.name == level.name }
        return if (currentIndex != -1 && currentIndex + 1 < LevelRepository.levels.size)
            LevelRepository.levels[currentIndex + 1]
        else null
    }

    private fun unlockNextLevel(currentLevelName: String) {
        val currentIndex = LevelRepository.levels.indexOfFirst { it.name == currentLevelName }
        if (currentIndex == -1) return

        if (currentIndex + 1 < LevelRepository.levels.size) {
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

    private fun moveEnemiesStepByStep(afterPlayer: GameState) {
        enqueue {
            if (afterPlayer.result != GameResult.Ongoing) return@enqueue

            var curState = afterPlayer

            enemyPaths(curState).forEachIndexed { idx, path ->
                val enemyPositions = curState.enemyPositions.toMutableList()
                val frozenTurns = curState.enemyFrozenTurns.toMutableList()
                var stopped = false

                path.forEach { step ->
                    if (stopped) return@forEach

                    delay(100)

                    if (frozenTurns[idx] > 0) return@forEach

                    enemyPositions[idx] = step

                    if (curState.tileAt(step)?.type == TileType.TRAP) {
                        frozenTurns[idx] = 4
                        stopped = true
                        return@forEach
                    }

                    if (step == curState.playerPos) {
                        curState = curState.copy(
                            enemyPositions = enemyPositions.toList(),
                            enemyFrozenTurns = frozenTurns.toList(),
                            result = GameResult.PlayerLost
                        )
                        repo.setState(curState)
                        return@enqueue
                    }

                    curState = curState.copy(
                        enemyPositions = enemyPositions.toList(),
                        enemyFrozenTurns = frozenTurns.toList()
                    )
                    repo.setState(curState)
                }

                curState = curState.copy(
                    enemyPositions = enemyPositions.toList(),
                    enemyFrozenTurns = frozenTurns.toList()
                )
            }

            curState = curState.copy(turn = Turn.PLAYER)
            repo.setState(curState)
        }
    }

}
