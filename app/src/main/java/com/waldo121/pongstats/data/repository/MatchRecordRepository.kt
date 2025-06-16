package com.waldo121.pongstats.data.repository


import com.waldo121.pongstats.data.local.MatchRecordsDatabase
import com.waldo121.pongstats.data.local.entities.DoubleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.toDomain
import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import com.waldo121.pongstats.data.model.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.sql.SQLException

class MatchRecordRepository (
    private val matchRecordLocalDatabase: MatchRecordsDatabase
) {
    @Throws(SQLException::class)
    suspend fun createSingleMatchRecord(record: SingleMatchRecord) {
        withContext(Dispatchers.IO) {
            matchRecordLocalDatabase.singleMatchRecordDao().createRecord(
                record.toEntity()
            )
        }
    }

    @Throws(SQLException::class)
    suspend fun getAllSingleMatchRecords(): Flow<List<SingleMatchRecord>> {
        return withContext(Dispatchers.IO) {
            val dao = matchRecordLocalDatabase.singleMatchRecordDao()
            return@withContext dao.getAll()
                .map { entities: List<SingleMatchRecordEntity> -> entities.map { it.toDomain() } }
        }
    }

    @Throws(SQLException::class)
    suspend fun createDoubleMatchRecord(record: DoubleMatchRecord) {
        withContext(Dispatchers.IO) {
            matchRecordLocalDatabase.doubleMatchRecordDao().createRecord(
                record.toEntity()
            )
        }
    }

    @Throws(SQLException::class)
    suspend fun getAllDoubleMatchRecords(): Flow<List<DoubleMatchRecord>> {
        return withContext(Dispatchers.IO) {
            val dao = matchRecordLocalDatabase.doubleMatchRecordDao()

            return@withContext dao.getAll()
                .map { entities: List<DoubleMatchRecordEntity> -> entities.map { it.toDomain() } }

        }
    }
}
