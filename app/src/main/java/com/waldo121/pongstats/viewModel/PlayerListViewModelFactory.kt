package com.waldo121.pongstats.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.waldo121.pongstats.data.repository.MatchRecordRepository

class PlayerListViewModelFactory(
    private val repository: MatchRecordRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayerListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PlayerListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 