package com.waldo121.pongstats.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.waldo121.pongstats.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import com.waldo121.pongstats.ui.theme.PingPongRed
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import com.waldo121.pongstats.TAG
import com.waldo121.pongstats.data.model.Player
import com.waldo121.pongstats.viewModel.PlayerViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
// Allows viewing player list and adding players
@Composable
fun PlayerListScreen(
    players: List<Player>,
    onPlayerSelected: (Int) -> Unit,
    playerViewModel: PlayerViewModel
) {
    var playerName by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val ctx = LocalContext.current

    // Snackbar host is moved here to be attached to the screen container
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = Modifier.fillMaxWidth()
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = stringResource(R.string.player_list_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            modifier = Modifier.padding(vertical = 12.dp),
            label = {
                Text(stringResource(R.string.player_name))
            },
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_player),
                    modifier = Modifier.clickable {
                        try {
                            playerViewModel.create(playerName)
                            playerName = ""
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    ctx.getString(R.string.player_creation_success),
                                    duration = SnackbarDuration.Short
                                )
                            }
                        } catch (_: Exception) {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(
                                    ctx.getString(R.string.player_creation_error),
                                    duration = SnackbarDuration.Short
                                )
                            }
                            Log.e(TAG, "Unable to create player")
                        }
                    }
                )
            },
            supportingText = {
                Text(
                    stringResource(R.string.hint_name),
                    modifier = Modifier.padding(4.dp)
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )
        if (players.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))
            players.forEach { player ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable(
                            onClick = { onPlayerSelected(player.id) }
                        )
                        .padding(vertical = 12.dp, horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PingPongRed,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = player.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
} 