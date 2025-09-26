package com.example.wappo_game.presentation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.wappo_game.domain.createDefaultGameState
import org.junit.Rule
import org.junit.Test

// It runs on the Medium Phone API 36.0 emulator.
class MenuScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun menu_screen_displays_title_and_buttons() {
        composeTestRule.setContent {
            MenuScreen(
                onPlayClick = {},
                onCreateMapClick = {},
                onMapsClick = {},
                onExitClick = {},
                previewState = null
            )
        }

        composeTestRule.onNodeWithText("Wappo-like Game").assertIsDisplayed()

        composeTestRule.onNodeWithText("Play").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create Map").assertIsDisplayed()
        composeTestRule.onNodeWithText("Saved Maps").assertIsDisplayed()
        composeTestRule.onNodeWithText("Exit").assertIsDisplayed()
    }

    @Test
    fun menu_screen_displays_previewState_if_provided() {
        val previewState = createDefaultGameState()

        composeTestRule.setContent {
            MenuScreen(
                onPlayClick = {},
                onCreateMapClick = {},
                onMapsClick = {},
                onExitClick = {},
                previewState = previewState
            )
        }

        composeTestRule.onNodeWithText("Default Map").assertIsDisplayed()

        composeTestRule.onNode(hasTestTag("BoardPreview")).assertExists()
    }
}
