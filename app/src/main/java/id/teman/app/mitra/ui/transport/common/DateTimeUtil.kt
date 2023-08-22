package id.teman.app.mitra.ui.transport.common

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun getChatCurrentTime(date: String?): String {
    if (date.isNullOrEmpty()) return ""
    val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    val formattedDate = apiFormat.parse(date)
    val format = SimpleDateFormat("HH:mm", Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("GMT+14")
    return formattedDate?.let { format.format(formattedDate) }.orEmpty()
}

fun getCurrentTimeFormat(date: String?, formatted: String = "EE, dd MMMM yyyy"): String {
    if (date.isNullOrEmpty()) return ""
    val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
    val formattedDate = apiFormat.parse(date)
    val format = SimpleDateFormat(formatted, Locale.getDefault())
    format.timeZone = TimeZone.getTimeZone("GMT+14")
    return formattedDate?.let { format.format(formattedDate) }.orEmpty()
}