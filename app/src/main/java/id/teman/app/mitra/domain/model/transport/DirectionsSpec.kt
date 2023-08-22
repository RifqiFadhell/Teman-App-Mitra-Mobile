package id.teman.app.mitra.domain.model.transport

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

data class DirectionsSpec(
    val points: List<LatLng>,
    val distanceText: String,
    val durationText: String,
    val bounds: LatLngBounds?
)