package com.example.wappo_game.presentation

import com.example.wappo_game.data.DataStoreManager
import com.example.wappo_game.domain.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalCoroutinesApi::class)
class MapsScreenViewModelTest {

    private lateinit var tempFile: File
    private lateinit var manager: DataStoreManager
    private lateinit var viewModel: GameViewModelTest.GameViewModelFake

    @Before
    fun setUp() {
        tempFile = File.createTempFile("test_prefs_${UUID.randomUUID()}", ".preferences_pb")
        val dataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO),
            produceFile = { tempFile }
        )
        manager = DataStoreManager(dataStore)

        viewModel = GameViewModelTest.GameViewModelFake(manager)
    }

    @Test
    fun `initial saved maps is empty`() = runTest {
        val maps = viewModel.savedMaps.first()
        assertThat(maps).isEmpty()
    }

    @Test
    fun `save map adds to saved maps`() = runTest {
        val map = GameState(
            tiles = listOf(Tile(Pos(0,0))),
            playerPos = Pos(0,0),
            enemyPos = Pos(0,1),
            name = "Map1"
        )
        viewModel.dataStore.saveOrUpdateMap(map)
        val maps = viewModel.savedMaps.first()
        assertThat(maps.any { it.name == "Map1" }).isTrue()
    }

    @Test
    fun `delete map removes from saved maps`() = runTest {
        val map = GameState(
            tiles = listOf(Tile(Pos(0,0))),
            playerPos = Pos(0,0),
            enemyPos = Pos(0,1),
            name = "MapToDelete"
        )
        viewModel.dataStore.saveOrUpdateMap(map)
        viewModel.deleteMap("MapToDelete")
        val maps = viewModel.savedMaps.first()
        assertThat(maps.any { it.name == "MapToDelete" }).isFalse()
    }

    @Test
    fun `clear all maps removes all saved maps`() = runTest {
        val map1 = GameState(
            tiles = listOf(Tile(Pos(0,0))),
            playerPos = Pos(0,0),
            enemyPos = Pos(0,1),
            name = "Map1"
        )
        val map2 = GameState(
            tiles = listOf(Tile(Pos(1,1))),
            playerPos = Pos(1,1),
            enemyPos = Pos(1,2),
            name = "Map2"
        )
        viewModel.dataStore.saveOrUpdateMap(map1)
        viewModel.dataStore.saveOrUpdateMap(map2)
        viewModel.clearAllMaps()
        val maps = viewModel.savedMaps.first()
        assertThat(maps).isEmpty()
    }

    @Test
    fun `load map returns correct map`() = runTest {
        val map = GameState(
            tiles = listOf(Tile(Pos(0,0))),
            playerPos = Pos(0,0),
            enemyPos = Pos(0,1),
            name = "LoadMap"
        )
        viewModel.loadCustomMap(map)
        val state = viewModel.state.value
        assertThat(state.name).isEqualTo("LoadMap")
        assertThat(state.playerPos).isEqualTo(map.initialPlayerPos)
    }
}
