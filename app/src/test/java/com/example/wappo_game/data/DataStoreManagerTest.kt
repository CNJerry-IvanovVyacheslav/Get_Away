package com.example.wappo_game.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.UUID
import com.example.wappo_game.domain.*
import com.google.common.truth.Truth.assertThat

@OptIn(ExperimentalCoroutinesApi::class)
class DataStoreManagerTest {

    private lateinit var tempFile: File
    private lateinit var manager: DataStoreManager

    @Before
    fun setUp() {
        tempFile = File.createTempFile("test_prefs_${UUID.randomUUID()}", ".preferences_pb")
        val dataStore = PreferenceDataStoreFactory.create(
            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO),
            produceFile = { tempFile }
        )
        manager = DataStoreManager(dataStore)
    }

    @After
    fun tearDown() {
        tempFile.delete()
    }

    private fun fakeGameState(name: String) = GameState(
        tiles = listOf(Tile(Pos(0, 0), TileType.EMPTY)),
        playerPos = Pos(0, 0),
        enemyPos = Pos(0, 1),
        name = name
    )

    @Test
    fun saveAndLoadMap() = runTest {
        val map = fakeGameState("Map1")

        manager.saveOrUpdateMap(map)
        val result = manager.loadMaps().first()

        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("Map1")
    }

    @Test
    fun updateExistingMap() = runTest {
        val map = fakeGameState("Map1")
        manager.saveOrUpdateMap(map)

        val updated = map.copy(playerMoves = 5)
        manager.saveOrUpdateMap(updated)

        val result = manager.loadMaps().first()
        assertThat(result).hasSize(1)
        assertThat(result[0].playerMoves).isEqualTo(5)
    }

    @Test
    fun deleteMap() = runTest {
        val map1 = fakeGameState("Map1")
        val map2 = fakeGameState("Map2")
        manager.saveOrUpdateMap(map1)
        manager.saveOrUpdateMap(map2)

        manager.deleteMap("Map1")
        val result = manager.loadMaps().first()

        assertThat(result).hasSize(1)
        assertThat(result[0].name).isEqualTo("Map2")
    }

    @Test
    fun clearMaps() = runTest {
        val map1 = fakeGameState("Map1")
        val map2 = fakeGameState("Map2")
        manager.saveOrUpdateMap(map1)
        manager.saveOrUpdateMap(map2)

        manager.clearMaps()
        val result = manager.loadMaps().first()
        assertThat(result).isEmpty()
    }

    @Test
    fun saveAndLoadLastMapName() = runTest {
        manager.saveLastMapName("LastMap")
        val name = manager.loadLastMapName().first()
        assertThat(name).isEqualTo("LastMap")
    }
}
