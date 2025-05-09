package com.waldo121.pongstats.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import java.util.Date
import java.time.LocalDate


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