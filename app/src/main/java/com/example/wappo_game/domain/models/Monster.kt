package com.example.wappo_game.domain.models

import com.example.wappo_game.data.monster.MonsterStatus
import com.example.wappo_game.data.player.PlayerVisibility

data class Monster(
    val status: MonsterStatus,
    val visibility: PlayerVisibility
)
