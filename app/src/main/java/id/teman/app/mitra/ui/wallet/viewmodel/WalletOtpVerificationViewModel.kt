package id.teman.app.mitra.ui.wallet.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.repository.wallet.WalletRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

@HiltViewModel
class WalletOtpVerificationViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {

    var uiState by mutableStateOf(WalletOtpVerificationUiState())
        private set

    fun requestOtp() {
        startOtpTimer()
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.requestWalletOtpPin()
                .catch { exception ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = exception.message.orEmpty()
                    )
                }.collect {
                    uiState = uiState.copy(isLoading = false)
                }
        }
    }

    fun verifyOtp(otp: String) {
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.verifyWalletOtpPIN(otp)
                .catch { exception ->
                    uiState =
                        uiState.copy(isLoading = false, errorMessage = exception.message.orEmpty())
                }.collect { token ->
                    uiState =
                        uiState.copy(isLoading = false, successRedirectSetPinPage = Event(token))
                }
        }
    }

    private val countdownTimerDelay = 1000L
    private val initialSecond = 30L
    fun startOtpTimer() = viewModelScope.launch {
        var currentSeconds = initialSecond
        flow {
            while (currentSeconds >= 0) {
                emit(currentSeconds--)
                kotlinx.coroutines.delay(countdownTimerDelay)
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

data class WalletOtpVerificationUiState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successRedirectSetPinPage: Event<String>? = null,
    val otpTimer: String? = null
)