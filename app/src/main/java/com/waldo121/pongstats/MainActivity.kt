@file:OptIn(ExperimentalMaterial3Api::class)

package com.waldo121.pongstats

import DoubleMatchRecordsUseCase
import SingleMatchRecordsUseCase
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.waldo121.pongstats.data.local.MatchRecordsDatabase
import com.waldo121.pongstats.data.repository.MatchRecordRepository
import com.waldo121.pongstats.domain.CurrentWinRate
import com.waldo121.pongstats.domain.DailyWinRateUseCase
import com.waldo121.pongstats.ui.theme.PingPongBlack
import com.waldo121.pongstats.ui.theme.PingPongDarkGrey
import com.waldo121.pongstats.ui.theme.PingPongDarkRed
import com.waldo121.pongstats.ui.theme.PingPongLightWood
import com.waldo121.pongstats.ui.theme.PingPongRed
import com.waldo121.pongstats.ui.theme.PingPongWood
import com.waldo121.pongstats.ui.theme.PongStatsTheme
import com.waldo121.pongstats.viewModel.MatchRecordViewModel
import com.waldo121.pongstats.viewModel.StatisticsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.round
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import java.time.Instant

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
            set(StatisticsViewModel.DAILY_WIN_RATE_USE_CASE, DailyWinRateUseCase(matchRecordRepository))
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
                        onClick = { selectedTab = 1 },
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
                        viewModel = matchRecordViewModel,
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
    val singleData by viewModel.uiStateWinRateSingle.collectAsStateWithLifecycle()
    val doubleData by viewModel.uiStateWinRateDouble.collectAsStateWithLifecycle()
    val currentSingleWinRate by viewModel.currentWinRateSingle.collectAsStateWithLifecycle()
    val currentDoubleWinRate by viewModel.currentWinRateDouble.collectAsStateWithLifecycle()
    
    val keySingle = singleData.hashCode()
    val keyDouble = doubleData.hashCode()
    val combinedModelProducer = remember(keySingle, keyDouble) { CartesianChartModelProducer() }
    val coroutineScope = rememberCoroutineScope()

    val singleSeriesX = singleData.map { Instant.ofEpochMilli(it.date.time).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay().toFloat() }
    val singleSeriesY = singleData.map { it.winRate.toFloat() }

    val doubleSeriesX = doubleData.map { Instant.ofEpochMilli(it.date.time).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay().toFloat() }
    val doubleSeriesY = doubleData.map { it.winRate.toFloat() }

    // Collect all unique x-values in order
    val allXValues = (singleSeriesX + doubleSeriesX).distinct().sorted()

    // Dynamically calculate labelEvery so that at most maxLabels are shown
    val maxLabels = 7
    val labelEvery = if (allXValues.size <= maxLabels) 1 else (allXValues.size + maxLabels - 1) / maxLabels

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
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Win rates side by side
        androidx.compose.foundation.layout.Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly
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
                    currentWinRate = currentSingleWinRate,
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
                    currentWinRate = currentDoubleWinRate,
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
                modifier = Modifier.padding(vertical = 8.dp),
                title = stringResource(R.string.daily_win_rate),
                showLegend = true,
                presentSeries = presentSeries,
                allXValues = allXValues,
                labelEvery = labelEvery
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
    currentWinRate: CurrentWinRate,
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
            text = "${round(currentWinRate.winRate).toInt()}%",
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
    labelEvery: Int = 5
) {
    val singlesColor = PingPongRed
    val doublesColor = PingPongDarkRed

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
                    itemPlacer = HorizontalAxis.ItemPlacer.aligned(),
                    valueFormatter = { _, value, _ ->
                        val index = allXValues.indexOf(value.toFloat())
                        if (index >= 0 && index % labelEvery == 0) {
                            val date = LocalDate.ofEpochDay(value.toLong())
                            date.format(DateTimeFormatter.ofPattern("MMM, YYYY"))
                        } else {
                            ""
                        }
                    },
                    labelRotationDegrees = 30f
                ),
            ),
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
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
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
        Text(matchTypeToLocalized(uiState.matchType))
        ExposedDropdownMenuBox(
            expanded = matchTypeSelectorExpanded,
            onExpandedChange = { matchTypeSelectorExpanded = it },
        ) {
            TextField(
                value = matchTypeToLocalized(uiState.matchType),
                onValueChange = {},
                enabled = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(), //TODO: deprecated
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
            colors = OutlinedTextFieldDefaults.colors(
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PingPongRed,
                    unfocusedBorderColor = PingPongDarkGrey,
                    focusedLabelColor = PingPongRed,
                    unfocusedLabelColor = PingPongDarkGrey
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = if (uiState.numberOfWins == 0) "" else uiState.numberOfWins.toString(),
            onValueChange = {
                val value = it.toIntOrNull() ?: 0
                viewModel.updateNumberOfWins(value)
            },
            label = { Text(stringResource(R.string.nb_victories)) },
            modifier = Modifier.fillMaxWidth(),
            isError = !viewModel.isResultValid(uiState.numberOfWins),
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
                viewModel.updateNumberOfDefeats(value)
            },
            label = { Text(stringResource(R.string.nb_defeats)) },
            modifier = Modifier.fillMaxWidth(),
            isError = !viewModel.isResultValid(uiState.numberOfDefeats),
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
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        // Snackbar host is moved outside the Box to be attached to the screen container
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.fillMaxWidth()
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = {
                    val result = viewModel.createMatchRecord()
                    scope.launch {
                        if (result) {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.save_success),
                                duration = SnackbarDuration.Short
                            )
                        } else {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.save_error),
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .align(Alignment.Center),
                enabled = viewModel.isFormDataValid(uiState),
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