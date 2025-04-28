package com.waldo121.pongstats.data.model

import java.util.Date


data class SingleMatchRecord (
    val numberOfWins: Int,
    val numberOfDefeats: Int,
    val opponentName: String,
    val date: Date
)
