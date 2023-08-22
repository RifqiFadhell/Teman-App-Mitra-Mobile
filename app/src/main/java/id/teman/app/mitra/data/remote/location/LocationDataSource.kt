package id.teman.app.mitra.data.remote.location

import id.teman.app.mitra.BuildConfig
import id.teman.app.mitra.data.dto.maps.GooglePredictionsDto
import id.teman.app.mitra.data.dto.maps.PlaceResponseDto
import id.teman.app.mitra.data.remote.ApiServiceInterface
import id.teman.app.mitra.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface LocationDataSource {

    suspend fun searchLocation(query: String): Flow<GooglePredictionsDto>
    suspend fun getLocationDetail(placeId: String): Flow<PlaceResponseDto>
}

class DefaultLocationDataSource(private val httpClient: ApiServiceInterface): LocationDataSource {
    override suspend fun searchLocation(query: String): Flow<GooglePredictionsDto> =
        handleRequestOnFlow {
            httpClient.searchLocation(input = query, key = BuildConfig.MAPS_API_KEY)
        }

    override suspend fun getLocationDetail(placeId: String): Flow<PlaceResponseDto> =
        handleRequestOnFlow {
            httpClient.getPlaceDetail(BuildConfig.MAPS_API_KEY, placeId)
        }

}