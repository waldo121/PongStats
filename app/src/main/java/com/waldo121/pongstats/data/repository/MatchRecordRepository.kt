package com.waldo121.pongstats.data.repository


import com.waldo121.pongstats.data.local.MatchRecordsDatabase
import com.waldo121.pongstats.data.local.dao.DoubleMatchRecordDao
import com.waldo121.pongstats.data.local.dao.SingleMatchRecordDao
import com.waldo121.pongstats.data.local.entities.DoubleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity
import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import java.sql.Date

class MatchRecordRepository (
    private val singleMatchRecordDao: SingleMatchRecordDao,
    private val doubleMatchRecordDao: DoubleMatchRecordDao,
) {
    fun createSingleMatchRecord(record: SingleMatchRecord) {
        singleMatchRecordDao.createRecord(
            SingleMatchRecordEntity(
            date = Date(record.date.toInstant().toEpochMilli()),
            opponentName = record.opponentName,
            numberOfWins = record.numberOfWins,
            numberOfDefeats = record.numberOfDefeats
        )
        )
    }
    fun getAllSingleMatchRecords(): List<SingleMatchRecord> {
        val singleMatchRecordsEntities: List<SingleMatchRecordEntity> = singleMatchRecordDao.getAll()
        return singleMatchRecordsEntities.map {
            singleMatchRecordEntity ->
            SingleMatchRecord(
                opponentName = singleMatchRecordEntity.opponentName,
                date = singleMatchRecordEntity.date,
                numberOfDefeats = singleMatchRecordEntity.numberOfDefeats,
                numberOfWins = singleMatchRecordEntity.numberOfWins
            )
        }

    }
    fun createDoubleMatchRecord(record: DoubleMatchRecord) {
        doubleMatchRecordDao.createRecord(
            DoubleMatchRecordEntity(
                date = Date(record.date.toInstant().toEpochMilli()),
                opponent1Name = record.opponent1Name,
                opponent2Name = record.opponent2Name,
                numberOfWins = record.numberOfWins,
                numberOfDefeats = record.numberOfDefeats,
            )
        )
    }
    fun getAllDoubleMatchRecords(): List<DoubleMatchRecord> {
        val doubleMatchRecordEntities: List<DoubleMatchRecordEntity> = doubleMatchRecordDao.getAll()
        doubleMatchRecordEntities.map { doubleMatchRecordEntity ->
            DoubleMatchRecord(
                date = doubleMatchRecordEntity.date,
                opponent1Name = doubleMatchRecordEntity.opponent1Name,
                opponent2Name = doubleMatchRecordEntity.opponent2Name,
                numberOfWins = doubleMatchRecordEntity.numberOfWins,
                numberOfDefeats = doubleMatchRecordEntity.numberOfDefeats,
            )
        }
    }
}
