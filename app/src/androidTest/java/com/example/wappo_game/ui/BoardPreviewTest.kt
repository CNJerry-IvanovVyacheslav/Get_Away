package com.example.wappo_game.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import com.example.wappo_game.domain.*
import org.junit.Rule
import org.junit.Test


class BoardPreviewTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createTestState(): GameState {
        return createLevel(
            name = "Preview Test",
            rows = 4,
            cols = 5,
            playerPos = Pos(1, 1),
            enemyPositions = listOf(Pos(2, 2)),
            traps = listOf(Pos(0, 0)),
            exit = Pos(3, 4),
            walls = setOf(Pos(0, 1) to Pos(0, 2))
        )
    }

    @Test
    fun board_preview_is_displayed_with_correct_tag() {
        val state = createTestState()
        composeTestRule.setContent {
            BoardPreview(state = state, sizeDp = 200.dp)
        }

        composeTestRule.onNodeWithTag("BoardPreview").assertExists()
    }

    @Test
    fun board_preview_shows_special_tiles_player_and_enemy() {
        val state = createTestState()
        composeTestRule.setContent {
            BoardPreview(state = state, sizeDp = 200.dp)
        }

        composeTestRule.onNodeWithContentDescription("Player").assertExists()
        composeTestRule.onNodeWithContentDescription("Enemy").assertExists()
        composeTestRule.onNodeWithContentDescription("Trap").assertExists()
        composeTestRule.onNodeWithContentDescription("Exit").assertExists()
    }
}