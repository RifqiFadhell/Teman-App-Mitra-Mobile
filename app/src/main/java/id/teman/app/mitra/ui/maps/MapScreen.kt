package id.teman.app.mitra.ui.maps

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Bitmap
import android.location.Location
import android.view.animation.LinearInterpolator
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import id.teman.app.mitra.R
import id.teman.app.mitra.domain.model.transport.TransportEnRouteSpec
import id.teman.app.mitra.ui.transport.TransportViewModel
import id.teman.coreui.typography.UiColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MapScreen(viewModel: TransportViewModel, value: Location) {
    val uiSpec = viewModel.transportUiState
    val uiSettings by remember { mutableStateOf(MapUiSettings(compassEnabled = false)) }
    val properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }
    var latestBearing by remember { mutableStateOf(value.bearing) }
    var userMarkerPosition by remember { mutableStateOf(LatLng(value.latitude, value.longitude)) }

    var activePolylines by rememberSaveable { mutableStateOf<TransportEnRouteSpec?>(null) }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(value.latitude, value.longitude), 30f)
    }
    val context = LocalContext.current
    val coroutinScope = rememberCoroutineScope()

    LaunchedEffect(key1 = uiSpec.resetMap, block = {
        uiSpec.resetMap?.consumeOnce {
            activePolylines = null
        }
    })

    LaunchedEffect(key1 = uiSpec.updateDriverLocation, block = {
        uiSpec.updateDriverLocation?.consumeOnce { location ->
            val destination = LatLng(location.latitude, location.longitude)
            val zoomLevel = calculateZoomLevel(userMarkerPosition, destination)
            latestBearing = location.bearing

            if (activePolylines != null) {
                val currentPlace = CameraPosition.Builder()
                    .target(destination)
                    .bearing(latestBearing)
                    .tilt(45f)
                    .zoom(zoomLevel)
                    .build()
                coroutinScope.launch {
                    cameraPositionState.animate(update = CameraUpdateFactory.newCameraPosition(currentPlace),
                        durationMs = 400)
                }

                animateMarkerPosition(userMarkerPosition, destination) {
                    userMarkerPosition = it
                }
            } else {
                userMarkerPosition = destination
                coroutinScope.launch {
                    val currentPlace = CameraPosition.Builder()
                        .target(destination)
                        .bearing(latestBearing)
                        .tilt(45f)
                        .zoom(zoomLevel)
                        .build()
                    delay(300)
                    cameraPositionState.animate(update = CameraUpdateFactory.newCameraPosition(currentPlace))
                }
            }
        }

    })

    LaunchedEffect(key1 = uiSpec.driverDirectionSpec, block = {
        uiSpec.driverDirectionSpec?.consumeOnce {
            activePolylines = it
        }
    })

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        properties = properties,
        uiSettings = uiSettings,
        cameraPositionState = cameraPositionState,
        onMapLoaded = {
            coroutinScope.launch {
                cameraPositionState.animate(
                    update= CameraUpdateFactory.scrollBy(0f, 400f)
                )
            }
        },
        content = {
            Marker(
                state = MarkerState(userMarkerPosition),
                icon = bitmapDescriptor(context, R.drawable.ic_marker),
                anchor = Offset(0.5f, 0.5f),
                rotation = latestBearing,
                flat = true,
            )
            activePolylines?.let { item ->
                item.destinationLatLng?.let {
                    Marker(state = MarkerState(it))
                }

                Polyline(
                    points = item.points,
                    color = UiColor.blue,
                    jointType = JointType.ROUND,
                    width = 20f,
                    geodesic = true
                )
            }
        }
    )
}

fun bitmapDescriptor(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

fun animateMarkerPosition(originLoc: LatLng, destination: LatLng, onAnimate: (LatLng) -> Unit) {
    val animator = ValueAnimator()
    animator.addUpdateListener { animation ->
        val newPosition: LatLng = SphericalUtil.interpolate(originLoc, destination, animation.animatedFraction.toDouble())
        onAnimate(newPosition)
    }
    animator.setFloatValues(0f,1f)
    animator.duration = 2000
    animator.interpolator = LinearInterpolator()
    animator.start()
}

fun calculateZoomLevel(origin: LatLng, destination: LatLng): Float {
    val distance = SphericalUtil.computeDistanceBetween(origin, destination)
    var zoomLevel = 20f

    if (distance > 10000) {
        zoomLevel = 16f
    } else if (distance > 5000) {
        zoomLevel = 17f
    } else if (distance > 1000) {
        zoomLevel = 18f
    } else if (distance > 500) {
        zoomLevel = 19f
    }
    return zoomLevel
}