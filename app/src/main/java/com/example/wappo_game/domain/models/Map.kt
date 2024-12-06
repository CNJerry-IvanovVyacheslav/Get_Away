package com.example.wappo_game.domain.models

import com.example.wappo_game.data.map.MapStatus

data class Map(
    val id: Int,
    val mapStatus: MapStatus,
    val cell: Cell
)