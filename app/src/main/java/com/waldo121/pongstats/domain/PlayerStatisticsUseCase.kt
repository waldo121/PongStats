package com.waldo121.pongstats.domain;


import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

// Retrieve Stats of matches played With or Against a specific player
class PlayerStatisticsUseCase (
    val singleMatchRecordsUseCase: SingleMatchRecordsUseCase,
    val doubleMatchRecordsUseCase: DoubleMatchRecordsUseCase,
    val statisticsModelUseCase: StatisticsModelUseCase
){
    suspend fun statsAgainst(playerId: Int): Flow<MatchesSummary> {
        val singles = singleMatchRecordsUseCase.getAgainst(playerId)
        val doubles = doubleMatchRecordsUseCase.get(playerId, "AGAINST")
        val combined = combine(singles, doubles) {
            singles, doubles ->
            singles + doubles
        }
        return statisticsModelUseCase.matchesSummary(combined)
    }
    suspend fun statsWith(playerId: Int): Flow<MatchesSummary> {
        val doubles = doubleMatchRecordsUseCase.get(playerId, "WITH")
        return statisticsModelUseCase.matchesSummary(doubles)
    }
    suspend fun winRateAgainst(playerId: Int): Flow<List<ChartDataPoint>> {
        val singles = singleMatchRecordsUseCase.getAgainst(playerId)
        val doubles = doubleMatchRecordsUseCase.get(playerId, "AGAINST")
        val combined = combine(singles, doubles) {
                singles, doubles ->
            singles + doubles
        }
        return statisticsModelUseCase.winRateEvolution(combined)
    }
}
