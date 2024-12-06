package com.example.wappo_game.data.map

abstract class MapStatus
class EmptyMap : MapStatus()
class CompleteMap : MapStatus()
class LoadingMap : MapStatus()
class MapError : MapStatus()