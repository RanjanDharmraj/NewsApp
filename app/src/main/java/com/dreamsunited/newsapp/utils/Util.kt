package com.dreamsunited.newsapp.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun changeDateFormat(date: String?): String? {
    val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
    val outputFormat: DateFormat = SimpleDateFormat("EEE, MMM dd hh:mm", Locale.getDefault())
    var date1: Date? = null
    try {
        date1 = inputFormat.parse(date)
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return outputFormat.format(date1)
}