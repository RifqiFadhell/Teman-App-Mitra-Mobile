package id.teman.app.mitra.domain.model.user

import androidx.compose.ui.graphics.Color
import id.teman.coreui.typography.UiColor
import kotlinx.serialization.Serializable

@Serializable
enum class RestaurantStatus(val value: String) {
    OPEN("Sedang Buka"),
    CLOSE("Sedang Tutup"),
    REST("Sedang Istirahat");

    companion object  {
        fun from(value: String?) = when (value) {
            "open" -> OPEN
            "close" -> CLOSE
            else -> REST
        }
    }
}

fun RestaurantStatus.colorStatus(): Pair<textColor, backgroundColor> = when (this) {
    RestaurantStatus.OPEN -> Pair(UiColor.success500, UiColor.success50)
    RestaurantStatus.CLOSE -> Pair(UiColor.primaryRed500, UiColor.primaryRed50)
    RestaurantStatus.REST -> Pair(UiColor.primaryYellow500, UiColor.primaryYellow50)
}

fun RestaurantStatus.toUpdateFieldValue(): String = when (this) {
    RestaurantStatus.OPEN -> "open"
    RestaurantStatus.CLOSE -> "close"
    RestaurantStatus.REST -> "rest"
}

typealias backgroundColor = Color
typealias textColor = Color