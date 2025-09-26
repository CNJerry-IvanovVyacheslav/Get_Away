package com.example.wappo_game.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.wappo_game.domain.GameState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("maps_prefs")

class DataStoreManager(
    private val dataStore: DataStore<Preferences>,
    private val gson: Gson = Gson()
) {
    constructor(context: Context) : this(context.dataStore)

    fun loadMaps(): Flow<List<GameState>> {
        return dataStore.data.map { prefs ->
            val json = prefs[MAPS_KEY] ?: "[]"
            val type = object : TypeToken<List<GameState>>() {}.type
            gson.fromJson<List<GameState>>(json, type) ?: emptyList()
        }
    }

    suspend fun saveOrUpdateMap(map: GameState) {
        dataStore.edit { prefs ->
            val json = prefs[MAPS_KEY] ?: "[]"
            val type = object : TypeToken<List<GameState>>() {}.type
            val current = gson.fromJson<List<GameState>>(json, type) ?: emptyList()

            val updated = if (current.any { it.name == map.name }) {
                current.map { if (it.name == map.name) map else it }
            } else {
                current + map
            }

            prefs[MAPS_KEY] = gson.toJson(updated)
        }
    }

    suspend fun deleteMap(name: String) {
        dataStore.edit { prefs ->
            val json = prefs[MAPS_KEY] ?: "[]"
            val type = object : TypeToken<List<GameState>>() {}.type
            val current = gson.fromJson<List<GameState>>(json, type) ?: emptyList()
            val updated = current.filterNot { it.name == name }
            prefs[MAPS_KEY] = gson.toJson(updated)
        }
    }

    suspend fun clearMaps() {
        dataStore.edit { prefs ->
            prefs[MAPS_KEY] = "[]"
        }
    }

    fun loadLastMapName(): Flow<String?> {
        return dataStore.data.map { prefs -> prefs[LAST_MAP_KEY] }
    }

    suspend fun saveLastMapName(name: String) {
        dataStore.edit { prefs ->
            prefs[LAST_MAP_KEY] = name
        }
    }

    private companion object {
        private val MAPS_KEY = stringPreferencesKey("custom_maps")
        private val LAST_MAP_KEY = stringPreferencesKey("last_map")
    }
}