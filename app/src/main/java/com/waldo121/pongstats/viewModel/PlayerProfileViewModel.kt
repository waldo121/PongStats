package com.waldo121.pongstats.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import com.waldo121.pongstats.ui.screens.PlayerStatsUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlayerProfileViewModel(
    private val repository: MatchRecordRepository,
    private val playerName: String
) : ViewModel() {
    private val _stats = MutableStateFlow(PlayerStatsUi(0, 0,  0))
    val stats: StateFlow<PlayerStatsUi> = _stats.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAllSingleMatchRecords()
            ) { singleMatches, doubleMatches ->
                val single = singleMatches.filter { it.opponentName == playerName }
                val totalWins = single.sumOf { it.numberOfWins } 
                val totalDefeats = single.sumOf { it.numberOfDefeats } 
                val totalMatches = totalWins + totalDefeats
                val winRate = if (totalMatches > 0) (totalWins * 100 / totalMatches) else 0
                PlayerStatsUi(totalWins, totalDefeats, winRate)
            }.collect { _stats.value = it }
        }
    }
} 