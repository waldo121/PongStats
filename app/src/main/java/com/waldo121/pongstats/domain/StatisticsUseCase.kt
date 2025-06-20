package com.waldo121.pongstats.domain

import com.waldo121.pongstats.SINGLE_MATCH
import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import java.time.ZoneId


data class ChartDataPoint (
    val date: Date,
    val winRate: Int
)

data class CurrentWinRate(
    val totalWins: Int,
    val totalMatches: Int,
    val winRate: Float
)

class DailyWinRateUseCase(
    private val matchRecordRepository: MatchRecordRepository
) {
     // Compute data to day resolution
     suspend operator fun invoke(matchType: String): Flow<List<ChartDataPoint>> {
         val zoneId = ZoneId.systemDefault()
         return withContext(Dispatchers.Default) {
             if (matchType == SINGLE_MATCH) {
                 return@withContext matchRecordRepository.getAllSingleMatchRecords()
                     .map { matchRecords: List<SingleMatchRecord> ->
                         // by date
                         matchRecords.groupBy { it.date }
                             .map { (date, matchesAtDate) ->
                                 val wins = matchesAtDate.sumOf { it.numberOfWins }
                                 val defeats = matchesAtDate.sumOf { it.numberOfDefeats }
                                 val total = wins + defeats
                                 val winRate = if (total > 0) (wins * 100 / total) else 0
                                 ChartDataPoint(date, winRate)
                             }.sortedBy { it.date }
                     }
             } else {
                 return@withContext matchRecordRepository.getAllDoubleMatchRecords()
                     .map { matchRecords: List<DoubleMatchRecord> ->
                         // by date
                         matchRecords.groupBy { it.date }
                             .map { (date, matchesAtDate) ->
                                 val wins = matchesAtDate.sumOf { it.numberOfWins }
                                 val defeats = matchesAtDate.sumOf { it.numberOfDefeats }
                                 val total = wins + defeats
                                 val winRate = if (total > 0) (wins * 100 / total) else 0
                                 ChartDataPoint(date, winRate)
                             }.sortedBy { it.date }
                     }
             }
         }
     }

    suspend fun getCurrentWinRate(matchType: String): Flow<CurrentWinRate> {
        return withContext(Dispatchers.Default) {
            if (matchType == SINGLE_MATCH) {
                matchRecordRepository.getAllSingleMatchRecords()
                    .map { records ->
                        val totalWins = records.sumOf { it.numberOfWins }
                        val totalDefeats = records.sumOf { it.numberOfDefeats }
                        val totalMatches = totalWins + totalDefeats
                        val winRate = if (totalMatches > 0) (totalWins.toFloat() / totalMatches) * 100 else 0f
                        CurrentWinRate(totalWins, totalMatches, winRate)
                    }
            } else {
                matchRecordRepository.getAllDoubleMatchRecords()
                    .map { records ->
                        val totalWins = records.sumOf { it.numberOfWins }
                        val totalDefeats = records.sumOf { it.numberOfDefeats }
                        val totalMatches = totalWins + totalDefeats
                        val winRate = if (totalMatches > 0) (totalWins.toFloat() / totalMatches) * 100 else 0f
                        CurrentWinRate(totalWins, totalMatches, winRate)
                    }
            }
        }
    }
}