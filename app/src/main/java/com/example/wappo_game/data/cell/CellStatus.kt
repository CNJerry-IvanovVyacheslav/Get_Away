package com.example.wappo_game.data.cell

abstract class CellStatus
class EmptyCell : CellStatus()
class CellError : CellStatus()
class FulledCell : CellStatus()