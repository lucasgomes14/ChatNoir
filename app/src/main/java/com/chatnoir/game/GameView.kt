package com.chatnoir.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private var _board: Board? = null
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var onGameEnd: ((Winner) -> Unit)? = null

    enum class Winner { CAT, USER }

    fun setBoard(board: Board) {
        _board = board
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val board = _board ?: return
        val size = board.size

        val cellSize = (width.coerceAtMost(height)) / size.toFloat()
        val offX = (width - cellSize * size) / 2f
        val offY = (height - cellSize * size) / 2f

        for (r in 0 until size) {
            for (c in 0 until size) {
                val cell = board.grid[r][c]
                paint.color = when (cell.type) {
                    CellType.EMPTY -> 0xFFEFEFEF.toInt()
                    CellType.FENCE -> 0xFF4A4A4A.toInt()
                    CellType.CAT   -> 0xFF000000.toInt()
                }
                val left = offX + c * cellSize
                val top = offY + r * cellSize
                canvas.drawRect(left, top, left + cellSize - 2f, top + cellSize - 2f, paint)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val board = _board ?: return true
            val size = board.size
            val cellSize = (width.coerceAtMost(height)) / size.toFloat()
            val offX = (width - cellSize * size) / 2f
            val offY = (height - cellSize * size) / 2f

            val col = ((event.x - offX) / cellSize).toInt()
            val row = ((event.y - offY) / cellSize).toInt()

            if (row in 0 until size && col in 0 until size) {
                val clicked = board.grid[row][col]
                if (clicked.type == CellType.EMPTY) {
                    // Usuário coloca cerca
                    if (board.placeFence(row, col)) {
                        invalidate()
                        // Turno do gato (IA A*)
                        when (GameLogic.performCatMove(board)) {
                            GameLogic.MoveResult.CAT_WON -> onGameEnd?.invoke(Winner.CAT)
                            GameLogic.MoveResult.USER_WON -> onGameEnd?.invoke(Winner.USER)
                            GameLogic.MoveResult.MOVED -> invalidate()
                        }
                    }
                }
            }
        }
        return true
    }
}