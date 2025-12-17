package com.waldo121.pongstats.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import java.util.Date



@Entity(tableName = "single_match_records", primaryKeys = ["date","opponent_id"])
@TypeConverters(DateConverter::class)
data class SingleMatchRecordEntity (
    val date: Date,
    @ColumnInfo(name = "opponent_id")
    val opponentId: Int,
    @ColumnInfo(name = "number_wins")
    val numberOfWins: Int,
    @ColumnInfo(name = "number_defeats")
    val numberOfDefeats: Int,
)
data class SingleMatchAndPlayer (
    @Embedded val opponent: PlayerEntity,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "opponentId"
    )
    val singleMatch: SingleMatchRecordEntity
)

@Entity(tableName = "double_match_records", primaryKeys = ["date", "opponent1_id", "opponent2_id", "teammate_id"])
@TypeConverters(DateConverter::class)
data class DoubleMatchRecordEntity (
    val date: Date,
    @ColumnInfo(name = "opponent1_id")
    val opponent1Id: Int,
    @ColumnInfo(name = "opponent2_id")
    val opponent2Id: Int,
    @ColumnInfo(name = "teammate_id")
    val teammateId: Int,
    @ColumnInfo(name = "number_wins")
    val numberOfWins: Int,
    @ColumnInfo(name = "number_defeats")
    val numberOfDefeats: Int,
)
data class DoubleMatchAndPlayers (
    @Embedded val opponent1: PlayerEntity,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "opponent1Id"
    )
    @Embedded val opponent2: PlayerEntity,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "opponent2Id"
    )
    @Embedded val teammate: PlayerEntity,
    @Relation(
        parentColumn = "playerId",
        entityColumn = "teammateId"
    )
    val doubleMatch: DoubleMatchRecordEntity
)

class DateConverter {
    @TypeConverter
    fun toLong(date: Date): Long {
        return date.toInstant().toEpochMilli()
    }
    @TypeConverter
    fun fromLong(value: Long): Date {
        return Date(value)
    }
}

fun SingleMatchRecordEntity.toDomain(): SingleMatchRecord = SingleMatchRecord(
    opponentId = opponentId,
    date = date,
    numberOfDefeats = numberOfDefeats,
    numberOfWins = numberOfWins
)
fun DoubleMatchRecordEntity.toDomain(): DoubleMatchRecord = DoubleMatchRecord(
    opponent1Id = opponent1Id,
    opponent2Id = opponent2Id,
    teammateId = teammateId,
    date = date,
    numberOfDefeats = numberOfDefeats,
    numberOfWins = numberOfWins
)