package com.waldo121.pongstats.domain

import android.util.Log
import com.waldo121.pongstats.data.model.Player
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

class PlayerUseCase (
    private val matchRecordRepository: MatchRecordRepository
) {
    suspend fun search(playerName: String): List<Player> = coroutineScope {
        val request = async {matchRecordRepository.searchPlayer(playerName) }
        request.await()
    }

    suspend fun get(playerId: Int): Player = coroutineScope {
       val request =  async { matchRecordRepository.getPlayer(playerId) }
        request.await()
    }

    fun validatePlayerName(name: String): Boolean {
        return name.all { c -> c.isLetter() || c == '-' } && name.length > 1
    }
    suspend fun create(playerMame: String) {
        if (!validatePlayerName(playerMame)) {
            Log.e("PlayerUseCase.","Invalid player name")
            throw Exception("Invalid player name")
        }
        matchRecordRepository.createPlayer(playerMame)
    }

    fun list(): Flow<List<Player>> {
        return matchRecordRepository.getPlayers()
    }
}