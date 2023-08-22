package id.teman.app.mitra.common

import android.app.TimePickerDialog
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import java.text.DecimalFormat
import java.util.Calendar
import kotlin.math.roundToInt

fun MyTimePicker(context: Context, selectedDate: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val dialog = TimePickerDialog(
        context,
        { view, hoursOfDay, minutes ->
            val newHours = String.format("%02d", hoursOfDay)
            val newMinute = String.format("%2d", minutes)
            selectedDate("$newHours:$newMinute")
        },
        hour, minute, false
    )
    dialog.show()
}

fun dpToPx(resources: Resources, dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).roundToInt()
}

fun decimalFormat(value: Double) : String {
    val format = DecimalFormat("###.#")
    return format.format(value)
}