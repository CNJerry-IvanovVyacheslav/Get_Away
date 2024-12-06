package com.example.wappo_game.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface Dao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addCellToMap(id: MapEntity)
}