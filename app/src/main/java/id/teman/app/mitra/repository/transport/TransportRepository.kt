package id.teman.app.mitra.repository.transport

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.PolyUtil
import id.teman.app.mitra.BuildConfig
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.data.remote.transport.TransportRemoteDataSource
import id.teman.app.mitra.domain.model.transport.DirectionsSpec
import id.teman.app.mitra.domain.model.transport.DriverOrderSummarySpec
import id.teman.app.mitra.domain.model.transport.OrderHistoryUISection
import id.teman.app.mitra.domain.model.transport.TransportOrderSpec
import id.teman.app.mitra.domain.model.transport.toOrderHistoryUISection
import id.teman.app.mitra.domain.model.transport.toTransportOrderSpec
import id.teman.app.mitra.domain.model.transport.toTransportOrderSpecList
import id.teman.app.mitra.preference.Preference
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class TransportRepository @Inject constructor(
    private val transportRemoteDataSource: TransportRemoteDataSource,
    private val preference: Preference
) {

    suspend fun getDriverOrderSummary(): Flow<DriverOrderSummarySpec> =
        transportRemoteDataSource.getDriverOrderSummary().flowOn(Dispatchers.IO)
            .map {
                DriverOrderSummarySpec(
                    income = it.income.orZero(),
                    onlineHours = it.onlineHours.orZero(),
                    totalDistance = it.totalDistance?.toLong() ?: 0L,
                    totalJobs = it.totalJobs.orZero()
                )
            }

    suspend fun getCustomerOrders(): Flow<List<TransportOrderSpec>> {
        return transportRemoteDataSource.getCustomerOrders().flowOn(Dispatchers.IO)
            .map {
                it.data?.toTransportOrderSpecList().orEmpty()
            }
    }

    suspend fun getMapDirection(origin: String, destination: String, vehicleType: String): Flow<DirectionsSpec> = flow {
        val mapApiKey = BuildConfig.MAPS_API_KEY
        transportRemoteDataSource.getMapDirection(origin, destination, mapApiKey, vehicleType).flowOn(Dispatchers.IO)
            .catch { exception -> throw exception }
            .collect {
                val legsDetail = it.routes?.getOrNull(0)?.legs
                val points = legsDetail?.getOrNull(0)?.steps
                val northeast = it.routes?.getOrNull(0)?.bounds?.northeast
                val southwest = it.routes?.getOrNull(0)?.bounds?.southwest
                val latLngBounds= if (northeast != null && southwest != null) LatLngBounds(
                    LatLng(southwest.lat!!, southwest.lng!!), LatLng(northeast.lat!!, northeast.lng!!)
                ) else null
                if (!points.isNullOrEmpty()) {
                    val latLng = arrayListOf<LatLng>()
                    points.forEach { step ->
                        latLng.add(LatLng(step.startLocation?.lat.orZero(),
                            step.startLocation?.lng.orZero()))

                        val polyline = PolyUtil.decode(step.polyline?.points)
                        latLng.addAll(polyline)
                        latLng.add(LatLng(step.endLocation?.lat.orZero(),
                                step.endLocation?.lng.orZero())
                        )
                    }
                    emit(
                        DirectionsSpec(
                            latLng, legsDetail.getOrNull(0)?.distance?.text.orEmpty(),
                            legsDetail.getOrNull(0)?.duration?.text.orEmpty(),
                            latLngBounds
                        )
                    )
                }
            }
    }

    suspend fun updateRequestStatus(
        selectedOrder: TransportOrderSpec,
        status: String
    ): Flow<TransportOrderSpec> = transportRemoteDataSource.updateRequestStatus(
        selectedOrder.requestId,
        status,
    ).flowOn(Dispatchers.IO).map { transportDto -> transportDto.toTransportOrderSpec() }


    suspend fun getDriverStatus(): Flow<TransportOrderSpec> = transportRemoteDataSource.getDriverStatus().flowOn(Dispatchers.IO)
        .map { it.toTransportOrderSpec() }

    suspend fun getSnappedRoad(originPoints: String): Flow<LatLng> = flow {
        transportRemoteDataSource
            .getSnappedRoad(originPoints).catch { exception -> throw exception }
            .collect { snappedPoint ->
                val firstPoints = snappedPoint.snappedPoints?.firstOrNull()

                firstPoints?.let {
                    val latitude = it.location?.latitude.orZero()
                    val longitude = it.location?.longitude.orZero()

                    if (latitude != 0.0 && longitude != 0.0) {
                        emit(LatLng(latitude, longitude))
                    } else {
                        throw Exception()
                    }
                } ?: run {
                    throw Exception()
                }
            }
    }

    suspend fun getOrderHistory(): Flow<Pair<List<OrderHistoryUISection>, List<TransportOrderSpec>>> {
        return transportRemoteDataSource.getHistory()
            .flowOn(Dispatchers.IO)
            .map {
                Pair(it.toOrderHistoryUISection(), it.data?.toTransportOrderSpecList().orEmpty())
            }
    }
}