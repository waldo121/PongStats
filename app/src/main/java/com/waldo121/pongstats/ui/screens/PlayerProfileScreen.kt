package com.waldo121.pongstats.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.waldo121.pongstats.R
import com.waldo121.pongstats.ui.components.xLabelFormatter
import com.waldo121.pongstats.ui.theme.PingPongDarkRed
import com.waldo121.pongstats.ui.theme.PingPongRed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.round

@Composable
fun PlayerProfileScreen(
    playerName: String,
    stats: PlayerStatsUi,
    onBack: () -> Unit,
    chartData: List<com.waldo121.pongstats.domain.ChartDataPoint> = emptyList(),
    modifier: Modifier = Modifier
) {
    val key = chartData.hashCode()
    val modelProducer = remember(key) { CartesianChartModelProducer() }
    val coroutineScope = rememberCoroutineScope()
    val seriesX = chartData.map { Instant.ofEpochMilli(it.date.time).atZone(ZoneId.systemDefault()).toLocalDate().toEpochDay().toFloat() }
    val seriesY = chartData.map { it.winRate.toFloat() }
    val allXValues = seriesX.distinct().sorted()
    val scrollState = rememberVicoScrollState(scrollEnabled = false)

    LaunchedEffect(key) {
        coroutineScope.launch(Dispatchers.Default) {
            modelProducer.runTransaction {
                lineSeries {
                    if (seriesX.isNotEmpty() && seriesY.isNotEmpty()) {
                        series(x = seriesX, y = seriesY)
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Profile header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(50),
                color = PingPongRed,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = playerName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        // Stats card
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.player_stats),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Victoires", style = MaterialTheme.typography.bodyMedium)
                        Text("${stats.totalWins}", color = PingPongRed, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Défaites", style = MaterialTheme.typography.bodyMedium)
                        Text("${stats.totalDefeats}", color = PingPongDarkRed, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Taux de victoire", style = MaterialTheme.typography.bodyMedium)
                        Text("${stats.winRate}%", color = PingPongRed, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        // Chart
        if (chartData.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.daily_win_rate),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                CartesianChartHost(
                    chart = rememberCartesianChart(
                        rememberLineCartesianLayer(),
                        startAxis = VerticalAxis.rememberStart(
                            valueFormatter = { _, value, _ ->
                                "${round(value).toInt()}%"
                            }
                        ),
                        bottomAxis = HorizontalAxis.rememberBottom(
                            valueFormatter = xLabelFormatter(allXValues),
                            labelRotationDegrees = 40f
                        ),
                    ),
                    scrollState = scrollState,
                    modelProducer = modelProducer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(56.dp))
    }
}

// Data class for UI stats
data class PlayerStatsUi(
    val totalWins: Int,
    val totalDefeats: Int,
    val winRate: Int
) 