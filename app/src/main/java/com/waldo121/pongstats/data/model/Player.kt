package com.waldo121.pongstats.data.model

import com.waldo121.pongstats.data.local.entities.PlayerEntity

data class Player (
    val id: Int,
    val name: String
)

fun Player.toEntity(): PlayerEntity = PlayerEntity(
    playerId = id,
    name = name,
)