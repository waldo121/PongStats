package com.waldo121.pongstats.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.waldo121.pongstats.DOUBLE_MATCH
import com.waldo121.pongstats.SINGLE_MATCH
import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import com.waldo121.pongstats.domain.DoubleMatchRecordsUseCase
import com.waldo121.pongstats.domain.SingleMatchRecordsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.Int

data class SessionFormState(
    val matchType: String = SINGLE_MATCH,
    val numberOfWins: Int = 0,
    val numberOfDefeats: Int = 0,
    val opponentId: Int = 0,
    val opponent2Id: Int = 0,
    val teammateId: Int = 0
)

class MatchRecordViewModel(
    private val singleMatchRecordsUseCase: SingleMatchRecordsUseCase,
    private val doubleMatchRecordsUseCase: DoubleMatchRecordsUseCase
): ViewModel() {

    private val _sessionForm = MutableStateFlow(SessionFormState())
    val sessionForm: StateFlow<SessionFormState> = _sessionForm.asStateFlow()

    companion object {
        val SINGLE_MATCH_USE_CASE_KEY = object : CreationExtras.Key<SingleMatchRecordsUseCase> {}
        val DOUBLE_MATCH_USE_CASE_KEY = object : CreationExtras.Key<DoubleMatchRecordsUseCase> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val singleUseCase = this[SINGLE_MATCH_USE_CASE_KEY] as SingleMatchRecordsUseCase
                val doubleUseCase = this[DOUBLE_MATCH_USE_CASE_KEY] as DoubleMatchRecordsUseCase
                MatchRecordViewModel(singleUseCase, doubleUseCase)
            }
        }
    }

    fun updateMatchType(matchType: String) {
        _sessionForm.update { currentState ->
            currentState.copy(
                matchType = matchType
            )
        }

    }
    fun updateNumberOfWins(numberOfWins: Int) {
        _sessionForm.update { currentState ->
            currentState.copy(
                numberOfWins = numberOfWins
            )
        }
    }
    fun updateNumberOfDefeats(numberOfDefeats: Int) {

        _sessionForm.update { currentState ->
            currentState.copy(
                numberOfDefeats = numberOfDefeats
            )
        }
    }
    fun updateOpponent(playerId: Int) {
        _sessionForm.update { currentState ->
            currentState.copy(
                opponentId = playerId
            )
        }

    }
    fun updateOpponent2(playerId: Int) {
        _sessionForm.update { currentState ->
            currentState.copy(
                opponent2Id = playerId
            )
        }

    }
    fun updateTeammate(playerId: Int) {
        _sessionForm.update { currentState ->
            currentState.copy(
                teammateId = playerId
            )
        }
    }
    fun createMatchRecord() {
            if (_sessionForm.value.matchType == SINGLE_MATCH) {
                val singleMatchRecord = SingleMatchRecord(
                    numberOfWins = _sessionForm.value.numberOfWins,
                    numberOfDefeats = _sessionForm.value.numberOfDefeats,
                    opponentId = _sessionForm.value.opponentId,
                    date = Date()
                )
                viewModelScope.launch(Dispatchers.IO) {
                    singleMatchRecordsUseCase.create(singleMatchRecord)
                }
            } else if (_sessionForm.value.matchType == DOUBLE_MATCH) {
                val doubleMatchRecord = DoubleMatchRecord(
                    numberOfWins = _sessionForm.value.numberOfWins,
                    numberOfDefeats = _sessionForm.value.numberOfDefeats,
                    opponent1Id = _sessionForm.value.opponentId,
                    opponent2Id = _sessionForm.value.opponent2Id,
                    teammateId = _sessionForm.value.teammateId,
                    date = Date()
                )
                viewModelScope.launch(Dispatchers.IO) {
                    doubleMatchRecordsUseCase.create(doubleMatchRecord)
                }
            }
            reset()
    }
    fun isResultValid(numberWins: Int, numberDefeats: Int): Boolean {
        return numberWins+numberDefeats > 0 && numberDefeats >= 0 && numberWins >= 0
    }
    fun arePlayerIdsValid(playerIds: List<Int>): Boolean {
        return playerIds.all { id -> id > 0 } && playerIds.distinct().size == playerIds.size
    }
    fun isFormDataValid(uiState: SessionFormState): Boolean {
        if (
            _sessionForm.value.matchType == SINGLE_MATCH
        )
            return  arePlayerIdsValid(listOf(uiState.opponentId)) &&
                    isResultValid(uiState.numberOfWins, uiState.numberOfDefeats)
        return  arePlayerIdsValid(listOf(uiState.opponentId, uiState.opponent2Id, uiState.teammateId)) &&
                isResultValid(uiState.numberOfWins, uiState.numberOfDefeats)
    }
    private fun reset() {
        _sessionForm.update { currentState ->
            currentState.copy(
                opponentId = 0,
                opponent2Id = 0,
                teammateId = 0,
                numberOfWins = 0,
                numberOfDefeats = 0,
            )
        }
    }
}