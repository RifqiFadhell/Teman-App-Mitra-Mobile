package id.teman.app.mitra.domain.model.transport

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransportEnRouteSpec(
    val points: List<LatLng>,
    val destinationLatLng: LatLng?= null,
    val bounds: LatLngBounds? = null
): Parcelable