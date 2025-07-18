package com.waldo121.pongstats.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import java.util.Date


@Entity(tableName = "single_match_records")
@TypeConverters(DateConverter::class)
data class SingleMatchRecordEntity (
    @PrimaryKey
    val date: Date,
    val opponentName: String,
    @ColumnInfo(name = "number_wins")
    val numberOfWins: Int,
    @ColumnInfo(name = "number_defeats")
    val numberOfDefeats: Int,
)

@Entity(tableName = "double_match_records")
@TypeConverters(DateConverter::class)
data class DoubleMatchRecordEntity (
    @PrimaryKey
    val date: Date,
    val opponent1Name: String,
    val opponent2Name: String,
    @ColumnInfo(name = "number_wins")
    val numberOfWins: Int,
    @ColumnInfo(name = "number_defeats")
    val numberOfDefeats: Int,
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
    opponentName = opponentName,
    date = date,
    numberOfDefeats = numberOfDefeats,
    numberOfWins = numberOfWins
)
fun DoubleMatchRecordEntity.toDomain(): DoubleMatchRecord = DoubleMatchRecord(
    opponent1Name = opponent1Name,
    opponent2Name = opponent2Name,
    date = date,
    numberOfDefeats = numberOfDefeats,
    numberOfWins = numberOfWins
)