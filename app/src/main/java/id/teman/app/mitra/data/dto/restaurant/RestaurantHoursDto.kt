package id.teman.app.mitra.data.dto.restaurant

import id.teman.app.mitra.domain.model.restaurant.RestaurantOpenHourSpec
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RestaurantHoursDto(
    @SerialName("sunday") val sunday: List<TimeSlotDto>? = null,
    @SerialName("monday") val monday: List<TimeSlotDto>? = null,
    @SerialName("tuesday") val tuesday: List<TimeSlotDto>? = null,
    @SerialName("wednesday") val wednesday: List<TimeSlotDto>? = null,
    @SerialName("thursday") val thursday: List<TimeSlotDto>? = null,
    @SerialName("friday") val friday: List<TimeSlotDto>? = null,
    @SerialName("saturday") val saturday: List<TimeSlotDto>? = null
)

@Serializable
data class TimeSlotDto(
    val start: String? = null,
    val end: String? = null,
    @SerialName("is_full") val isFull: Boolean? = null,
    @SerialName("is_open") val isOpen: Boolean? = null,
    val day: String? = null
)

fun List<RestaurantOpenHourSpec>.toRestaurantHoursDto() : RestaurantHoursDto {
    val dayList = flatMap { item ->
        item.openHours.map { time ->
            TimeSlotDto(
                start = time.startTime,
                end = time.endTime,
                isFull = item.isOpen24Hour,
                isOpen = item.isOpenForTheDay,
                day = item.apiDayOfWeekName
            )
        }
    }

    val dayOfWeekTimeSlot = dayList.groupBy { it.day.orEmpty() }

    return RestaurantHoursDto(
        sunday = dayOfWeekTimeSlot["sunday"],
        monday = dayOfWeekTimeSlot["monday"],
        tuesday = dayOfWeekTimeSlot["tuesday"],
        wednesday = dayOfWeekTimeSlot["wednesday"],
        thursday = dayOfWeekTimeSlot["thursday"],
        friday = dayOfWeekTimeSlot["friday"],
        saturday = dayOfWeekTimeSlot["saturday"]
    )
}
