package com.waldo121.pongstats.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.waldo121.pongstats.data.local.entities.DoubleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SingleMatchRecordDao {
    @Query("SELECT * FROM single_match_records ORDER BY date ASC")
    fun getAll(): Flow<List<SingleMatchRecordEntity>>
    @Insert
    fun createRecord(vararg matchRecordEntity: SingleMatchRecordEntity)
    @Query("SELECT DISTINCT opponentName FROM single_match_records")
    fun getAllUniqueOpponentNames(): Flow<List<String>>
}
@Dao
interface DoubleMatchRecordDao {
    @Query("SELECT * FROM double_match_records ORDER BY date ASC")
    fun getAll(): Flow<List<DoubleMatchRecordEntity>>
    @Insert
    fun createRecord(vararg matchRecordEntity: DoubleMatchRecordEntity)
    @Query("SELECT DISTINCT opponent1Name FROM double_match_records UNION SELECT DISTINCT opponent2Name FROM double_match_records")
    fun getAllUniqueOpponentNames(): Flow<List<String>>
}