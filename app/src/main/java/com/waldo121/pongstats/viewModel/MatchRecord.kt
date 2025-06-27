package com.waldo121.pongstats.viewModel

import DoubleMatchRecordsUseCase
import SingleMatchRecordsUseCase
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.SQLException
import java.util.Date
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import kotlinx.coroutines.flow.collect

data class MatchRecordUiState(
    val matchType: String = SINGLE_MATCH,
    val numberOfWins: Int = 0,
    val numberOfDefeats: Int = 0,
    val opponentName: String = "",
    val opponent2Name: String = "",
)

class MatchRecordViewModel(
    private val singleMatchRecordsUseCase: SingleMatchRecordsUseCase,
    private val doubleMatchRecordsUseCase: DoubleMatchRecordsUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(MatchRecordUiState())
    val uiState: StateFlow<MatchRecordUiState> = _uiState.asStateFlow()

    private val _allPlayerNames = MutableStateFlow<List<String>>(emptyList())
    val allPlayerNames: StateFlow<List<String>> = _allPlayerNames.asStateFlow()

    private val _allSingleMatchRecords = MutableStateFlow<List<SingleMatchRecord>>(emptyList())
    val allSingleMatchRecords: StateFlow<List<SingleMatchRecord>> = _allSingleMatchRecords.asStateFlow()

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

    init {
        viewModelScope.launch {
            val repository = singleMatchRecordsUseCase.repository
            repository.getAllUniquePlayerNames().collect { names ->
                _allPlayerNames.value = names
            }
        }
        viewModelScope.launch {
            val repository = singleMatchRecordsUseCase.repository
            repository.getAllSingleMatchRecords().collect { records ->
                _allSingleMatchRecords.value = records
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
        if (opponentName != _uiState.value.opponentName) {
            _uiState.update { currentState ->
                currentState.copy(
                    opponent2Name = opponentName
                )
            }
        }
    }
    fun createMatchRecord(): Boolean {
        try {
            if (_uiState.value.matchType == SINGLE_MATCH) {
                val singleMatchRecord = SingleMatchRecord(
                    numberOfWins = _uiState.value.numberOfWins,
                    numberOfDefeats = _uiState.value.numberOfDefeats,
                    opponentName = _uiState.value.opponentName,
                    date = Date()
                )
                viewModelScope.launch(Dispatchers.IO) {
                    singleMatchRecordsUseCase.invoke(singleMatchRecord)
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
                    doubleMatchRecordsUseCase.invoke(doubleMatchRecord)
                }
            }
            reset()
            return true
        } catch (e: SQLException) {
            return false
        }
    }
    fun isNameValid(name: String): Boolean {
        return name.isNotBlank() && name.isNotEmpty() && name.all { it.isLetter() || it == '-' }
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