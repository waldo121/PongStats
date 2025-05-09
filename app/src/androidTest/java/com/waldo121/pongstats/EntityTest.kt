package com.waldo121.pongstats

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.waldo121.pongstats.data.local.MatchRecordsDatabase
import com.waldo121.pongstats.data.local.dao.DoubleMatchRecordDao
import com.waldo121.pongstats.data.local.dao.SingleMatchRecordDao
import com.waldo121.pongstats.data.local.entities.DoubleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import java.io.IOException
import java.util.Date

@RunWith(AndroidJUnit4::class)
class EntityTest {
    private lateinit var singleMatchRecordDao: SingleMatchRecordDao
    private lateinit var doubleMatchRecordDao: DoubleMatchRecordDao
    private lateinit var db: MatchRecordsDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, MatchRecordsDatabase::class.java).build()

        singleMatchRecordDao = db.singleMatchRecordDao()
        doubleMatchRecordDao = db.doubleMatchRecordDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeSingleMatchRecordAndRetrieve() {
        val singleMatchRecord = SingleMatchRecordEntity(
            numberOfWins = 0,
            numberOfDefeats = 1,
            opponentName = "bob",
            date = Date()
        )
        singleMatchRecordDao.createRecord(singleMatchRecord)
        val records = singleMatchRecordDao.getAll()
        println(records[0])
        assertThat(records[0], equalTo(singleMatchRecord))
    }

    @Test
    @Throws(Exception::class)
    fun writeDoubleMatchRecordAndRetrieve() {
        val doubleMatchRecord = DoubleMatchRecordEntity(
            numberOfWins = 0,
            numberOfDefeats = 1,
            opponent1Name = "bob",
            opponent2Name = "alice",
            date = Date()
        )
        doubleMatchRecordDao.createRecord(doubleMatchRecord)
        val records = doubleMatchRecordDao.getAll()
        println(records[0])
        assertThat(records[0], equalTo(doubleMatchRecord))
    }

}