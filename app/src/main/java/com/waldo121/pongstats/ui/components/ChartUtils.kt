package com.waldo121.pongstats.ui.components

import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun xLabelFormatter(
    allXValues: List<Float>,
): CartesianValueFormatter {
    // Format the tick as a date only if it exactly matches a data point
    return CartesianValueFormatter { _,value ,_ ->
        val nearest = allXValues.minByOrNull { kotlin.math.abs(it - value) }
        if (nearest != null) {
            val date = LocalDate.ofEpochDay(nearest.toLong())
            val result = date.format(DateTimeFormatter.ofPattern("MM-yyyy"))
            result
        } else ""
    }
}
