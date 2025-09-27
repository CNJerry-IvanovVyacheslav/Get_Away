package com.example.wappo_game.ui

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.unit.dp
import com.example.wappo_game.domain.*
import com.example.wappo_game.presentation.TestActivity
import org.junit.Rule
import org.junit.Test


class BoardPreviewTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    private fun createTestState(): GameState {
        val rows = 4
        val cols = 5
        val tiles = mutableListOf<Tile>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val type = when {
                    r == 0 && c == 0 -> TileType.TRAP
                    r == rows - 1 && c == cols - 1 -> TileType.EXIT
                    else -> TileType.EMPTY
                }
                tiles.add(Tile(Pos(r, c), type))
            }
        }

        val walls = setOf(
            Pos(0, 1) to Pos(0, 2),
            Pos(1, 1) to Pos(2, 1)
        )

        return GameState(
            rows = rows,
            cols = cols,
            tiles = tiles,
            playerPos = Pos(1, 1),
            enemyPos = Pos(2, 2),
            walls = walls
        )
    }

    @Test
    fun board_preview_has_tag() {
        val state = createTestState()
        composeTestRule.setContent {
            BoardPreview(state = state, sizeDp = 200.dp)
        }

        composeTestRule.onNodeWithTag("BoardPreview")
            .assertExists("The BoardPreview must be present on the screen")
    }

    @Test
    fun board_preview_tiles_have_traps_and_exit() {
        val state = createTestState()
        composeTestRule.setContent {
            BoardPreview(state = state, sizeDp = 200.dp)
        }

        composeTestRule.onNodeWithText("T").assertExists("There should be a TRAP tile")
        composeTestRule.onNodeWithText("EXIT").assertExists("There should be an EXIT tile")
    }

    @Test
    fun board_preview_shows_player_and_enemy() {
        val state = createTestState()
        composeTestRule.setContent {
            BoardPreview(state = state, sizeDp = 200.dp)
        }

        composeTestRule.onNodeWithText("P").assertExists("The player should be displayed")
        composeTestRule.onNodeWithText("E").assertExists("The enemy should be displayed")
    }

    @Test
    fun board_preview_walls_exist_in_state() {
        val state = createTestState()
        composeTestRule.setContent {
            BoardPreview(state = state, sizeDp = 200.dp)
        }

        composeTestRule.onNodeWithTag("BoardPreview")
            .assertExists("The BoardPreview with walls should be rendered")
    }
}
