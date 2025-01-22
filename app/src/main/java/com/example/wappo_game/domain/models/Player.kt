package com.example.wappo_game.domain.models

import com.example.wappo_game.data.player.PlayerStatus
import com.example.wappo_game.data.player.PlayerVisibility

data class Player(
    val status: PlayerStatus,
    val visibility: PlayerVisibility
)
