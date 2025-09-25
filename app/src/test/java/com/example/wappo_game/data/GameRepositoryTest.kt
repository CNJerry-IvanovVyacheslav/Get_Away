package com.example.wappo_game.data

import com.example.wappo_game.domain.GameState
import com.example.wappo_game.domain.createDefaultGameState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GameRepositoryTest {

    private lateinit var repository: InMemoryGameRepository
    private lateinit var initialState: GameState

    @Before
    fun setup() {
        initialState = createDefaultGameState()
        repository = InMemoryGameRepository(initialState)
    }

    @Test
    fun `initial state is correct`() = runBlocking {
        assertThat(repository.getState()).isEqualTo(initialState)
        assertThat(repository.state.first()).isEqualTo(initialState)
    }

    @Test
    fun `setState updates state correctly`() = runBlocking {
        val newState = initialState.copy(playerMoves = 1)
        repository.setState(newState)

        assertThat(repository.getState()).isEqualTo(newState)
        assertThat(repository.state.first()).isEqualTo(newState)
    }
}