package com.waldo121.pongstats.data.model

import java.util.Date


data class SingleMatchRecord (
    val numberOfWins: Int,
    val numberOfDefeats: Int,
    val opponentName: String,
    val date: Date
)

data class DoubleMatchRecord (
    val numberOfWins: Int,
    val numberOfDefeats: Int,
    val opponent1Name: String,
    val opponent2Name: String,
    val date: Date
)