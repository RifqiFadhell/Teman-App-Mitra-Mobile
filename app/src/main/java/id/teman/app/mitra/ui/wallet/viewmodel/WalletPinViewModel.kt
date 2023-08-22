package id.teman.app.mitra.ui.wallet.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.common.isNotNullOrEmpty
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.domain.model.wallet.WalletDataTransferSpec
import id.teman.app.mitra.preference.Preference
import id.teman.app.mitra.repository.wallet.WalletRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@HiltViewModel
class WalletPinViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val savedStateHandle: SavedStateHandle,
    private val preference: Preference,
    private val json: Json
): ViewModel() {

    var uiState by mutableStateOf(WalletPinUiState())
        private set

    fun initToken(token: String) {
        savedStateHandle["token"] = token
    }

    fun getUserProfile() = viewModelScope.launch {
        uiState = uiState.copy(isLoading = true)
        val rawJson = runBlocking {  preference.getUserInfo.first() }
        if (rawJson.isNotBlank()) {
            val userInfo = json.decodeFromString<UserInfo>(rawJson)
            uiState = uiState.copy(isLoading = false, userInfo = userInfo)
        }
    }

    fun setupWalletPin(otpCode: String) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            val token = savedStateHandle.get<String>("token")
            if (token.isNotNullOrEmpty()) {
                walletRepository.updateWalletPIN(otpCode, token!!)
                    .catch { exception ->
                        uiState = uiState.copy(isLoading = false, errorSetupPin = Event(exception.message.orEmpty()))
                    }
                    .collect {
                        uiState = uiState.copy(isLoading = false, successSetupPin = Event(Unit))
                    }
            }
        }
    }

    fun withdrawBalance(otpCode: String, spec: WalletDataTransferSpec) {
        viewModelScope.launch(Dispatchers.IO) {
            uiState = uiState.copy(isLoading = true)
            walletRepository.withdrawMoney(otpCode, spec)
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, errorSetupPin = Event(exception.message.orEmpty()))
                }.collect {
                    uiState = uiState.copy(isLoading = false, successWithdraw = Event(Unit))
                }
        }
    }
}

data class WalletPinUiState(
    val isLoading: Boolean = false,
    val successSetupPin: Event<Unit>? = null,
    val errorSetupPin: Event<String>? = null,
    val userInfo: UserInfo? = null,
    val successWithdraw: Event<Unit>? = null
)

