package id.teman.app.mitra.ui

import android.content.Intent
import android.location.Location
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.BuildConfig
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.domain.model.user.UserKycStatus
import id.teman.app.mitra.manager.UserManager
import id.teman.app.mitra.manager.UserState
import id.teman.app.mitra.preference.Preference
import id.teman.app.mitra.repository.user.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preference: Preference,
    private val json: Json,
    private val userManager: UserManager,
    private val remoteConfig: FirebaseRemoteConfig,
    ) : ViewModel() {

    private val _uiState = MutableStateFlow<PermissionState>(PermissionState.Start)
    val uiState = _uiState.asStateFlow()

    var locationUiState by mutableStateOf(LocationPermissionUiState())
        private set

    var currentLocation: Location? = null

    var currentPolyline by mutableStateOf<List<LatLng>>(emptyList())
        private set

    init {
        viewModelScope.launch {
            userManager.start()
            userManager.observeUserState().collect { userState ->
                if (userState is UserState.Revoked) {
                    locationUiState = locationUiState.copy(logoutUser = Event(Unit))
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userManager.changeUserState(UserState.Revoked)
        }
    }

    fun checkIsAppUpToDate() {
        remoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val latestVersion = remoteConfig.getLong("app_version_mitra")
                if (latestVersion > BuildConfig.VERSION_CODE) {
                    locationUiState = locationUiState.copy(isNeedUpdate = true)
                }
            }
        }
    }

    fun setPolyline(points: List<LatLng>) {
        currentPolyline = points
        locationUiState = if (points.isNotEmpty()) {
            locationUiState.copy(changeMaximumSpeedInterval = Event(true))
        } else {
            locationUiState.copy(changeMaximumSpeedInterval = Event(false))
        }
    }

    fun changeUiState(state: PermissionState) {
        viewModelScope.launch {
            _uiState.emit(state)
        }
    }

    fun stopLoadingLocationUIState() {
        locationUiState = locationUiState.copy(loading = false)
    }

    fun showLoadingLocationUIState() {
        locationUiState = locationUiState.copy(loading = true)
    }

    fun captureLocationLatLng(location: Location) {
        currentLocation = location
        updateUserLocation()
        locationUiState = locationUiState.copy(updatedLocation = Event(location))
    }

    fun blockUser() {
        locationUiState = locationUiState.copy(showMockLocationBlock = Event(Unit))
    }

    fun updateUserLocation() {
        val userInfoJson = runBlocking {  preference.getUserInfo.first() }
        if (userInfoJson.isEmpty()) {
            locationUiState =
                locationUiState.copy(loading = false, showMockLocationBlock = Event(Unit))
            return
        }
        val userInfo = json.decodeFromString<UserInfo>(userInfoJson)
        if (userInfo.userKycStatus != UserKycStatus.APPROVED && userInfo.driverInfo == null) {
            return
        }
        currentLocation?.let { location ->
            viewModelScope.launch(Dispatchers.IO) {
                userRepository.updateUserLocation(
                    latitude = location.latitude, longitude = location.longitude,
                    bearing = location.bearing
                )
                    .catch {
                        // omit for now
                        locationUiState = locationUiState.copy(loading = false)
                    }.collect {
                        _uiState.emit(
                            PermissionState.UpdateLocationSuccess(
                                location.latitude,
                                location.longitude
                            )
                        )
                    }
            }
        }
    }

    fun getUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.getUserProfile()
                .catch {
                    /* no-op */
                }
                .collect {
                    /* no-op */
                }
        }
    }

    fun checkPreferences() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val rawData = preference.getDriverCancelledCount.first()
            val timeStamp: Long = preference.getDriverTimestampCount.first()
            if (rawData != 0) {
                val oneDayInMillis = 24 * 60 * 60 * 1000
                if (currentTime - timeStamp > oneDayInMillis) {
                    preference.setDriverCancelledCount(0)
                }
            }
        }
    }
    fun checkFromDeeplink(intent: Intent?) {
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener {
                it?.let { pendingDynamicLinkData ->
                    val deeplink = pendingDynamicLinkData.link
                    val referralCode: String? = deeplink?.getQueryParameter("referral")
                    locationUiState = locationUiState.copy(successGetReferral = referralCode.orEmpty())
                } ?: run {

                }
            }
    }
}

sealed class PermissionState {
    object LocationActive : PermissionState()
    object LocationDenied : PermissionState()
    object StartLocationPermission : PermissionState()
    object Start : PermissionState()
    data class UpdateLocationSuccess(val latitude: Double, val longitude: Double) :
        PermissionState()
}

data class LocationPermissionUiState(
    val loading: Boolean = false,
    val updatedLocation: Event<Location>? = null,
    val showMockLocationBlock: Event<Unit>? = null,
    val changeMaximumSpeedInterval: Event<Boolean>? = null,
    val successGetReferral: String? = "",
    val isNeedUpdate: Boolean? = false,
    val logoutUser: Event<Unit>? = null
)