package id.teman.app.mitra.ui.search

import android.location.Address
import android.location.Geocoder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.location.PlaceDetailSpec
import id.teman.app.mitra.domain.model.location.SearchLocationSpec
import id.teman.app.mitra.repository.location.LocationRepository
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class SearchLocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val geocoder: Geocoder
) : ViewModel() {

    var searchUiState by mutableStateOf(SearchLocationUiState())
        private set

    private var searchJob: Job? = null

    override fun onCleared() {
        super.onCleared()
        if (searchJob != null) {
            searchJob?.cancel()
        }
    }

    fun searchDebounced(searchText: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500)
            searchLocation(searchText)
        }
    }

    fun searchLocation(query: String) {
        searchUiState = searchUiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.searchLocation(query)
                .catch { exception ->
                    searchUiState = searchUiState.copy(
                        loading = false,
                        error = Event(exception.message.orEmpty())
                    )
                }.collect { spec ->
                    searchUiState = searchUiState.copy(loading = false, availableLocation = spec)
                }
        }
    }

    fun getLocationDetail(placeId: String) {
        searchUiState = searchUiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.IO) {
            locationRepository.getLocationDetail(placeId)
                .catch { exception ->
                    searchUiState = searchUiState.copy(
                        loading = false,
                        error = Event(exception.message.orEmpty())
                    )
                }.collect { spec ->
                    if (spec != null) {
                        searchUiState  = searchUiState.copy(
                            loading = false,
                            successGetPlaceDetail = Event(spec)
                        )
                    } else {
                        searchUiState = searchUiState.copy(
                            loading = false
                        )
                    }
                }
        }
    }

    fun getLocationName(value: LatLng) {
        searchUiState = searchUiState.copy(loading = true)
        viewModelScope.launch(Dispatchers.Default) {
            if (Geocoder.isPresent()) {
                val addresses: MutableList<Address>
                try {
                    addresses = geocoder.getFromLocation(
                        value.latitude,
                        value.longitude,
                        5
                    ) as ArrayList<Address>

                    if (addresses.isNotEmpty()) {
                        val city = addresses.getOrNull(0)?.subAdminArea.orEmpty()
                        val subAdminArea = addresses.getOrNull(0)?.subLocality.orEmpty()
                        val spec = PlaceDetailSpec(
                            LatLng(value.latitude, value.longitude),
                            addresses.getOrNull(0)?.getAddressLine(0).orEmpty()
                        )
                        searchUiState = searchUiState.copy(
                            loading = false,
                            availableLocation = emptyList(),
                            pinLocationName = Event(spec)
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                searchUiState = searchUiState.copy(loading = false)
            } else {
                searchUiState = searchUiState.copy(loading = false, error = Event("Gagal Mendapatkan Data"))
            }
        }
    }
}

data class SearchLocationUiState(
    val loading: Boolean = false,
    val error: Event<String>? = null,
    val availableLocation: List<SearchLocationSpec> = emptyList(),
    val successGetPlaceDetail: Event<PlaceDetailSpec>? = null,
    val pinLocationName: Event<PlaceDetailSpec>? = null
)