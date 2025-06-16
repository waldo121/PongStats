package com.waldo121.pongstats.data.model

import com.waldo121.pongstats.data.local.entities.DoubleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity
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

fun SingleMatchRecord.toEntity(): SingleMatchRecordEntity = SingleMatchRecordEntity(
    opponentName = opponentName,
    date = date,
    numberOfDefeats = numberOfDefeats,
    numberOfWins = numberOfWins
)

fun DoubleMatchRecord.toEntity(): DoubleMatchRecordEntity = DoubleMatchRecordEntity(
    opponent1Name = opponent1Name,
    opponent2Name = opponent2Name,
    date = date,
    numberOfDefeats = numberOfDefeats,
    numberOfWins = numberOfWins
)
