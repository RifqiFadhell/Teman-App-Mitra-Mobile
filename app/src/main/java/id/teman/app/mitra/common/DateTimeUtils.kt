package id.teman.app.mitra.common

import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone

fun String.convertUtcIso8601ToLocalTimeAgo(): String {
    val utcFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    val utcZoned = ZonedDateTime.parse(this, utcFormatter)
    val localFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val localZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault())
    val finalTime = localZoned.format(localFormatter)

    val now = ZonedDateTime.now()
    val days = now.toLocalDate().until(localZoned.toLocalDate(), ChronoUnit.DAYS)
    val dayOfWeek = localZoned.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val hour = if (localZoned.hour < 12) "AM" else "PM"

    return when (days) {
        0L -> "Hari ini $finalTime"
        1L -> "Kemarin $finalTime"
        in 2..6 -> "Hari $dayOfWeek, $finalTime"
        else ->{
            val fullDate = localZoned.format(DateTimeFormatter.ofPattern("d MMMM yyyy, HH:mm"))
            "$fullDate $hour"
        }
    }
}

fun String.convertToNotificationDate(): String {
    val utcFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    val utcZoned = ZonedDateTime.parse(this, utcFormatter)
    val localFormatter = DateTimeFormatter.ofPattern("dd MMM")
    val localZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault())
    return localZoned.format(localFormatter)
}

fun getOrderHistoryTimeFormat(date: String?, formatted: String = "EE, dd MMMM yyyy"): String {
    if (date == null) return ""
    val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val formattedDate = apiFormat.parse(date)
    val format = SimpleDateFormat(formatted, Locale.getDefault())
    format.timeZone = TimeZone.getDefault()
    return formattedDate?.let { format.format(formattedDate) }.orEmpty()
}

fun minutesToReadableText(minutes: Int): String {
    val hours = minutes / 60
    val remainingMinutes = minutes % 60
    val resultBuilder = StringBuilder()
    if (hours > 0) {
        resultBuilder.append("$hours jam")
    }
    if (remainingMinutes > 0) {
        resultBuilder.append("$minutes menit")
    }

    return resultBuilder.toString()
}