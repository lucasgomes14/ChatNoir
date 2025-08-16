package com.chatnoir.game

enum class CellType { EMPTY, FENCE, CAT }

data class Cell(val row: Int, val col: Int, var type: CellType = CellType.EMPTY)