package com.waldo121.pongstats.data.repository

import com.waldo121.pongstats.data.local.MatchRecordsDatabase
import com.waldo121.pongstats.data.local.entities.DoubleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.PlayerEntity
import com.waldo121.pongstats.data.local.entities.SingleMatchRecordEntity
import com.waldo121.pongstats.data.local.entities.toDomain
import com.waldo121.pongstats.data.model.DoubleMatchRecord
import com.waldo121.pongstats.data.model.Player
import com.waldo121.pongstats.data.model.SingleMatchRecord
import com.waldo121.pongstats.data.model.toEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.sql.SQLException

class MatchRecordRepository(
    private val matchRecordLocalDatabase: MatchRecordsDatabase,
    private val dispatcher: CoroutineDispatcher
) {

    @Throws(SQLException::class)
    suspend fun createSingleMatchRecord(record: SingleMatchRecord) {
        withContext(dispatcher) {
            matchRecordLocalDatabase.singleMatchRecordDao().createRecord(
                record.toEntity()
            )
        }
    }

    @Throws(SQLException::class)
    fun getSingleMatchRecords(): Flow<List<SingleMatchRecord>> {
        return matchRecordLocalDatabase.singleMatchRecordDao().getAll()
            .map { entities: List<SingleMatchRecordEntity> -> entities.map { it.toDomain() } }
    }

    @Throws(SQLException::class)
    suspend fun createDoubleMatchRecord(record: DoubleMatchRecord) {
        withContext(dispatcher) {
            matchRecordLocalDatabase.doubleMatchRecordDao().createRecord(
                record.toEntity()
            )
        }
    }

    @Throws(SQLException::class)
    fun getDoubleMatchRecords(): Flow<List<DoubleMatchRecord>> {

            return matchRecordLocalDatabase.doubleMatchRecordDao().getAll()
                .map { entities: List<DoubleMatchRecordEntity> -> entities.map { it.toDomain() } }

    }

    @Throws(SQLException::class)
    fun getSingleMatchesAgainst(playerId: Int): Flow<List<SingleMatchRecord>> {

            return matchRecordLocalDatabase.singleMatchRecordDao().getMatchesAgainst(playerId)
                .map { entities -> entities.map { it.toDomain() } }

    }
    @Throws(SQLException::class)
    fun getDoubleMatchesAgainst(playerId: Int): Flow<List<DoubleMatchRecord>> {

        return matchRecordLocalDatabase.doubleMatchRecordDao().getMatchesAgainst(playerId)
                .map { entities -> entities.map { it.toDomain() } }

    }
    @Throws(SQLException::class)
    fun getDoubleMatchesWith(playerId: Int): Flow<List<DoubleMatchRecord>> {

            return matchRecordLocalDatabase.doubleMatchRecordDao().getMatchesWith(playerId)
                .map { entities -> entities.map { it.toDomain() } }

    }
    // TODO: Interrogate player table
    @Throws(SQLException::class)
    fun getPlayers(): Flow<List<Player>> {
        val players = matchRecordLocalDatabase.playerDao().getAll()
        return players.map { entities: List<PlayerEntity> -> entities.map { it.toDomain() } }
    }

    @Throws(SQLException::class)
    suspend fun createPlayer(playerName: String) {
        withContext(Dispatchers.IO) {
           matchRecordLocalDatabase.playerDao().createPlayer(PlayerEntity(playerId=0,name=playerName))
        }
    }

    @Throws(SQLException::class)
    suspend fun getPlayer(playerId: Int): Player {
        return withContext(dispatcher) {
            val player = matchRecordLocalDatabase.playerDao().getPlayer(playerId)
            return@withContext player.toDomain()
        }
    }

    @Throws(SQLException:: class)
    suspend fun searchPlayer(name: String): List<Player> {
        println("searched: $name")
        return withContext(dispatcher) {
            val players = matchRecordLocalDatabase.playerDao().search(name)
            println(players)
            return@withContext players.map { entity -> entity.toDomain() }
        }
    }
}
