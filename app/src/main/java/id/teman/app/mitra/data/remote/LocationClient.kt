package id.teman.app.mitra.data.remote

import android.app.Application
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import id.teman.app.mitra.common.hasLocationPermission
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

interface LocationClient {
    fun getLocationUpdates(locationRequest: LocationRequest): Flow<Location>

    class PermissionLocationException(message: String) : Exception()
    class GpsLocationException(val locationRequest: LocationRequest) : Exception()
}

class DefaultLocationClient(
    private val application: Application,
    private val client: FusedLocationProviderClient,
) : LocationClient {

    override fun getLocationUpdates(locationRequest: LocationRequest): Flow<Location> {
        return callbackFlow {
            if (!application.hasLocationPermission()) {
                throw LocationClient.PermissionLocationException("Mission location permission")
            } else {
                val locationManager =
                    application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled =
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                if (!isGpsEnabled && !isNetworkEnabled) {
                    throw LocationClient.GpsLocationException(locationRequest)
                } else {
                    val locationCallback = object : LocationCallback() {
                        override fun onLocationResult(result: LocationResult) {
                            super.onLocationResult(result)
                            launch {
                                send(result.lastLocation)
                            }
                        }
                    }

                    client.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )

                    awaitClose {
                        client.removeLocationUpdates(locationCallback)
                    }
                }
            }
        }
    }
}