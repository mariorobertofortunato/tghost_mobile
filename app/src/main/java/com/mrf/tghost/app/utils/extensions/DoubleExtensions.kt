package com.mrf.tghost.app.utils.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.pow
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun Double.maxDecimalPlaces(decimalPlaces: Int): Double {
    require(decimalPlaces >= 0) { "Decimal places must be non-negative." }

    if (decimalPlaces == 0) {
        return this
    }

    val factor = 10.0.pow(decimalPlaces.toDouble())
    val roundedValue = kotlin.math.round(this * factor) / factor
    return roundedValue
}

fun Double.maxDecimalPlacesString(decimalPlaces: Int): String {
    require(decimalPlaces >= 0) { "Decimal places must be non-negative." }

    if (this.isNaN()) return "0"

    return BigDecimal.valueOf(this)
        .setScale(decimalPlaces, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()
}

/**
 * Formatta un Double in stringa gestendo in modo intelligente i numeri tipici della blockchain.
 * - Numeri molto piccoli (es. 0.00000123) vengono mostrati con precisione sufficiente.
 * - Numeri "microscopici" (sotto una soglia minima) possono essere mostrati come "< 0.000001".
 * - Numeri grandi (es. > 1000) vengono formattati con separatori delle migliaia e pochi decimali.
 *
 * @param minDecimalPlaces Il numero minimo di decimali da mostrare.
 * @param maxDecimalPlaces Il numero massimo di decimali da mostrare (se il numero non è troppo piccolo).
 * @param smallNumberThreshold La soglia sotto la quale il numero viene trattato come "molto piccolo".
 */
fun Double.smartFormatAmount(
    minDecimalPlaces: Int = 2,
    maxDecimalPlaces: Int = 8,
    smallNumberThreshold: Double = 0.01
): String {
    if (this.isNaN()) return "0"
    if (this == 0.0) return "0"

    val absValue = kotlin.math.abs(this)

    val lowestVisibleValue = 1.0 / 10.0.pow(maxDecimalPlaces)
    if (absValue > 0 && absValue < lowestVisibleValue) {
        return "< ${DecimalFormat("0." + "#".repeat(maxDecimalPlaces), getDecimalSymbols()).format(lowestVisibleValue)}"
    }

    val symbols = getDecimalSymbols()
    val pattern = StringBuilder("#,##0")

    if (maxDecimalPlaces > 0) {
        pattern.append(".")
        if (absValue < smallNumberThreshold) {
            pattern.append("#".repeat(maxDecimalPlaces))
        } else {
            pattern.append("0".repeat(minDecimalPlaces))
            pattern.append("#".repeat((maxDecimalPlaces - minDecimalPlaces).coerceAtLeast(0)))
        }
    }

    val df = DecimalFormat(pattern.toString(), symbols)
    if (absValue >= 100_000) {
        df.maximumFractionDigits = 2
        df.minimumFractionDigits = 2
    }

    return df.format(this)
}

private fun getDecimalSymbols(): DecimalFormatSymbols {
    return DecimalFormatSymbols(Locale.US).apply {
        groupingSeparator = ',' // Migliaia
        decimalSeparator = '.'  // Decimali
    }
}