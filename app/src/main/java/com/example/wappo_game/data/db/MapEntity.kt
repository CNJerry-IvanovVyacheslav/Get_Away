package com.example.wappo_game.data.db

import androidx.room.Entity
import com.example.wappo_game.data.map.MapStatus
import com.example.wappo_game.domain.models.Cell

@Entity(tableName = "map_table")
data class MapEntity(
    val id: Int,
    val cell: Cell,
    val mapStatus: MapStatus
)