package id.teman.app.mitra.domain.model.restaurant

import id.teman.app.mitra.domain.model.user.backgroundColor
import id.teman.app.mitra.domain.model.user.textColor
import id.teman.coreui.typography.UiColor
import kotlinx.serialization.Serializable

@Serializable
enum class RestaurantOrderStatus(val value: String) {
    UNKNOWN("Unknown"),
    NEW("new"),
    CANCELLED("canceled"),
    PROCESS("process"),
    FINISHED("finished");

    companion object {
        fun from(value: String?) = when (value) {
            PROCESS.value -> PROCESS
            FINISHED.value -> FINISHED
            CANCELLED.value -> CANCELLED
            NEW.value -> NEW
            else -> UNKNOWN
        }
    }
}

fun RestaurantOrderStatus.colorStatus(): Pair<textColor, backgroundColor> = when (this) {
    RestaurantOrderStatus.NEW -> Pair(UiColor.primaryYellow500, UiColor.primaryYellow50)
    RestaurantOrderStatus.PROCESS -> Pair(UiColor.success500, UiColor.success50)
    RestaurantOrderStatus.FINISHED -> Pair(UiColor.tertiaryBlue500, UiColor.tertiaryBlue50)
    else -> Pair(UiColor.primaryRed500, UiColor.primaryRed50)
}

fun RestaurantOrderStatus.textValue(): String = when (this) {
    RestaurantOrderStatus.UNKNOWN -> "Unknown"
    RestaurantOrderStatus.NEW -> "Baru"
    RestaurantOrderStatus.CANCELLED -> "Dibatalkan"
    RestaurantOrderStatus.PROCESS -> "Diproses"
    RestaurantOrderStatus.FINISHED -> "Selesai"
}

fun RestaurantOrderStatus.isTerminalStatus(): Boolean = when (this) {
    RestaurantOrderStatus.NEW -> false
    RestaurantOrderStatus.PROCESS -> false
    else -> true
}