package com.chatnoir.game

import android.content.Context

class ScoreManager(context: Context) {
    private val prefs = context.getSharedPreferences("chatnoir_score", Context.MODE_PRIVATE)

    fun getUserWins(): Int = prefs.getInt("userWins", 0)
    fun getCatWins(): Int = prefs.getInt("catWins", 0)

    fun addUserWin() { prefs.edit().putInt("userWins", getUserWins() + 1).apply() }
    fun addCatWin() { prefs.edit().putInt("catWins", getCatWins() + 1).apply() }
}