package com.example.ui.components

import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToLong

object CurrencyFormatter {
    fun formatRand(amount: Double): String {
        val isNegative = amount < 0
        val positiveAmount = abs(amount)
        val wholePart = positiveAmount.toLong()
        val cents = ((positiveAmount - wholePart) * 100.0).roundToLong().coerceIn(0, 99)

        val wholeString = wholePart.toString()
            .reversed()
            .chunked(3)
            .joinToString(" ")
            .reversed()

        val sign = if (isNegative) "-" else ""
        return String.format(Locale.US, "%sR %s,%02d", sign, wholeString, cents)
    }

    fun formatCasesAndLoose(cases: Int, loose: Int): String {
        return when {
            cases > 0 && loose > 0 -> "$cases cases + $loose"
            cases > 0 -> "$cases cases"
            loose > 0 -> "$loose bottles"
            else -> "0 cases"
        }
    }
}
