package com.waldo121.pongstats

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

data class NavigationItems(
    val label: String = "",
    val icon: ImageVector = Icons.Filled.Home,
    val route: String = ""
)

fun NavigationItems(): List<NavigationItems> {
    return listOf(
        NavigationItems
    )
}