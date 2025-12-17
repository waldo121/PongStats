package com.waldo121.pongstats.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.Companion.LOCALIZED
import androidx.room.ColumnInfo.Companion.NOCASE
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import com.waldo121.pongstats.data.model.Player
@Fts4
@Entity(tableName = "players")
data class PlayerEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val playerId: Int,
    @ColumnInfo(name = "name", collate = LOCALIZED)
    val name: String
)

fun PlayerEntity.toDomain(): Player = Player(
    id = playerId,
    name = name
)