package com.waldo121.pongstats.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.waldo121.pongstats.data.local.dao.DoubleMatchRecordDao
import com.waldo121.pongstats.data.local.dao.SingleMatchRecordDao
import com.waldo121.pongstats.data.local.entities.DoubleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity

@Database(entities = [SingleMatchRecordEntity::class, DoubleMatchRecordEntity::class], version = 1)
abstract class MatchRecordsDatabase : RoomDatabase() {
    abstract fun singleMatchRecordDao(): SingleMatchRecordDao
    abstract fun doubleMatchRecordDao(): DoubleMatchRecordDao
}

