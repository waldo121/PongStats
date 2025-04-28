package com.waldo121.pongstats.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date


@Entity(tableName = "single_match_records")
data class SingleMatchRecordEntity (
    @PrimaryKey
    val date: Date,
    val opponentName: String,
    @ColumnInfo(name = "number_wins")
    val numberOfWins: Int,
    @ColumnInfo(name = "number_defeats")
    val numberOfDefeats: Int,
)