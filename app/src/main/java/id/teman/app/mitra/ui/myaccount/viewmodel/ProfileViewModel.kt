package id.teman.app.mitra.ui.myaccount.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.preference.Preference
import id.teman.app.mitra.repository.user.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preference: Preference,
    private val json: Json,
    private val userRepository: UserRepository
) : ViewModel() {

    var uiState by mutableStateOf(ProfileUiState())
        private set

    init {
        getUserProfile()
    }

    fun logout() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.logout()
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
                }
                .collect {
                    preference.setUserInfo("")
                    preference.setBearerToken("")
                    preference.setRefreshToken("")
                    uiState = uiState.copy(redirectToLogin = Event(Unit), isLoading = false)
                }
        }
    }

    fun getUserProfile() = viewModelScope.launch {
        uiState = uiState.copy(isLoading = true)
        val rawJson = runBlocking {  preference.getUserInfo.first() }
        rawJson?.let {
            val userInfo = json.decodeFromString<UserInfo>(it)
            uiState = uiState.copy(isLoading = false, userInfo = userInfo)
        }
    }
}

data class ProfileUiState(
    val isLoading: Boolean = false,
    val userInfo: UserInfo? = null,
    val redirectToLogin: Event<Unit>? = null,
    val error: Event<String>? = null
)