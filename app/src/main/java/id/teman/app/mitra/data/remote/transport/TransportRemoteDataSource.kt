package id.teman.app.mitra.data.remote.transport

import id.teman.app.mitra.BuildConfig
import id.teman.app.mitra.data.dto.maps.DirectionResponseDto
import id.teman.app.mitra.data.dto.transport.DrivingSummaryDto
import id.teman.app.mitra.data.dto.transport.SnappedPointsDto
import id.teman.app.mitra.data.dto.transport.TransportDataResponseDto
import id.teman.app.mitra.data.dto.transport.TransportResponseDto
import id.teman.app.mitra.data.dto.transport.UpdateRequestStatusDto
import id.teman.app.mitra.data.remote.ApiServiceInterface
import id.teman.app.mitra.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface TransportRemoteDataSource {

    suspend fun getCustomerOrders(): Flow<TransportDataResponseDto>
    suspend fun getDriverOrderSummary(): Flow<DrivingSummaryDto>
    suspend fun getMapDirection(
        origin: String,
        destination: String,
        apiKey: String, vehicleType: String
    ): Flow<DirectionResponseDto>

    suspend fun updateRequestStatus(requestId: String, status: String): Flow<TransportResponseDto>
    suspend fun getDriverStatus(): Flow<TransportResponseDto>
    suspend fun getSnappedRoad(originPoint: String): Flow<SnappedPointsDto>
    suspend fun getHistory(): Flow<TransportDataResponseDto>
}

class DefaultTransportRemoteDataSource(
    private val httpClient: ApiServiceInterface
) : TransportRemoteDataSource {

    override suspend fun getCustomerOrders(): Flow<TransportDataResponseDto> =
        handleRequestOnFlow { httpClient.getCustomerOrder() }

    override suspend fun getDriverOrderSummary(): Flow<DrivingSummaryDto> =
        handleRequestOnFlow { httpClient.getDrivingSummary() }

    override suspend fun getMapDirection(
        origin: String,
        destination: String,
        apiKey: String,
        vehicleType: String
    ): Flow<DirectionResponseDto> =
        handleRequestOnFlow {
            httpClient.getMapDirection(
                origin = origin,
                destination = destination,
                mode = "driving",
                vehicleType = vehicleType,
                apiKey = apiKey,
                avoid = if (vehicleType == "motorcycle") "tolls" else null
            )
        }

    override suspend fun updateRequestStatus(
        requestId: String,
        status: String
    ): Flow<TransportResponseDto> =
        handleRequestOnFlow {
            httpClient.updateDriverRequestStatus(
                requestId,
                UpdateRequestStatusDto(status)
            )
        }

    override suspend fun getDriverStatus(): Flow<TransportResponseDto> =
        handleRequestOnFlow { httpClient.getDriverStatus() }

    override suspend fun getSnappedRoad(originPoint: String): Flow<SnappedPointsDto> =
        handleRequestOnFlow {
            httpClient.getSnappedRoad(
                origin = originPoint,
                apiKey = BuildConfig.MAPS_API_KEY
            )
        }

    override suspend fun getHistory(): Flow<TransportDataResponseDto> =
        handleRequestOnFlow { httpClient.getRequestHistory() }

}