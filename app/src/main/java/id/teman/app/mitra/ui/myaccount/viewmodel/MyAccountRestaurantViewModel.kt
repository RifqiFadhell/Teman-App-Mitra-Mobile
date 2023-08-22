package id.teman.app.mitra.ui.myaccount.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.common.createMultipartImageFromUriGallery
import id.teman.app.mitra.common.createPartFromString
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.repository.user.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class MyAccountRestaurantViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val application: Application,
) : ViewModel() {

    var uiState by mutableStateOf(MyAccountUiState())
        private set

    fun getUserProfile() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            userRepository.getUserProfile()
                .catch { exception ->
                    uiState= uiState.copy(isLoading = false, updateFailed = Event(exception.message ?: "telah terjadi kesalahan"))
                }
                .collect {
                    uiState = uiState.copy(isLoading = false, userInfo = it)
                }
        }
    }

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            userRepository.logout()
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, updateFailed = Event(exception.message ?: "telah terjadi kesalahan"))
                }.collect {
                    uiState = uiState.copy(isLoading = false, logoutSuccess = Event(Unit))
                }
        }
    }

    fun updateRestaurantProfile(
        name: String? = null,
        description: String? = null,
        email: String? = null,
        phoneNumber: String? = null,
        restoPhoto: Uri? = null,
        uriPath: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            val map: MutableMap<String, RequestBody> = mutableMapOf()
            if (name.isNotNullOrEmpty()) {
                map["name"] = createPartFromString(name!!)
            }
            if (phoneNumber.isNotNullOrEmpty()) {
                map["phone_number"] = createPartFromString(phoneNumber!!)
            }
            if (description.isNotNullOrEmpty()) {
                map["description"] = createPartFromString(description!!)
            }
            if (email.isNotNullOrEmpty()) {
                map["email"] = createPartFromString(email!!)
            }
            val photo = if (restoPhoto != null && restoPhoto != Uri.EMPTY) {
                createMultipartImageFromUriGallery(
                    application,
                    restoPhoto,
                    "restaurant_photo",
                    uriPath.orEmpty()
                )
            } else {
                null
            }
            userRepository.updateRestaurantProfile(map, photo)
                .catch { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        updateFailed = Event(exception.message.orEmpty())
                    )
                }.collect {
                    uiState = uiState.copy(isLoading = false, updateSuccess = Event(Unit))
                }
        }
    }

    fun updateDriverProfile(
        name: String? = null,
        phoneNumber: String? = null,
        driverPhoto: Uri? = null,
        uriPath: String? = null
    ) = viewModelScope.launch(Dispatchers.IO) {
        uiState = uiState.copy(isLoading = true)
        val map: MutableMap<String, RequestBody> = mutableMapOf()
        if (name.isNotNullOrEmpty()) {
            map["name"] = createPartFromString(name!!)
        }
        if (phoneNumber.isNotNullOrEmpty()) {
            map["phone_number"] = createPartFromString(phoneNumber!!)
        }
        val photo = if (driverPhoto != null && driverPhoto != Uri.EMPTY) {
            createMultipartImageFromUriGallery(
                application,
                driverPhoto,
                "user_photo",
                uriPath.orEmpty()
            )
        } else {
            null
        }
        userRepository.updateDriverProfile(map, photo)
            .catch { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    updateFailed = Event(exception.message.orEmpty())
                )
            }.collect {
                uiState = uiState.copy(isLoading = false, updateSuccess = Event(Unit))
            }
    }
}

data class MyAccountUiState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo? = null,
    val updateSuccess: Event<Unit>? = null,
    val updateFailed: Event<String>? = null,
    val logoutSuccess: Event<Unit>? = null
)