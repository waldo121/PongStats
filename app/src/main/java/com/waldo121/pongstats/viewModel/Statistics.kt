package com.waldo121.pongstats.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.waldo121.pongstats.DOUBLE_MATCH
import com.waldo121.pongstats.SINGLE_MATCH
import com.waldo121.pongstats.domain.ChartDataPoint
import com.waldo121.pongstats.domain.GlobalStatisticsUseCase
import com.waldo121.pongstats.domain.MatchesSummary
import com.waldo121.pongstats.domain.PlayerStatisticsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val globalStatisticsUseCase: GlobalStatisticsUseCase,
    private val playerStatisticsUseCase: PlayerStatisticsUseCase
) : ViewModel() {

    private val _winRateSingle = MutableStateFlow<List<ChartDataPoint>>(emptyList())
    val winRateSingle: StateFlow<List<ChartDataPoint>> = _winRateSingle
    
    private val _winRateDouble = MutableStateFlow<List<ChartDataPoint>>(emptyList())
    val winRateDouble: StateFlow<List<ChartDataPoint>> = _winRateDouble

    private val _matchesSummarySingle = MutableStateFlow(MatchesSummary(0, 0, 0f))
    val matchesSummarySingle: StateFlow<MatchesSummary> = _matchesSummarySingle

    private val _matchesSummaryDouble = MutableStateFlow(MatchesSummary(0, 0, 0f))
    val matchesSummaryDouble: StateFlow<MatchesSummary> = _matchesSummaryDouble

    private val _matchesSummaryAgainst = MutableStateFlow(MatchesSummary(0,0,0f))
    val matchesSummaryAgainst = _matchesSummaryAgainst

    private val _matchesSummaryWith = MutableStateFlow(MatchesSummary(0,0,0f))
    val matchesSummaryWith = _matchesSummaryWith

    private val _winRateAgainst = MutableStateFlow<List<ChartDataPoint>>(emptyList())
    val winRateAgainst = _winRateAgainst

    companion object {
        val GLOBAL_STATS_USE_CASE_KEY = object : CreationExtras.Key<GlobalStatisticsUseCase> {}
        val PLAYER_STATS_USE_CASE_KEY = object: CreationExtras.Key<PlayerStatisticsUseCase> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val globalStatisticsUseCase = this[GLOBAL_STATS_USE_CASE_KEY] as GlobalStatisticsUseCase
                val playerStatisticsUseCase = this[PLAYER_STATS_USE_CASE_KEY] as PlayerStatisticsUseCase
                StatisticsViewModel(
                    globalStatisticsUseCase = globalStatisticsUseCase,
                    playerStatisticsUseCase = playerStatisticsUseCase
                )
            }
        }
    }

    init {
        // Collect singles data
        viewModelScope.launch(Dispatchers.Default) {
            globalStatisticsUseCase.getChartData(SINGLE_MATCH)
                .collect { winRateData: List<ChartDataPoint> ->
                    _winRateSingle.value = winRateData
                }
        }

        // Collect doubles data in a separate coroutine
        viewModelScope.launch(Dispatchers.Default) {
            globalStatisticsUseCase.getChartData(DOUBLE_MATCH)
                .collect { winRateData: List<ChartDataPoint> ->
                    _winRateDouble.value = winRateData
                }
        }

        // Collect current singles win rate
        viewModelScope.launch(Dispatchers.Default) {
            globalStatisticsUseCase.getCurrentWinRate(SINGLE_MATCH)
                .collect { currentWinRate ->
                    _matchesSummarySingle.value = currentWinRate
                }
        }

        // Collect current doubles win rate
        viewModelScope.launch(Dispatchers.Default) {
            globalStatisticsUseCase.getCurrentWinRate(DOUBLE_MATCH)
                .collect { currentWinRate ->
                    _matchesSummaryDouble.value = currentWinRate
                }
        }
    }
    fun statsAgainstPlayer(playerId: Int) {
        viewModelScope.launch {
            playerStatisticsUseCase.statsAgainst(playerId).collect { value ->
                _matchesSummaryAgainst.value = value
            }
        }
    }
    // TODO: Add to ui
    fun statsWith(playerId: Int) {
        viewModelScope.launch {
            playerStatisticsUseCase.statsWith(playerId).collect { value ->
                _matchesSummaryWith.value = value
            }
        }
    }
    fun winRateAgainst(playerId: Int) {
        viewModelScope.launch {
            playerStatisticsUseCase.winRateAgainst(playerId).collect { value ->
                _winRateAgainst.value = value
            }
        }
    }
}