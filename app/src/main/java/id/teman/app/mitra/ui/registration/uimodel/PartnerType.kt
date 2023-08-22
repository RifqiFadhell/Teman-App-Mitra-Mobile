package id.teman.app.mitra.ui.registration.uimodel

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import id.teman.app.mitra.R
import id.teman.coreui.typography.UiColor

const val T_CAR_TITLE = "Mitra T-Car"
const val T_BIKE_TITLE = "Mitra T-Bike"
const val T_FOOD_TITLE = "Mitra T-Food"

sealed class PartnerType(
    val backgroundColor: Color,
    val title: String,
    val subtitle: String,
    @DrawableRes val icon: Int
) {
    object PartnerCar: PartnerType(
        backgroundColor = UiColor.primaryRed50,
        title = T_CAR_TITLE,
        subtitle = "Kamu bisa ambil orderan T-Car",
        icon = R.drawable.ic_teman_car
    )

    object PartnerBike: PartnerType(
        backgroundColor = UiColor.primaryRed50,
        title = T_BIKE_TITLE,
        subtitle = "Kamu bisa ambil orderan T-Bike dan T-Send",
        icon = R.drawable.ic_teman_bike
    )

    object PartnerFood: PartnerType(
        backgroundColor = UiColor.primaryYellow50,
        title = T_FOOD_TITLE,
        subtitle = "Kamu bisa mengembangkan usaha bisnis makanan Kamu dengan menjadi Mitra T-Food.",
        icon = R.drawable.ic_teman_food
    )
}