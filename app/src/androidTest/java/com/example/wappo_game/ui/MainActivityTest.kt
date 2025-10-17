package com.example.wappo_game.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun app_starts_on_menu_screen() {
        composeTestRule.onNodeWithText("Play").assertIsDisplayed()
    }

    @Test
    fun clicking_play_navigates_to_game_screen() {
        composeTestRule.onNodeWithText("Play").performClick()
        composeTestRule.onNodeWithText("Moves:", substring = true).assertIsDisplayed()
    }

    @Test
    fun clicking_campaign_navigates_to_campaign_screen() {
        composeTestRule.onNodeWithText("Campaign").performClick()
        composeTestRule.onNodeWithText("Campaign Levels").assertIsDisplayed()
    }

    @Test
    fun clicking_create_map_navigates_to_editor_screen() {
        composeTestRule.onNodeWithText("Create Map").performClick()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
    }

    @Test
    fun clicking_saved_maps_navigates_to_maps_screen() {
        composeTestRule.onNodeWithText("Saved Maps").performClick()
        composeTestRule.onNodeWithText("Saved Maps").assertIsDisplayed()
    }

    @Test
    fun clicking_exit_finishes_activity() {
        composeTestRule.onNodeWithText("Exit").performClick()
        composeTestRule.waitForIdle()
        assertTrue(composeTestRule.activityRule.scenario.state == Lifecycle.State.DESTROYED)
    }
}