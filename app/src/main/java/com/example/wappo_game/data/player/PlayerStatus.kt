package com.example.wappo_game.data.player

abstract class PlayerStatus
class IsDefeat : PlayerStatus()
class IsComplete : PlayerStatus()
class IsCanMove : PlayerStatus()
class Error : PlayerStatus()