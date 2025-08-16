package com.chatnoir.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.chatnoir.R

class GameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val catDrawable = context.getDrawable(R.drawable.cat1)
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
                val left = offX + c * cellSize
                val top = offY + r * cellSize
                val right = left + cellSize - 2f
                val bottom = top + cellSize - 2f

                when (cell.type) {
                    CellType.EMPTY -> {
                        paint.color = 0xFFD7D5E1.toInt()
                        canvas.drawRect(left, top, right, bottom, paint)
                    }
                    CellType.FENCE -> {
                        paint.color = 0xFF625b71.toInt()
                        canvas.drawRect(left, top, right, bottom, paint)
                    }
                    CellType.CAT -> {
                        catDrawable?.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
                        catDrawable?.draw(canvas)
                    }
                }
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        postInvalidateOnAnimation()
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