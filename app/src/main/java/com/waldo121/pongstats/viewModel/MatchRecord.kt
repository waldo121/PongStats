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
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

data class MatchRecordUiState(
    val matchType: String = SINGLE_MATCH,
    val numberOfWins: Int = 0,
    val numberOfDefeats: Int = 0,
    val opponentName: String = "",
    val opponent2Name: String = "",
)

class MatchRecordViewModel(
    private val matchRecordRepository: MatchRecordRepository,
): ViewModel() {

    private val _uiState = MutableStateFlow(MatchRecordUiState())
    val uiState: StateFlow<MatchRecordUiState> = _uiState.asStateFlow()

    companion object {
        val MATCH_RECORD_REPOSITORY_KEY = object: CreationExtras.Key<MatchRecordRepository> {}
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repo =  this[MATCH_RECORD_REPOSITORY_KEY] as MatchRecordRepository
                MatchRecordViewModel(
                    matchRecordRepository = repo
                )
            }
        }
    }

    fun updateMatchType(matchType: String) {
        _uiState.update { currentState ->
            currentState.copy(
                matchType = matchType
            )
        }

    }
    fun updateNumberOfWins(numberOfWins: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                numberOfWins = numberOfWins
            )
        }
    }
    fun updateNumberOfDefeats(numberOfDefeats: Int) {

        _uiState.update { currentState ->
            currentState.copy(
                numberOfDefeats = numberOfDefeats
            )
        }
    }
    fun updateOpponentName(opponentName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                opponentName = opponentName
            )
        }

    }
    fun updateOpponent2Name(opponentName: String) {
        _uiState.update { currentState ->
            currentState.copy(
                opponent2Name = opponentName
            )
        }

    }
    fun createMatchRecord() {
        if (_uiState.value.matchType == SINGLE_MATCH) {
            val singleMatchRecord = SingleMatchRecord(
                numberOfWins = _uiState.value.numberOfWins,
                numberOfDefeats = _uiState.value.numberOfDefeats,
                opponentName = _uiState.value.opponentName,
                date = Date()
            )
            viewModelScope.launch(Dispatchers.IO) {
                matchRecordRepository.createSingleMatchRecord(singleMatchRecord)
            }
        } else if (_uiState.value.matchType == DOUBLE_MATCH) {
            val doubleMatchRecord = DoubleMatchRecord(
                numberOfWins = _uiState.value.numberOfWins,
                numberOfDefeats = _uiState.value.numberOfDefeats,
                opponent1Name = _uiState.value.opponentName,
                opponent2Name = _uiState.value.opponent2Name,
                date = Date()
            )
            viewModelScope.launch(Dispatchers.IO) {
                matchRecordRepository.createDoubleMatchRecord(doubleMatchRecord)
            }
        }
        reset()
    }
    fun isNameValid(name: String): Boolean {
        return name.isNotBlank() && name.isNotEmpty() && name.all{ it.isLetter() }
    }
    fun isResultValid(number: Int): Boolean {
        return number >= 0
    }
    fun isTotalValid(numberW: Int, numberD: Int): Boolean {
        return numberW+numberD > 0
    }
    fun isFormDataValid(uiState: MatchRecordUiState): Boolean {
        if (
            _uiState.value.matchType == SINGLE_MATCH
        )
            return isNameValid(uiState.opponentName) &&
                    isResultValid(uiState.numberOfWins) &&
                    isResultValid(uiState.numberOfDefeats) &&
                    isTotalValid(uiState.numberOfWins, uiState.numberOfDefeats)
        return isNameValid(uiState.opponentName) &&
                isNameValid(uiState.opponent2Name) &&
                isResultValid(uiState.numberOfWins) &&
                isResultValid(uiState.numberOfDefeats) &&
                isTotalValid(uiState.numberOfWins, uiState.numberOfDefeats)
    }
    private fun reset() {
        _uiState.update { currentState ->
            currentState.copy(
                opponentName = "",
                opponent2Name = "",
                numberOfWins = 0,
                numberOfDefeats = 0,
            )
        }
    }


}