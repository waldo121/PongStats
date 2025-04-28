package com.waldo121.pongstats.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity

@Dao
interface SingleMatchRecordDao {
    @Query("SELECT * FROM single_match_records")
    fun getAll(): List<SingleMatchRecordEntity>
    @Insert
    fun createRecord(vararg matchRecordEntity: SingleMatchRecordEntity)
}