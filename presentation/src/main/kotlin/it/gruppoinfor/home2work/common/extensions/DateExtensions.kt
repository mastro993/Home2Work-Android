package it.gruppoinfor.home2work.common.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.format(pattern: String): String {
    val simpleDate = SimpleDateFormat(pattern, Locale.ITALIAN)
    return simpleDate.format(this)
}