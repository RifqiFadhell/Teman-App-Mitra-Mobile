package id.teman.app.mitra.domain.model.restaurant

import id.teman.app.mitra.common.orFalse
import id.teman.app.mitra.data.dto.restaurant.RestaurantHoursDto
import id.teman.app.mitra.data.dto.restaurant.TimeSlotDto
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantOpenHourSpec(
    val isOpen24Hour: Boolean,
    val isOpenForTheDay: Boolean,
    val dayOfWeekName: String,
    val openHours: List<OpenHoursSpec>,
    val apiDayOfWeekName: String
)

@Serializable
data class OpenHoursSpec(val startTime: String, val endTime: String)

fun RestaurantHoursDto.toRestaurantOpenHourListSpec(): List<RestaurantOpenHourSpec> {
    val sunday = sunday.orEmpty().toRestaurantOpenHourSpec("minggu")
    val monday = monday.orEmpty().toRestaurantOpenHourSpec("senin")
    val tuesday = tuesday.orEmpty().toRestaurantOpenHourSpec("selasa")
    val wednesday = wednesday.orEmpty().toRestaurantOpenHourSpec("rabu")
    val thursday = thursday.orEmpty().toRestaurantOpenHourSpec("kamis")
    val friday = friday.orEmpty().toRestaurantOpenHourSpec("jumat")
    val saturday = saturday.orEmpty().toRestaurantOpenHourSpec("sabtu")
    val restaurantOpenHours = listOf(sunday, monday, tuesday, wednesday, thursday, friday, saturday)
    return restaurantOpenHours.filter { it.isNotEmpty() }.flatten()
}

// always return 1 list, only start & end time that have multiple child
fun List<TimeSlotDto>.toRestaurantOpenHourSpec(dayName: String): List<RestaurantOpenHourSpec> {
    if (this.isEmpty()) return emptyList()

    // we only needs start time and end time as list. So iterate list in here only and take time only
    val openHours = this.map { item ->
        OpenHoursSpec(
            startTime = item.start.orEmpty(),
            endTime = item.end.orEmpty()
        )
    }
    return listOf(this[0].toRestaurantItemInfoSpec(this[0].day, dayName, openHours))
}

fun TimeSlotDto.toRestaurantItemInfoSpec(apiDayOfWeek: String?, dayOfWeekName: String, openHours: List<OpenHoursSpec>): RestaurantOpenHourSpec {
    return RestaurantOpenHourSpec(
        dayOfWeekName = dayOfWeekName,
        isOpenForTheDay = isOpen.orFalse(),
        isOpen24Hour = isFull.orFalse(),
        openHours = openHours,
        apiDayOfWeekName = apiDayOfWeek.orEmpty()
    )
}