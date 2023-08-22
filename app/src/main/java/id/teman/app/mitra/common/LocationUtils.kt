package id.teman.app.mitra.common

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil
import java.util.Collections
import kotlin.math.*

fun findMidPoint(source: LatLng, destination: LatLng): LatLng {
    val x1: Double = Math.toRadians(source.latitude)
    val y1: Double = Math.toRadians(source.longitude)
    val x2: Double = Math.toRadians(destination.latitude)
    val y2: Double = Math.toRadians(destination.longitude)
    val Bx = Math.cos(x2) * Math.cos(y2 - y1)
    val By = Math.cos(x2) * Math.sin(y2 - y1)
    val x3: Double = Math.toDegrees(
        Math.atan2(
            Math.sin(x1) + Math.sin(x2),
            Math.sqrt((Math.cos(x1) + Bx) * (Math.cos(x1) + Bx) + By * By)
        )
    )
    var y3 = y1 + Math.atan2(By, Math.cos(x1) + Bx)
    y3 = Math.toDegrees((y3 + 540) % 360 - 180)
    return LatLng(x3, y3)
}

fun splitPathIntoPoints(source: LatLng, destination: LatLng): List<LatLng> {
    var distance: Float = findDistance(source, destination)
    val splitPoints: MutableList<LatLng> = ArrayList()
    splitPoints.add(source)
    splitPoints.add(destination)
    while (distance > 1) {
        val polypathSize = splitPoints.size
        val tempPoints: MutableList<LatLng?> = ArrayList()
        tempPoints.addAll(splitPoints)
        var injectionIndex = 1
        for (i in 0 until polypathSize - 1) {
            val a1 = tempPoints[i]
            val a2 = tempPoints[i + 1]
            splitPoints.add(injectionIndex, findMidPoint(a1!!, a2!!))
            injectionIndex += 2
        }
        distance = findDistance(splitPoints[0], splitPoints[1])
    }
    return splitPoints
}

fun findDistance(source: LatLng, destination: LatLng): Float {
    val srcLoc = Location("srcLoc")
    srcLoc.latitude = source.latitude
    srcLoc.longitude = source.longitude
    val destLoc = Location("destLoc")
    destLoc.latitude = destination.latitude
    destLoc.longitude = destination.longitude
    return srcLoc.distanceTo(destLoc)
}

fun snapToPolyline(mSplitPoints: List<LatLng>, currentLocation: LatLng): LatLng? {
    var snappedLatLng: LatLng? = null
    var mMinorIndexTravelled = 0
    val current = Location("current")
    current.latitude = currentLocation.latitude
    current.longitude = currentLocation.longitude
    var minConfirmCount = 0
    var currentMinDistance = 0f
    var previousMinDistance = 0f
    val distances: MutableList<Float> = ArrayList()
    for (point in mSplitPoints.subList(mMinorIndexTravelled, mSplitPoints.size - 1)) {
        val pointLoc = Location("pointLoc")
        pointLoc.latitude = point.latitude
        pointLoc.longitude = point.longitude
        distances.add(current.distanceTo(pointLoc))
        previousMinDistance = currentMinDistance
        currentMinDistance = Collections.min(distances)
        if (currentMinDistance == previousMinDistance) {
            minConfirmCount++
            if (minConfirmCount > 10) {
                mMinorIndexTravelled += distances.indexOf(currentMinDistance)
                snappedLatLng = mSplitPoints.get(mMinorIndexTravelled)
                break
            }
        }
    }
    return snappedLatLng
}

fun getEdgeIndex(polylines: List<LatLng>, currentLatLng: LatLng): Int {
    val edgeIndex1 = PolyUtil.locationIndexOnPath(
        currentLatLng, polylines, true, 1.0
    )
    val edgeIndex2 = PolyUtil.locationIndexOnPath(
        currentLatLng, polylines, true, 2.0
    )
    val edgeIndex6 = PolyUtil.locationIndexOnPath(
        currentLatLng, polylines, true, 6.0
    )
    val edgeIndex10 = PolyUtil.locationIndexOnPath(
        currentLatLng, polylines, true, 10.0
    )
    val edgeIndex15 = PolyUtil.locationIndexOnPath(
        currentLatLng, polylines, true, 15.0
    )
    var finalIndex = -1

    if (edgeIndex1 >= 0) {
        finalIndex = edgeIndex1
    } else if (edgeIndex2 >= 0) {
        finalIndex = edgeIndex2
    } else if (edgeIndex6 >= 0) {
        finalIndex = edgeIndex6
    } else if (edgeIndex10 >= 0) {
        finalIndex = edgeIndex10
    } else if (edgeIndex15 >= 0) {
        finalIndex = edgeIndex15
    }
    return finalIndex
}

fun getSnapLatLng(polylines: List<LatLng>, currentLatLng: LatLng): LatLng {
    val finalIndex = getEdgeIndex(polylines, currentLatLng)

    if (finalIndex >= 0) {
        val snappedLatLng2 = if (finalIndex < polylines.count() - 1) polylines[finalIndex +1] else currentLatLng

        val snappedLatLng = polylines[finalIndex]

        val distance = SphericalUtil.computeDistanceBetween(snappedLatLng, currentLatLng)
        val heading = SphericalUtil.computeHeading(snappedLatLng2, snappedLatLng)
        val extrapolated = SphericalUtil.computeOffset(snappedLatLng, -distance, heading)

        return LatLng(extrapolated.latitude, extrapolated.longitude)
    }
    return currentLatLng
}

fun calculateZoomLevel(distance: Float):Float {
    val maxZoom = 20.0f
    val minZoom = 5.0f
    val maxDistance = 1000.0f
    val minDistance = 100.0f

    val zoomLevel = maxZoom - (distance - minDistance) * (maxZoom - minZoom) / (maxDistance - minDistance)

    return Math.max(minZoom, Math.min(maxZoom, zoomLevel))
}

fun calculateTiltAngle(altitude: Float): Float {
    // The maximum tilt angle allowed
    // The maximum tilt angle allowed
    val maxTiltAngle = 60.0f

    // The minimum tilt angle allowed

    // The minimum tilt angle allowed
    val minTiltAngle = 0.0f

    // The maximum altitude allowed

    // The maximum altitude allowed
    val maxAltitude = 1000.0f

    // The minimum altitude allowed

    // The minimum altitude allowed
    val minAltitude = 100.0f

    // Calculate the tilt angle based on the altitude

    // Calculate the tilt angle based on the altitude
    var tiltAngle =
        minTiltAngle + (altitude - minAltitude) * (maxTiltAngle - minTiltAngle) / (maxAltitude - minAltitude)

    // Ensure that the calculated tilt angle is within the allowed range

    // Ensure that the calculated tilt angle is within the allowed range
    tiltAngle = Math.max(minTiltAngle, Math.min(maxTiltAngle, tiltAngle))

    return tiltAngle
}

fun distance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
    val earthRadius = 6371
    val dLat = Math.toRadians(lat2 - lat1)
    val dLng = Math.toRadians(lng2 - lng1)
    val sindLat = sin(dLat / 2)
    val sindLng = sin(dLng / 2)
    val a = sindLat.pow(2.0) + (sindLng.pow(2.0)
            * cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)))
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}