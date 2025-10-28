package com.waldo121.pongstats.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waldo121.pongstats.data.model.SingleMatchRecord
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import com.waldo121.pongstats.ui.screens.PlayerStatsUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted



class PlayerProfileViewModel(
    private val repository: MatchRecordRepository,
    private val playerName: String
) : ViewModel() {
        val stats: StateFlow<PlayerStatsUi> = repository.getAllSingleMatchRecords()
        .map { singleMatches ->
            val single = singleMatches.filter { it.opponentName == playerName }
            val totalWins = single.sumOf { it.numberOfWins }
            val totalDefeats = single.sumOf { it.numberOfDefeats }
            val totalMatches = totalWins + totalDefeats
            val winRate = if (totalMatches > 0) (totalWins * 100 / totalMatches) else 0
            PlayerStatsUi(totalWins, totalDefeats, winRate)
        }
        .stateIn(
            scope = viewModelScope,
            started = kotlinx.coroutines.flow.SharingStarted.Lazily,
            initialValue = PlayerStatsUi(0, 0, 0)
        )
} 