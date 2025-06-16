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
import com.waldo121.pongstats.domain.CurrentWinRate
import com.waldo121.pongstats.domain.DailyWinRateUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StatisticsViewModel(
    private val dailyWinRateUseCase: DailyWinRateUseCase
) : ViewModel() {

    private val _uiStateWinRateSingle = MutableStateFlow<List<ChartDataPoint>>(emptyList())
    val uiStateWinRateSingle: StateFlow<List<ChartDataPoint>> = _uiStateWinRateSingle
    
    private val _uiStateWinRateDouble = MutableStateFlow<List<ChartDataPoint>>(emptyList())
    val uiStateWinRateDouble: StateFlow<List<ChartDataPoint>> = _uiStateWinRateDouble

    private val _currentWinRateSingle = MutableStateFlow(CurrentWinRate(0, 0, 0f))
    val currentWinRateSingle: StateFlow<CurrentWinRate> = _currentWinRateSingle

    private val _currentWinRateDouble = MutableStateFlow(CurrentWinRate(0, 0, 0f))
    val currentWinRateDouble: StateFlow<CurrentWinRate> = _currentWinRateDouble

    companion object {
        val DAILY_WIN_RATE_USE_CASE = object : CreationExtras.Key<DailyWinRateUseCase> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val dailyWinRateUseCase = this[DAILY_WIN_RATE_USE_CASE] as DailyWinRateUseCase
                StatisticsViewModel(
                    dailyWinRateUseCase = dailyWinRateUseCase
                )
            }
        }
    }

    init {
        // Collect singles data
        viewModelScope.launch(Dispatchers.Default) {
            dailyWinRateUseCase(SINGLE_MATCH)
                .collect { winRateData ->
                    _uiStateWinRateSingle.value = winRateData
                }
        }

        // Collect doubles data in a separate coroutine
        viewModelScope.launch(Dispatchers.Default) {
            dailyWinRateUseCase(DOUBLE_MATCH)
                .collect { winRateData ->
                    _uiStateWinRateDouble.value = winRateData
                }
        }

        // Collect current singles win rate
        viewModelScope.launch(Dispatchers.Default) {
            dailyWinRateUseCase.getCurrentWinRate(SINGLE_MATCH)
                .collect { currentWinRate ->
                    _currentWinRateSingle.value = currentWinRate
                }
        }

        // Collect current doubles win rate
        viewModelScope.launch(Dispatchers.Default) {
            dailyWinRateUseCase.getCurrentWinRate(DOUBLE_MATCH)
                .collect { currentWinRate ->
                    _currentWinRateDouble.value = currentWinRate
                }
        }
    }
}