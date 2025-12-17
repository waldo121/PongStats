package com.waldo121.pongstats.data.model

import com.waldo121.pongstats.data.local.entities.DoubleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity
import java.util.Date

abstract class MatchRecord {
    abstract val numberOfWins: Int
    abstract val numberOfDefeats: Int
    abstract val date: Date
}
data class SingleMatchRecord(
    val opponentId: Int,
    override val numberOfWins: Int,
    override val numberOfDefeats: Int,
    override val date: Date
): MatchRecord()

data class DoubleMatchRecord (
    val opponent1Id: Int,
    val opponent2Id: Int,
    val teammateId: Int,
    override val numberOfWins: Int,
    override val numberOfDefeats: Int,
    override val date: Date,
): MatchRecord()

fun SingleMatchRecord.toEntity(): SingleMatchRecordEntity = SingleMatchRecordEntity(
    opponentId = opponentId,
    date = date,
    numberOfDefeats = numberOfDefeats,
    numberOfWins = numberOfWins
)

fun DoubleMatchRecord.toEntity(): DoubleMatchRecordEntity = DoubleMatchRecordEntity(
    opponent1Id = opponent1Id,
    opponent2Id = opponent2Id,
    teammateId = teammateId,
    date = date,
    numberOfDefeats = numberOfDefeats,
    numberOfWins = numberOfWins
)
