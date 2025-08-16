package com.chatnoir

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chatnoir.game.Board
import com.chatnoir.game.GameView
import com.chatnoir.game.ScoreManager

class MainActivity : AppCompatActivity() {

    private lateinit var board: Board
    private lateinit var scoreManager: ScoreManager
    private lateinit var gameView: GameView

    private lateinit var tvUserScore: TextView
    private lateinit var tvCatScore: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        board = Board(size = 11)
        scoreManager = ScoreManager(this)

        tvUserScore = findViewById(R.id.tvUserScore)
        tvCatScore = findViewById(R.id.tvCatScore)
        gameView = findViewById(R.id.gameView)
        gameView.setBoard(board)

        findViewById<Button>(R.id.btnNewGame).setOnClickListener { newGame() }

        gameView.onGameEnd = { winner ->
            when (winner) {
                GameView.Winner.CAT -> scoreManager.addCatWin()
                GameView.Winner.USER -> scoreManager.addUserWin()
            }
            updateScores()
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.game_over))
                .setMessage(
                    when (winner) {
                        GameView.Winner.CAT -> getString(R.string.cat_won)
                        GameView.Winner.USER -> getString(R.string.user_won)
                    }
                )
                .setPositiveButton(getString(R.string.new_game)) { _, _ -> newGame() }
                .setCancelable(false)
                .show()
        }

        updateScores()
        newGame()
    }

    private fun updateScores() {
        tvUserScore.text = getString(R.string.user_score, scoreManager.getUserWins())
        tvCatScore.text = getString(R.string.cat_score, scoreManager.getCatWins())
    }

    private fun newGame() {
        board.resetBoard()
        gameView.invalidate()
    }
}