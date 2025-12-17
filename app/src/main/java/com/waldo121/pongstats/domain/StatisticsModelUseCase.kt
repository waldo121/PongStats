package com.waldo121.pongstats.domain

import com.waldo121.pongstats.data.model.MatchRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Defines which stats can be extracted from the matchRecords
class StatisticsModelUseCase {
    fun matchesSummary(matches: Flow<List<MatchRecord>>): Flow<MatchesSummary> {
        return matches.map {
                matchRecords ->
            val wins = matchRecords.sumOf { it.numberOfWins }
            val defeats = matchRecords.sumOf { it.numberOfDefeats }
            val total = wins+defeats
            val winRate = if (total > 0) (wins.toFloat() / total.toFloat()) else 0F
            MatchesSummary(totalWins = wins, totalMatches = total, winRate = winRate )
        }
    }
    fun winRateEvolution(matches: Flow<List<MatchRecord>>): Flow<List<ChartDataPoint>> {
        return matches.map { matchRecords: List<MatchRecord> ->
            // by date
            matchRecords.groupBy { it.date }
                .map { (date, matchesAtDate) ->
                    val wins = matchesAtDate.sumOf { it.numberOfWins }
                    val defeats = matchesAtDate.sumOf { it.numberOfDefeats }
                    val total = wins + defeats
                    val winRate = if (total > 0) wins.toFloat()/total.toFloat() else 0F
                    ChartDataPoint(date, winRate)
                }.sortedBy { it.date }
        }
    }


}