@file:OptIn(ExperimentalMaterial3Api::class)

package com.waldo121.pongstats

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.waldo121.pongstats.data.local.MatchRecordsDatabase
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import com.waldo121.pongstats.ui.theme.PingPongBlack
import com.waldo121.pongstats.ui.theme.PingPongDarkGrey
import com.waldo121.pongstats.ui.theme.PingPongDarkRed
import com.waldo121.pongstats.ui.theme.PingPongLightWood
import com.waldo121.pongstats.ui.theme.PingPongRed
import com.waldo121.pongstats.ui.theme.PingPongWood
import com.waldo121.pongstats.ui.theme.PongStatsTheme
import com.waldo121.pongstats.viewModel.MatchRecordViewModel


class MainActivity : ComponentActivity() {
    private lateinit var appDatabase: MatchRecordsDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = MatchRecordsDatabase.getDatabase(this)
        enableEdgeToEdge()
        setContent {
            App(
                modifier = Modifier,
                MatchRecordRepository(appDatabase)
            )
        }
    }
}

@Composable
fun App(
    modifier: Modifier = Modifier,
    matchRecordRepository: MatchRecordRepository
) {
    val viewModel: MatchRecordViewModel = viewModel(
        factory = MatchRecordViewModel.Factory,
        extras = MutableCreationExtras().apply {
            set(MatchRecordViewModel.MATCH_RECORD_REPOSITORY_KEY, matchRecordRepository)
        }
    )
    var selectedTab by remember { mutableIntStateOf(0) }
    
    PongStatsTheme {
        Scaffold(
            modifier = modifier,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.statististics)) },
                        label = { Text(stringResource(R.string.statististics)) },
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = PingPongRed,
                            selectedTextColor = PingPongRed,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.session)) },
                        label = { Text(stringResource(R.string.session)) },
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = PingPongRed,
                            selectedTextColor = PingPongRed,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.options)) },
                        label = { Text(stringResource(R.string.options)) },
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = PingPongRed,
                            selectedTextColor = PingPongRed,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            },
            topBar = { AppTopBar(Modifier) },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            when (selectedTab) {
                0 -> {
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Home Screen")
                    }
                }
                1 -> {
                    SessionScreen(
                        modifier = modifier.padding(innerPadding),
                        viewModel = viewModel,
                    )
                }
                2 -> {
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Settings Screen")
                    }
                }
            }
        }
    }
}

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = PingPongWood,
            titleContentColor = PingPongBlack,
        ),
        title = {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "PongStats",
                tint = PingPongBlack
            )
        },
        modifier = modifier
    )
}

@Composable
fun SessionScreen(
    modifier: Modifier = Modifier,
    viewModel: MatchRecordViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var matchTypeSelectorExpanded by remember { mutableStateOf(false) }
    val options: List<String> = listOf(stringResource(R.string.singles_match), stringResource(R.string.doubles_match))
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        
        ExposedDropdownMenuBox(
            expanded = matchTypeSelectorExpanded,
            onExpandedChange = { matchTypeSelectorExpanded = it },
        ) {
            TextField(
                value = matchTypeToLocalized(uiState.matchType),
                onValueChange = {},
                enabled = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                minLines = 1,
                maxLines = 1,
                label = { Text(stringResource(R.string.match_type)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = matchTypeSelectorExpanded) },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = PingPongLightWood,
                    unfocusedContainerColor = PingPongLightWood,
                    focusedLabelColor = PingPongRed,
                    unfocusedLabelColor = PingPongDarkGrey
                ),
                singleLine = true,
            )
            ExposedDropdownMenu(
                expanded = matchTypeSelectorExpanded,
                onDismissRequest = { matchTypeSelectorExpanded = false },
            ) {
                val context = LocalContext.current
                options.forEach { option ->
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text(option, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            viewModel.updateMatchType(matchTypeFromLocalized(option, context))
                            matchTypeSelectorExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = uiState.opponentName,
            onValueChange = { viewModel.updateOpponentName(it) },
            label = { Text(stringResource(R.string.opponent_name)) },
            modifier = Modifier.fillMaxWidth(),
            isError = !viewModel.isNameValid(uiState.opponentName),
            supportingText = { Text(stringResource(R.string.hint_name)) },
            singleLine = true,
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PingPongRed,
                unfocusedBorderColor = PingPongDarkGrey,
                focusedLabelColor = PingPongRed,
                unfocusedLabelColor = PingPongDarkGrey
            )
        )
        
        if (uiState.matchType == DOUBLE_MATCH) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = uiState.opponent2Name,
                onValueChange = { viewModel.updateOpponent2Name(it) },
                label = { Text(stringResource(R.string.opponent_2_name)) },
                modifier = Modifier.fillMaxWidth(),
                isError = !viewModel.isNameValid(uiState.opponent2Name),
                supportingText = { Text(stringResource(R.string.hint_name)) },
                singleLine = true,
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PingPongRed,
                    unfocusedBorderColor = PingPongDarkGrey,
                    focusedLabelColor = PingPongRed,
                    unfocusedLabelColor = PingPongDarkGrey
                )
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = uiState.numberOfWins.toString(),
            onValueChange = {
                if (it.isNotEmpty()) viewModel.updateNumberOfWins(it.toInt()) else viewModel.updateNumberOfWins(
                    0
                )
            },
            label = { Text(stringResource(R.string.nb_victories)) },
            modifier = Modifier.fillMaxWidth(),
            isError = !viewModel.isResultValid(uiState.numberOfWins),
            supportingText = { Text(stringResource(R.string.hint_nb_victories)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PingPongRed,
                unfocusedBorderColor = PingPongDarkGrey,
                focusedLabelColor = PingPongRed,
                unfocusedLabelColor = PingPongDarkGrey
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = uiState.numberOfDefeats.toString(),
            onValueChange = {
                if (it.isNotEmpty()) viewModel.updateNumberOfDefeats(it.toInt()) else viewModel.updateNumberOfDefeats(
                    0
                )
            },
            label = { Text(stringResource(R.string.nb_defeats)) },
            modifier = Modifier.fillMaxWidth(),
            isError = !viewModel.isResultValid(uiState.numberOfDefeats),
            supportingText = { Text(stringResource(R.string.hint_nb_victories)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PingPongRed,
                unfocusedBorderColor = PingPongDarkGrey,
                focusedLabelColor = PingPongRed,
                unfocusedLabelColor = PingPongDarkGrey
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                viewModel.createMatchRecord()
            },
            modifier = Modifier.fillMaxWidth(0.7f),
            enabled = viewModel.isFormDataValid(uiState),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = PingPongDarkRed,
                contentColor = androidx.compose.ui.graphics.Color.White,
                disabledContainerColor = PingPongBlack,
                disabledContentColor = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.6f)
            )
        ) {
            Text(
                text = stringResource(R.string.action_save),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun matchTypeToLocalized(matchType: String): String {
    return if (matchType == SINGLE_MATCH) {
        stringResource(R.string.singles_match)
    }
    else stringResource(R.string.doubles_match)
}

fun matchTypeFromLocalized(matchType: String, context: Context): String {
    return if (matchType == context.resources.getString(R.string.singles_match)) {
        SINGLE_MATCH
    }
    else DOUBLE_MATCH
}
