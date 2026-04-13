package com.mrf.tghost.app.utils

import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

object NumberFormatterUtils {

    fun formatLargeNumber(number: Any?, decimalPlaces: Int = 1): String {
        val numDouble: Double = when (number) {
            is String -> number.toDoubleOrNull() ?: return number
            is Int -> number.toDouble()
            is Long -> number.toDouble()
            is Float -> number.toDouble()
            is Double -> number
            null -> return "0"
            else -> return number.toString()
        }

        if (numDouble.isNaN() || numDouble.isInfinite()) {
            return numDouble.toString()
        }

        val isNegative = numDouble < 0
        val absNum = abs(numDouble)

        if (absNum < 1000) {

            val df = if (absNum == absNum.toLong().toDouble() && decimalPlaces > 0) {
                DecimalFormat("#,##0")
            } else {
                DecimalFormat("#,##0.${"#".repeat(maxOf(0, decimalPlaces))}")
            }
            return (if (isNegative) "-" else "") + df.format(absNum)
        }

        val suffixes = arrayOf("", "k", "M", "B", "T")
        val magnitude = (ln(absNum) / ln(1000.0)).toInt()

        if (magnitude >= suffixes.size) {
            val df = DecimalFormat("0.${"#".repeat(maxOf(0, decimalPlaces))}E0")
            return (if (isNegative) "-" else "") + df.format(absNum)
        }

        val scaledValue = absNum / 1000.0.pow(magnitude.toDouble())

        val pattern = if (decimalPlaces <= 0) {
            "#,##0"
        } else {
            "#,##0.${"#".repeat(decimalPlaces)}"
        }
        val formatter = DecimalFormat(pattern)

        return (if (isNegative) "-" else "") + formatter.format(scaledValue) + suffixes[magnitude]
    }
}