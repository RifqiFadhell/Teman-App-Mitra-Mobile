package id.teman.app.mitra.domain.model.registration

import android.net.Uri
import id.teman.app.mitra.common.UriSerializer
import id.teman.app.mitra.data.dto.restaurant.CategoriesRestaurantDto
import kotlinx.serialization.Serializable

@Serializable
data class OutletInformationSpec(
    val postalCode: String,
    val outletPhoneNumber: String,
    val outletCompleteAddress: String,
    val outletOptionalAddress: String,
    val outletNearbyHint: String,
    val outletLatitude: Double,
    val outletLongitude: Double,
    @Serializable(UriSerializer::class)
    val outletPhoto: Uri
)

@Serializable
data class CategoriesRestaurantSpec(
    val id: String,
    val name: String
)

fun List<CategoriesRestaurantDto>?.convertToCategoriesRestaurantSpec(): List<CategoriesRestaurantSpec> {
    return this?.map {
        CategoriesRestaurantSpec(
            id = it.id.orEmpty(),
            name = it.name.orEmpty()
        )
    }.orEmpty()
}