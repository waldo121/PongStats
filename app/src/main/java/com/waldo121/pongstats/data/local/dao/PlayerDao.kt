package com.waldo121.pongstats.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.waldo121.pongstats.data.local.entities.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT rowid, name from players where rowid = :playerId")
    fun getPlayer(playerId: Int): PlayerEntity
    @Query("SELECT rowid, name from players where name MATCH :playerName")
    fun search(playerName: String): List<PlayerEntity>
    @Query("SELECT rowid, name from players")
    fun getAll(): Flow<List<PlayerEntity>>
    @Insert
    fun createPlayer(player: PlayerEntity)
}