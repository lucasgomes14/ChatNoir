package com.chatnoir.game

import kotlin.random.Random

class Board(val size: Int = 11) {
    val grid: Array<Array<Cell>> = Array(size) { r -> Array(size) { c -> Cell(r, c) } }

    var catRow: Int = size / 2
    var catCol: Int = size / 2

    fun resetBoard() {
        // limpar
        for (r in 0 until size) for (c in 0 until size) grid[r][c].type = CellType.EMPTY

        // colocar gato no centro
        catRow = size / 2
        catCol = size / 2
        grid[catRow][catCol].type = CellType.CAT

        // colocar 9 a 15 cercas aleatórias (não sobre o gato)
        val fences = Random.nextInt(9, 16)
        var placed = 0
        while (placed < fences) {
            val r = Random.nextInt(0, size)
            val c = Random.nextInt(0, size)
            if (grid[r][c].type == CellType.EMPTY) {
                grid[r][c].type = CellType.FENCE
                placed++
            }
        }
    }

    fun placeFence(r: Int, c: Int): Boolean {
        if (r !in 0 until size || c !in 0 until size) return false
        val cell = grid[r][c]
        if (cell.type != CellType.EMPTY) return false
        cell.type = CellType.FENCE
        return true
    }

    fun moveCatTo(r: Int, c: Int) {
        grid[catRow][catCol].type = CellType.EMPTY
        catRow = r
        catCol = c
        grid[catRow][catCol].type = CellType.CAT
    }
}