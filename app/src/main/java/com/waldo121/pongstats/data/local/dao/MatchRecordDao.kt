package com.waldo121.pongstats.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.waldo121.pongstats.data.local.entities.DoubleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SingleMatchRecordDao {
    @Query("SELECT * " +
            "FROM single_match_records " +
            "ORDER BY date ASC")
    fun getAll(): Flow<List<SingleMatchRecordEntity>>
    @Query("SELECT * " +
            "FROM single_match_records " +
            "WHERE opponent_id = :playerId " +
            "ORDER BY date ASC")
    fun getMatchesAgainst(playerId: Int): Flow<List<SingleMatchRecordEntity>>
    @Insert
    fun createRecord(matchRecordEntity: SingleMatchRecordEntity)
}
@Dao
interface DoubleMatchRecordDao {
    @Query("SELECT * " +
            "FROM double_match_records " +
            "ORDER BY date ASC")
    fun getAll(): Flow<List<DoubleMatchRecordEntity>>
    @Query("SELECT * " +
            "FROM double_match_records " +
            "WHERE opponent1_id = :playerId " +
            "OR opponent2_id = :playerId " +
            "ORDER BY date ASC")
    fun getMatchesAgainst(playerId: Int): Flow<List<DoubleMatchRecordEntity>>
    @Query("SELECT *" +
            "FROM double_match_records " +
            "WHERE teammate_id= :playerId " +
            "ORDER BY date ASC")
    fun getMatchesWith(playerId: Int): Flow<List<DoubleMatchRecordEntity>>
    @Insert
    fun createRecord(matchRecordEntity: DoubleMatchRecordEntity)
}