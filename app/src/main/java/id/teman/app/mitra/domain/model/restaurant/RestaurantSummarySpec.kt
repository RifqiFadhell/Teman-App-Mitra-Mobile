package id.teman.app.mitra.domain.model.restaurant

import kotlinx.serialization.Serializable

@Serializable
data class RestaurantSummarySpec(
    val transaction: Int,
    val income: Double
)