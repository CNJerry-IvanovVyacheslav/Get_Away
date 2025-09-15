package com.example.wappo_game.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wappo_game.data.InMemoryGameRepository
import com.example.wappo_game.domain.GameResult
import com.example.wappo_game.domain.GameState
import com.example.wappo_game.domain.Pos
import com.example.wappo_game.domain.Turn
import com.example.wappo_game.domain.createDefaultGameState
import com.example.wappo_game.domain.moveEnemy
import com.example.wappo_game.domain.movePlayer
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val repo = InMemoryGameRepository(createDefaultGameState())

    val state: StateFlow<GameState> = repo.state

    private fun tryMovePlayer(to: Pos) {
        val cur = repo.state.value
        val afterPlayer = movePlayer(cur, to)
        repo.setState(afterPlayer)

        if (afterPlayer.turn == Turn.ENEMY && afterPlayer.result is GameResult.Ongoing) {
            viewModelScope.launch {
                val afterEnemy = moveEnemy(afterPlayer)
                repo.setState(afterEnemy)
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

    fun resetGame() {
        repo.setState(createDefaultGameState())
    }
}