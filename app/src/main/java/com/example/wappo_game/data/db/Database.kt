package com.example.wappo_game.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [MapEntity::class], exportSchema = false)
abstract class Database: RoomDatabase() {
    fun dao :
}