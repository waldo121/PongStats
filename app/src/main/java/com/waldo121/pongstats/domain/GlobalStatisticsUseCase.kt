package com.waldo121.pongstats.domain

import com.waldo121.pongstats.SINGLE_MATCH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.Date


data class ChartDataPoint (
    val date: Date,
    val winRate: Float
)

data class MatchesSummary(
    val totalWins: Int,
    val totalMatches: Int,
    val winRate: Float
)
// TODO: Get through
class GlobalStatisticsUseCase(
    private val singleMatchRecordsUseCase: SingleMatchRecordsUseCase,
    private val doubleMatchRecordsUseCase: DoubleMatchRecordsUseCase,
    private val statisticsModelUseCase: StatisticsModelUseCase
) {
     // Compute data to day resolution
     suspend fun getChartData(matchType: String): Flow<List<ChartDataPoint>> {
         return withContext(Dispatchers.Default) {
             if (matchType == SINGLE_MATCH) {
                 statisticsModelUseCase.winRateEvolution(singleMatchRecordsUseCase.list())
             } else {
                 statisticsModelUseCase.winRateEvolution(doubleMatchRecordsUseCase.list())
             }
         }
     }

    suspend fun getCurrentWinRate(matchType: String): Flow<MatchesSummary> {
        return withContext(Dispatchers.Default) {
            if (matchType == SINGLE_MATCH) {
                statisticsModelUseCase.matchesSummary(singleMatchRecordsUseCase.list())
            } else {
                statisticsModelUseCase.matchesSummary(doubleMatchRecordsUseCase.list())
            }
        }
    }
}