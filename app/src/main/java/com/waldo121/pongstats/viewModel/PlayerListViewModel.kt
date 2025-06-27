package com.waldo121.pongstats.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlayerListViewModel(
    private val repository: MatchRecordRepository
) : ViewModel() {
    private val _playerNames = MutableStateFlow<List<String>>(emptyList())
    val playerNames: StateFlow<List<String>> = _playerNames.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllUniquePlayerNames().collect { names ->
                _playerNames.value = names
            }
        }
    }
} 