package com.waldo121.pongstats.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.waldo121.pongstats.data.local.dao.DoubleMatchRecordDao
import com.waldo121.pongstats.data.local.dao.SingleMatchRecordDao
import com.waldo121.pongstats.data.local.entities.DoubleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity

@Database(entities = [SingleMatchRecordEntity::class, DoubleMatchRecordEntity::class], version = 1)
abstract class MatchRecordsDatabase : RoomDatabase() {
    abstract fun singleMatchRecordDao(): SingleMatchRecordDao
    abstract fun doubleMatchRecordDao(): DoubleMatchRecordDao
    companion object {
        @Volatile
        private var db: MatchRecordsDatabase? = null

        fun getDatabase(context: Context): MatchRecordsDatabase {
            db?: synchronized(this){
                db ?: Room.databaseBuilder(
                    context.applicationContext,
                    MatchRecordsDatabase::class.java, "match-record-database"
                ).build().also { db = it }
            }
            return db!!
        }
    }

}

