package com.example.wappo_game.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_starts_on_menu_screen() {
        composeTestRule.onNodeWithText("Play").assertExists()
    }

    @Test
    fun clicking_play_navigates_to_game_screen() {
        composeTestRule.onNodeWithText("Play").performClick()
        composeTestRule.onNodeWithText("Moves:", substring = true).assertExists()
    }

    @Test
    fun clicking_saved_maps_navigates_to_maps_screen() {
        composeTestRule.onNodeWithText("Saved Maps").performClick()
        composeTestRule.onNodeWithText("Saved Maps").assertExists()
    }

    @Test
    fun clicking_exit_finishes_activity() {
        composeTestRule.onNodeWithText("Exit").performClick()
        composeTestRule.waitForIdle()
        assert(composeTestRule.activityRule.scenario.state.isAtLeast(androidx.lifecycle.Lifecycle.State.DESTROYED))
    }
}
