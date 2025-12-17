package com.waldo121.pongstats.domain
import android.util.Log
import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.MatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow


class SingleMatchRecordsUseCase(
    val repository: MatchRecordRepository
) {

    suspend fun create(matchRecord: SingleMatchRecord ) {
        repository.createSingleMatchRecord(record = matchRecord)
    }
    fun getAgainst(playerId: Int): Flow<List<MatchRecord>> {
        return repository.getSingleMatchesAgainst(playerId)
    }
    fun list(): Flow<List<MatchRecord>> {
        return repository.getSingleMatchRecords()
    }
}

class DoubleMatchRecordsUseCase(
    val repository: MatchRecordRepository
) {
    private val logTag: String = "DoubleMatchRecordsUseCase"
    private val acceptedSideValues = listOf("WITH", "AGAINST")
    suspend fun create(matchRecord: DoubleMatchRecord) {
        repository.createDoubleMatchRecord(record = matchRecord)
    }
    @Throws(Exception::class)
    fun get(playerId: Int, side: String): Flow<List<MatchRecord>> {
        return when (side) {
            "AGAINST" -> {
                repository.getDoubleMatchesAgainst(playerId)
            }

            "WITH" -> {
                repository.getDoubleMatchesWith(playerId)
            }
            else -> {
                Log.e(logTag, "Invalid side argument, accepted values are $acceptedSideValues")
                throw Exception("Invalid side argument, accepted values are $acceptedSideValues")
            }
        }
    }

    fun list(): Flow<List<MatchRecord>> {
        return repository.getDoubleMatchRecords()
    }
}