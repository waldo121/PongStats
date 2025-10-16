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
    private val _stats = MutableStateFlow(PlayerStatsUi(0, 0, 0, 0))
    val stats: StateFlow<PlayerStatsUi> = _stats.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAllSingleMatchRecords(),
                repository.getAllDoubleMatchRecords()
            ) { singleMatches, doubleMatches ->
                val single = singleMatches.filter { it.opponentName == playerName }
                val double = doubleMatches.filter { it.opponent1Name == playerName || it.opponent2Name == playerName }
                val totalWins = single.sumOf { it.numberOfWins } + double.sumOf {
                    when (playerName) {
                        it.opponent1Name -> it.numberOfWins
                        it.opponent2Name -> it.numberOfWins
                        else -> 0 //playerName is not matched
                    }
                }
                val totalDefeats = single.sumOf { it.numberOfDefeats } + double.sumOf {
                    when (playerName) {
                        it.opponent1Name -> it.numberOfWins
                        it.opponent2Name -> it.numberOfWins 
                        else -> 0 //playerName is not matched
                    }
                }
                val totalMatches = totalWins + totalDefeats
                val winRate = if (totalMatches > 0) (totalWins * 100 / totalMatches) else 0
                PlayerStatsUi(totalWins, totalDefeats, totalMatches, winRate)
            }.collect { _stats.value = it }
        }
    }
} 