package id.teman.app.mitra.ui.otp

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.user.UserKycStatus
import id.teman.app.mitra.repository.user.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    var uiState by mutableStateOf(OtpUiState())
        private set

    private val countdownTimerDelay = 1000L
    private val initialSecond = 30L

    init {
        startOtpTimer(0)
    }

    fun resetUiState() {
        uiState = OtpUiState()
    }

    fun verifyOtpCode(otpCode: String) = viewModelScope.launch {
        uiState = uiState.copy(isLoading = true)
        userRepository.verifyOtp(otpCode)
            .catch { exception ->
                uiState = uiState.copy(isLoading = false, errorMessage = exception.message.orEmpty())
            }
            .collect {
                if (it.isVerified && it.userKycStatus == UserKycStatus.APPROVED) {
                    getProfile()
                } else {
                    uiState = uiState.copy(isLoading = false, successRedirectRegistration = Event(Unit))
                }
            }
    }

    private suspend fun getProfile() {
        userRepository.getUserProfile()
            .catch { exception ->
                uiState = uiState.copy(isLoading = false,errorMessage = exception.message.orEmpty())
            }
            .collect {
                if (it.driverInfo != null) {
                    uiState = uiState.copy(isLoading = false, successRedirectLoginDriver = Event(Unit))
                } else if (it.restaurantInfo != null) {
                    uiState = uiState.copy(isLoading = false, successRedirectLoginRestaurant = Event(Unit))
                }
            }
    }

    fun resendOtp() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO){
            userRepository.sendOtp()
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, errorMessage = exception.message.orEmpty())
                }.collect {
                    uiState = uiState.copy(isLoading = false, successSendOtp = Event(it))
                }
        }
    }

    fun startOtpTimer(attempt: Int) = viewModelScope.launch {
        var currentSeconds = if (attempt <= 1) {
            initialSecond
        } else {
            initialSecond * attempt
        }
        flow {
            while (currentSeconds >= 0) {
                emit(currentSeconds--)
                delay(countdownTimerDelay)
            }
        }.collect { lastSecond ->
            uiState = if (lastSecond == 0L) {
                uiState.copy(otpTimer = null)
            } else {
                uiState.copy(otpTimer = "$lastSecond")
            }
        }
    }
}

data class OtpUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successRedirectRegistration: Event<Unit>? = null,
    val successSendOtp: Event<Int>? = null,
    val successRedirectLoginDriver: Event<Unit>? = null,
    val successRedirectLoginRestaurant: Event<Unit>? = null,
    val otpTimer: String? = null
)