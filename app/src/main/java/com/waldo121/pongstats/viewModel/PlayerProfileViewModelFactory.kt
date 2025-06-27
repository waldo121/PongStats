package com.waldo121.pongstats.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waldo121.pongstats.data.repository.MatchRecordRepository

class PlayerProfileViewModelFactory(
    private val repository: MatchRecordRepository,
    private val playerName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerProfileViewModel(repository, playerName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 