package id.teman.app.mitra.repository.location

import id.teman.app.mitra.data.remote.location.LocationDataSource
import id.teman.app.mitra.domain.model.location.PlaceDetailSpec
import id.teman.app.mitra.domain.model.location.SearchLocationSpec
import id.teman.app.mitra.domain.model.location.toPlaceDetailSpec
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class LocationRepository @Inject constructor(
    private val locationDataSource: LocationDataSource
) {

    suspend fun searchLocation(query: String): Flow<List<SearchLocationSpec>> = flow {
        locationDataSource
            .searchLocation(query)
            .flowOn(Dispatchers.IO)
            .catch { exception -> throw exception }
            .collect { response ->
                if (!response.predictions.isNullOrEmpty()) {
                    emit(response.predictions.map {
                        SearchLocationSpec(
                            it.structuredFormatting?.title.orEmpty(),
                            it.structuredFormatting?.description.orEmpty(),
                            placeId = it.placeId.orEmpty()
                        )
                    })
                } else {
                    throw Exception()
                }
            }
    }

    suspend fun getLocationDetail(placeId: String): Flow<PlaceDetailSpec?> = locationDataSource
        .getLocationDetail(placeId).flowOn(Dispatchers.IO).map { it.toPlaceDetailSpec() }
}