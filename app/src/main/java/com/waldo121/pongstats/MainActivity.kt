@file:OptIn(ExperimentalMaterial3Api::class)

package com.waldo121.pongstats

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.waldo121.pongstats.data.local.MatchRecordsDatabase
import com.waldo121.pongstats.data.model.Player
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import com.waldo121.pongstats.domain.DoubleMatchRecordsUseCase
import com.waldo121.pongstats.domain.GlobalStatisticsUseCase
import com.waldo121.pongstats.domain.MatchesSummary
import com.waldo121.pongstats.domain.PlayerStatisticsUseCase
import com.waldo121.pongstats.domain.PlayerUseCase
import com.waldo121.pongstats.domain.SingleMatchRecordsUseCase
import com.waldo121.pongstats.domain.StatisticsModelUseCase
import com.waldo121.pongstats.ui.screens.PlayerListScreen
import com.waldo121.pongstats.ui.screens.PlayerProfileScreen
import com.waldo121.pongstats.ui.theme.PingPongBlack
import com.waldo121.pongstats.ui.theme.PingPongDarkGrey
import com.waldo121.pongstats.ui.theme.PingPongDarkRed
import com.waldo121.pongstats.ui.theme.PingPongRed
import com.waldo121.pongstats.ui.theme.PingPongWood
import com.waldo121.pongstats.ui.theme.PongStatsTheme
import com.waldo121.pongstats.ui.utils.QueryState
import com.waldo121.pongstats.ui.utils.xLabelFormatter
import com.waldo121.pongstats.viewModel.MatchRecordViewModel
import com.waldo121.pongstats.viewModel.PlayerViewModel
import com.waldo121.pongstats.viewModel.StatisticsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import kotlin.math.round


const val TAG: String = "MyActivity"

class MainActivity : ComponentActivity() {

    private lateinit var appDatabase: MatchRecordsDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDatabase = MatchRecordsDatabase.getDatabase(this)
        enableEdgeToEdge()
        setContent {
            App(
                modifier = Modifier,
                MatchRecordRepository(appDatabase, Dispatchers.IO)
            )
        }
    }
}

@Composable
fun App(
    modifier: Modifier = Modifier,
    matchRecordRepository: MatchRecordRepository
) {
    val matchRecordViewModel: MatchRecordViewModel = viewModel(
        factory = MatchRecordViewModel.Factory,
        extras = MutableCreationExtras().apply {
            set(MatchRecordViewModel.SINGLE_MATCH_USE_CASE_KEY, SingleMatchRecordsUseCase(matchRecordRepository))
            set(MatchRecordViewModel.DOUBLE_MATCH_USE_CASE_KEY, DoubleMatchRecordsUseCase(matchRecordRepository))
        }
    )
    val statisticsViewModel: StatisticsViewModel = viewModel(
        factory = StatisticsViewModel.Factory,
        extras = MutableCreationExtras().apply {
            set(StatisticsViewModel.GLOBAL_STATS_USE_CASE_KEY, GlobalStatisticsUseCase(
                SingleMatchRecordsUseCase(matchRecordRepository),
                DoubleMatchRecordsUseCase(matchRecordRepository),
                statisticsModelUseCase = StatisticsModelUseCase()
            ))
            set(StatisticsViewModel.PLAYER_STATS_USE_CASE_KEY, PlayerStatisticsUseCase(
                SingleMatchRecordsUseCase(matchRecordRepository),
                DoubleMatchRecordsUseCase(matchRecordRepository),
                statisticsModelUseCase = StatisticsModelUseCase()
            ))
        }
    )
    val playerViewModel: PlayerViewModel = viewModel(
        factory = PlayerViewModel.Factory,
        extras = MutableCreationExtras().apply {
            set(PlayerViewModel.PLAYER_USE_CASE_KEY, PlayerUseCase(matchRecordRepository))
        }
    )
    var selectedTab by remember { mutableIntStateOf(0) }

    PongStatsTheme {
        val navController = rememberNavController()
        Scaffold(
            modifier = modifier,
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = stringResource(R.string.statistics)) },
                        label = { Text(stringResource(R.string.statistics)) },
                        selected = selectedTab == 0,
                        onClick = {
                            selectedTab = 0
                            navController.navigate("main_tabs") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PingPongRed,
                            selectedTextColor = PingPongRed,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Add, contentDescription = stringResource(R.string.session)) },
                        label = { Text(stringResource(R.string.session)) },
                        selected = selectedTab == 1,
                        onClick = {
                            selectedTab = 1
                            navController.navigate("main_tabs") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PingPongRed,
                            selectedTextColor = PingPongRed,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = stringResource(R.string.player_list_title)) },
                        label = { Text(stringResource(R.string.player_list_title)) },
                        selected = selectedTab == 2,
                        onClick = {
                            selectedTab = 2
                            navController.navigate("main_tabs") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
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
            NavHost(
                navController = navController,
                startDestination = "main_tabs"
            ) {
                composable("main_tabs") {
                    when (selectedTab) {
                        0 -> {
                            HomeScreen(
                                modifier = Modifier.padding(innerPadding),
                                viewModel = statisticsViewModel,
                            )
                        }
                        1 -> {
                            SessionScreen(
                                modifier = Modifier.padding(innerPadding),
                                matchRecordViewModel = matchRecordViewModel,
                                playerViewModel = playerViewModel
                            )
                        }
                        2 -> {

                            val players = playerViewModel.players.collectAsStateWithLifecycle().value
                            PlayerListScreen(
                                players = players,
                                onPlayerSelected = { playerId ->
                                    navController.navigate("player_profile/" + playerId)
                                },
                                playerViewModel = playerViewModel
                            )
                        }
                    }
                }
                composable(
                    "player_profile/{playerId}",
                    arguments = listOf(navArgument("playerId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val playerId = backStackEntry.arguments?.getInt("playerId")?: 0

                    PlayerProfileScreen(
                        playerId = playerId,
                        statisticsViewModel = statisticsViewModel,
                        playerViewModel = playerViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
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
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Icon(
                painterResource(id = R.drawable.ic_main),
                contentDescription = stringResource(id = R.string.main_icon_description),
                tint = Color.Unspecified,
                modifier = Modifier.size(64.dp)
            )
        },
        modifier = modifier
    )
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel,
) {
    val singleData by viewModel.winRateSingle.collectAsStateWithLifecycle()
    val doubleData by viewModel.winRateDouble.collectAsStateWithLifecycle()
    val currentSingleWinRate by viewModel.matchesSummarySingle.collectAsStateWithLifecycle()
    val currentDoubleWinRate by viewModel.matchesSummaryDouble.collectAsStateWithLifecycle()
    
    val keySingle = singleData.hashCode()
    val keyDouble = doubleData.hashCode()
    val combinedModelProducer = remember(keySingle, keyDouble) { CartesianChartModelProducer() }
    val coroutineScope = rememberCoroutineScope()

    val singleSeriesX = singleData.map { Instant.ofEpochMilli(it.date.time).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay().toFloat() }
    val singleSeriesY = singleData.map { it.winRate }

    val doubleSeriesX = doubleData.map { Instant.ofEpochMilli(it.date.time).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay().toFloat() }
    val doubleSeriesY = doubleData.map { it.winRate }

    // Collect all unique x-values in order
    val allXValues = (singleSeriesX + doubleSeriesX).distinct().sorted()

    // Determine which series are present and in what order
    val presentSeries = mutableListOf<String>()
    if (singleSeriesY.isNotEmpty()) presentSeries.add("singles")
    if (doubleSeriesY.isNotEmpty()) presentSeries.add("doubles")

    LaunchedEffect(keySingle, keyDouble) {
        coroutineScope.launch(Dispatchers.Default) {
            combinedModelProducer.runTransaction {
                lineSeries {
                    if (singleSeriesX.isNotEmpty() && singleSeriesY.isNotEmpty()) {
                        series(x = singleSeriesX, y = singleSeriesY)
                    }
                    if (doubleSeriesX.isNotEmpty() && doubleSeriesY.isNotEmpty()) {
                        series(x = doubleSeriesX, y = doubleSeriesY)
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Title for current win rates
        Text(
            text = stringResource(R.string.current_win_rate),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = TextAlign.Center
        )

        // Win rates side by side
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Singles Current Win Rate
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.singles_match),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                CurrentWinRateCard(
                    matchesSummary = currentSingleWinRate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }

            // Doubles Current Win Rate
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.doubles_match),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                CurrentWinRateCard(
                    matchesSummary = currentDoubleWinRate,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Combined Chart
        if (singleData.isNotEmpty() || doubleData.isNotEmpty()) {
            WinRateChart(
                modelProducer = combinedModelProducer,
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                title = stringResource(R.string.daily_win_rate),
                showLegend = true,
                presentSeries = presentSeries,
                allXValues = allXValues,
            )
        } else {
            EmptyChartPlaceholder(
                title = stringResource(R.string.daily_win_rate),
                message = stringResource(R.string.no_data)
            )
        }
    }
}

@Composable
private fun CurrentWinRateCard(
    matchesSummary: MatchesSummary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${round(matchesSummary.winRate).toInt()}%",
            style = MaterialTheme.typography.headlineLarge,
            color = PingPongRed,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun WinRateChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
    title: String,
    showLegend: Boolean = false,
    presentSeries: List<String>,
    allXValues: List<Float>,
) {
    val singlesColor = PingPongRed
    val doublesColor = PingPongDarkRed
    val scrollState = rememberVicoScrollState(scrollEnabled = false)
    val lines = presentSeries.map { series ->
        when (series) {
            "singles" -> LineCartesianLayer.Line(
                fill = LineCartesianLayer.LineFill.single(fill(singlesColor)),
                stroke = LineCartesianLayer.LineStroke.Continuous(2f)
            )
            "doubles" -> LineCartesianLayer.Line(
                fill = LineCartesianLayer.LineFill.single(fill(doublesColor)),
                stroke = LineCartesianLayer.LineStroke.Continuous(2f)
            )
            else -> error("Unknown series type")
        }
    }
    val lineProvider = LineCartesianLayer.LineProvider.series(*lines.toTypedArray())

    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally)
        )
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = lineProvider
                ),
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = { _, value, _ ->
                        "${round(value).toInt()}%"
                    }
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = xLabelFormatter(allXValues),
                    labelRotationDegrees = 40f,
                ),
            ),
            scrollState = scrollState,
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
        )

        if (showLegend) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // Singles Legend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(PingPongRed, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.singles_match),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                // Doubles Legend
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(PingPongDarkRed, shape = CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.doubles_match),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyChartPlaceholder(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MatchTypeDropdown(
    matchType: String,
    onMatchTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val options = listOf(
        stringResource(R.string.singles_match),
        stringResource(R.string.doubles_match)
    )
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        OutlinedTextField(
            value = matchType,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.match_type)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    Modifier.clickable { expanded = !expanded }
                )
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(),
            offset = DpOffset(0.dp, 0.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onMatchTypeSelected(option)
                        expanded = false
                    }
                )
            }
            // Make the whole text field clickable to open the dropdown
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { expanded = true }
            )
        }
    }
}

@Composable
fun PlayerSelectionField(
    onSelected: (Int) -> Unit,
    label: String,
    modifier: Modifier,
    playerViewModel: PlayerViewModel
) {
    var text by remember { mutableStateOf("") }
    val playersSearchResultUiState by playerViewModel.searchedPlayers.collectAsStateWithLifecycle()
    var showSuggestions by remember { mutableStateOf(true) }
    // Debounce filtering for both typing and deleting characters
    LaunchedEffect(text) {
        delay(600)
        playerViewModel.searchPlayers(text)
    }

    Column(modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                text = it
                showSuggestions = true
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = null,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = KeyboardOptions.Default.imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    showSuggestions = false
                }
            )
        )
        println(playersSearchResultUiState)
        when (playersSearchResultUiState) {
            is QueryState.Loading -> null
            is QueryState.Success -> {
                val searchResults = (playersSearchResultUiState as QueryState.Success<List<Player>>).data
                if (showSuggestions) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(vertical = 2.dp)
                            .heightIn(max = 200.dp)
                    ) {
                        items(searchResults) { player ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        text = player.name
                                        onSelected(player.id)
                                        showSuggestions = false
                                    }
                                    .padding(12.dp)
                            ) {
                                Text(player.name)
                            }
                        }
                    }
                }
            }
            is QueryState.Error -> null
        }

    }
}

@Composable
fun SessionScreen(
    modifier: Modifier = Modifier,
    matchRecordViewModel: MatchRecordViewModel,
    playerViewModel: PlayerViewModel
) {
    val uiState by matchRecordViewModel.sessionForm.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        MatchTypeDropdown(
            matchType = matchTypeToLocalized(uiState.matchType),
            onMatchTypeSelected = { selected ->
                matchRecordViewModel.updateMatchType(matchTypeFromLocalized(selected, context))
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        PlayerSelectionField(
            label = stringResource(R.string.opponent_name),
            modifier = Modifier.fillMaxWidth(),
            onSelected = { matchRecordViewModel.updateOpponent(it)},
            playerViewModel = playerViewModel,
        )
        if (uiState.matchType == DOUBLE_MATCH) {
            Spacer(modifier = Modifier.height(8.dp))
            PlayerSelectionField(
                label = stringResource(R.string.opponent_2_name),
                modifier = Modifier.fillMaxWidth(),
                onSelected = { matchRecordViewModel.updateOpponent2(it)},
                playerViewModel = playerViewModel,
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = if (uiState.numberOfWins == 0) "" else uiState.numberOfWins.toString(),
            onValueChange = {
                val value = it.toIntOrNull() ?: 0
                matchRecordViewModel.updateNumberOfWins(value)
            },
            label = { Text(stringResource(R.string.nb_victories)) },
            modifier = Modifier.fillMaxWidth(),
            isError = !matchRecordViewModel.isResultValid(uiState.numberOfWins, uiState.numberOfDefeats),
            supportingText = { Text(stringResource(R.string.hint_nb_victories)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PingPongRed,
                unfocusedBorderColor = PingPongDarkGrey,
                focusedLabelColor = PingPongRed,
                unfocusedLabelColor = PingPongDarkGrey
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = if (uiState.numberOfDefeats == 0) "" else uiState.numberOfDefeats.toString(),
            onValueChange = {
                val value = it.toIntOrNull() ?: 0
                matchRecordViewModel.updateNumberOfDefeats(value)
            },
            label = { Text(stringResource(R.string.nb_defeats)) },
            modifier = Modifier.fillMaxWidth(),
            isError = !matchRecordViewModel.isResultValid(uiState.numberOfWins,uiState.numberOfDefeats),
            supportingText = { Text(stringResource(R.string.hint_nb_victories)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PingPongRed,
                unfocusedBorderColor = PingPongDarkGrey,
                focusedLabelColor = PingPongRed,
                unfocusedLabelColor = PingPongDarkGrey
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        val snackbarHostState = remember { SnackbarHostState() }

        // Snackbar host is moved outside the Box to be attached to the screen container
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth()
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    try {
                        matchRecordViewModel.createMatchRecord()
                    } catch (e: Exception) {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.session_save_error),
                                duration = SnackbarDuration.Short
                            )
                        }
                        Log.e(TAG,"Unable to create match record")
                    } finally {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.save_success),
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.Center),
                enabled = matchRecordViewModel.isFormDataValid(uiState),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PingPongDarkRed,
                    contentColor = Color.White,
                    disabledContainerColor = PingPongBlack,
                    disabledContentColor = Color.White.copy(alpha = 0.6f)
                )
            ) {
                Text(
                    text = stringResource(R.string.action_save),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun matchTypeToLocalized(matchType: String): String {
    return if (matchType == SINGLE_MATCH) {
        stringResource(R.string.singles_match)
    } else {
        stringResource(R.string.doubles_match)
    }
}

fun matchTypeFromLocalized(matchType: String, context: Context): String {
    return if (matchType == context.resources.getString(R.string.singles_match)) {
        SINGLE_MATCH
    } else {
        DOUBLE_MATCH
    }
}