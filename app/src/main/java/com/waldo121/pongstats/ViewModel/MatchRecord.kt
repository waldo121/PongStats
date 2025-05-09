package com.waldo121.pongstats.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.SingleMatchRecord
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class MatchRecordViewModel(
    private val matchRecordRepository: MatchRecordRepository
): ViewModel() {
    fun createSingleMatchRecord(
        numberOfWins: Int,
        numberOfDefeats: Int,
        opponentName: String,
    ) {
        val singleMatchRecord = SingleMatchRecord(
            numberOfWins = numberOfWins,
            numberOfDefeats = numberOfDefeats,
            opponentName = opponentName,
            date = Date()
        )
        viewModelScope.launch(Dispatchers.IO) {
            matchRecordRepository.createSingleMatchRecord(singleMatchRecord)
        }
    }
    fun createDoubleMatchRecord(
        numberOfWins: Int,
        numberOfDefeats: Int,
        opponent1Name: String,
        opponent2Name: String,
    ) {
        val doubleMatchRecord = DoubleMatchRecord(
            numberOfWins = numberOfWins,
            numberOfDefeats = numberOfDefeats,
            opponent1Name = opponent1Name,
            opponent2Name = opponent2Name,
            date = Date()
        )
        viewModelScope.launch(Dispatchers.IO) {
            matchRecordRepository.createDoubleMatchRecord(doubleMatchRecord)
        }
    }

}