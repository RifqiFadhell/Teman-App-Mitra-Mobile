package id.teman.app.mitra.domain.model.location

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.dto.maps.PlaceResponseDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceDetailSpec(
    val locationLatLng: LatLng,
    val formattedAddress: String
) : Parcelable

fun PlaceResponseDto.toPlaceDetailSpec(): PlaceDetailSpec? {
    if (result == null) return null
    return PlaceDetailSpec(
        locationLatLng = LatLng(
            result.geometry?.location?.lat.orZero(),
            result.geometry?.location?.lng.orZero()
        ),
        formattedAddress = result.formattedAddress.orEmpty()
    )
}