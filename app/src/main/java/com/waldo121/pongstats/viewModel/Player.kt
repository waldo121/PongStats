package com.waldo121.pongstats.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.waldo121.pongstats.data.model.Player
import com.waldo121.pongstats.domain.PlayerUseCase
import com.waldo121.pongstats.ui.utils.QueryState


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerUseCase: PlayerUseCase
) : ViewModel() {
    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()
    private val _selectedPlayerQueryState = MutableStateFlow<QueryState<Player>>(QueryState.Loading)
    val player: StateFlow<QueryState<Player>> = _selectedPlayerQueryState

    private val _searchedPlayersQueryState = MutableStateFlow<QueryState<List<Player>>>(QueryState.Loading)
    val searchedPlayers: StateFlow<QueryState<List<Player>>> = _searchedPlayersQueryState

    init {
        viewModelScope.launch {
            playerUseCase.list().collect { players ->
                _players.value = players
            }
        }
    }
    fun searchPlayers(name: String) {
         viewModelScope.launch {
             _searchedPlayersQueryState.value = QueryState.Loading
             try {
                 val playersResult = playerUseCase.search(name)
                 _searchedPlayersQueryState.value = QueryState.Success(playersResult)
             } catch (e: Exception) {
                 _selectedPlayerQueryState.value = QueryState.Error(e.message ?: "Unknown error")
             }
        }
    }
    fun create(name: String) {
         viewModelScope.launch {
             playerUseCase.create(name)
        }
    }
    fun get(id: Int) {
        viewModelScope.launch {
            _selectedPlayerQueryState.value = QueryState.Loading
            try {
                val player = playerUseCase.get(id)
                _selectedPlayerQueryState.value = QueryState.Success(player)
            } catch (e: Exception) {
                _selectedPlayerQueryState.value = QueryState.Error(e.message ?: "Unknown error")
            }
        }
    }
    companion object {
        val PLAYER_USE_CASE_KEY = object: CreationExtras.Key<PlayerUseCase> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val playerUseCase = this[PLAYER_USE_CASE_KEY] as PlayerUseCase
                PlayerViewModel(playerUseCase)
            }
        }
    }

}
