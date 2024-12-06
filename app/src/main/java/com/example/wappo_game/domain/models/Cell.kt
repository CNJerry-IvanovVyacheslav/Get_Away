package com.example.wappo_game.domain.models

import com.example.wappo_game.data.cell.CellStatus

data class Cell(
    val id: Int,
    val cellStatus: CellStatus,
    val isExit: Boolean = false,
    val isTrap: Boolean = false,
)