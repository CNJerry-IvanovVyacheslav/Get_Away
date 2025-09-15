package com.example.wappo_game.data

import com.example.wappo_game.domain.GameState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class InMemoryGameRepository(initial: GameState) {
    private val _state = MutableStateFlow(initial)
    val state: StateFlow<GameState> = _state

    fun setState(next: GameState) { _state.value = next }
}