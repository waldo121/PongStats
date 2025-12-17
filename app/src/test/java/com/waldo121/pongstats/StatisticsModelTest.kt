package com.waldo121.pongstats


import com.waldo121.pongstats.data.model.MatchRecord
import com.waldo121.pongstats.domain.ChartDataPoint
import com.waldo121.pongstats.domain.MatchesSummary
import com.waldo121.pongstats.domain.StatisticsModelUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.util.Date


data class FakeMatchRecord(
    override val numberOfWins: Int,
    override val numberOfDefeats: Int,
    override val date: Date
): MatchRecord ()

class StatisticsModelTest {
    private lateinit var statisticsModelUseCase: StatisticsModelUseCase
    @Before
    fun setup() {
        statisticsModelUseCase = StatisticsModelUseCase()
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testMatchesSummary() = runTest {
        val now = Date()
        val matches: Flow<List<MatchRecord>> = MutableStateFlow(
            listOf(
                FakeMatchRecord(1, 2, now),
                FakeMatchRecord(12, 45, now),
                FakeMatchRecord(34, 8, now),
                FakeMatchRecord(4, 5, now)
            )
        )
        val summary = MutableStateFlow(MatchesSummary(0,0,0f))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            statisticsModelUseCase.matchesSummary(matches)
                .collect { v: MatchesSummary -> summary.value = v }
        }
        assert(summary.value.totalWins == 51)
        assert(summary.value.totalMatches == 111)
        assert(summary.value.winRate == 51F/111F)
    }
    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testWinRateEvolution() = runTest {
        val now = Date()
        val datePlus1Day = Date(LocalDateTime.now().plusDays(1).toLocalDate().toEpochDay())
        val datePlus2Day = Date(LocalDateTime.now().plusDays(2).toLocalDate().toEpochDay())
        val datePlus3Day = Date(LocalDateTime.now().plusDays(3).toLocalDate().toEpochDay())
        val matches: Flow<List<MatchRecord>> = MutableStateFlow(
            listOf(
                FakeMatchRecord(1, 2, now),
                FakeMatchRecord(12, 45, datePlus1Day),
                FakeMatchRecord(34, 8, datePlus2Day),
                FakeMatchRecord(4, 5, datePlus3Day)
            )
        )
        val dataPoints = MutableStateFlow<List<ChartDataPoint>>(emptyList())
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            statisticsModelUseCase.winRateEvolution(matches)
                .collect { v: List<ChartDataPoint> -> dataPoints.value = v }
        }
        assert(dataPoints.value.find { e -> e.date == now }?.winRate == 1.toFloat() / 3.toFloat())
        assert(dataPoints.value.find { e -> e.date == datePlus1Day }?.winRate == 12.toFloat() / 57.toFloat())
        assert(dataPoints.value.find { e -> e.date == datePlus2Day }?.winRate == 34.toFloat() / 42.toFloat())
        assert(dataPoints.value.find { e -> e.date == datePlus3Day }?.winRate == 4.toFloat() / 9.toFloat())
    }
}