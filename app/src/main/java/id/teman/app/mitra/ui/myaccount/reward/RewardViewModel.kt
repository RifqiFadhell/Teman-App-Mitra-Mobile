package id.teman.app.mitra.ui.myaccount.reward

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.common.decimalFormat
import id.teman.app.mitra.common.orZero
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.domain.model.wallet.ItemReward
import id.teman.app.mitra.domain.model.wallet.ItemRewardRedeemed
import id.teman.app.mitra.domain.model.wallet.ItemRewardTransaction
import id.teman.app.mitra.preference.Preference
import id.teman.app.mitra.repository.user.UserRepository
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
class RewardViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val preference: Preference,
    private val userRepository: UserRepository,
    private val json: Json
) : ViewModel() {

    var uiState by mutableStateOf(WalletUiState())
        private set

    fun getUserInfo(): UserInfo? {
        val userRawJson = runBlocking { preference.getUserInfo.first() }
        return userRawJson?.let { info ->
            json.decodeFromString<UserInfo>(info)
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

    fun getHistoryPoint() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            walletRepository.getHistoryPoint().catch { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = Event(exception.message ?: "Telah Terjadi Kesalahan"), rewardPoint = decimalFormat(
                        getUserInfo()?.point.orZero()
                    ))
            }.collect {
                uiState = uiState.copy(
                    isLoading = false,
                    historyPoint = it, rewardPoint = decimalFormat(
                        getUserInfo()?.point.orZero()
                    ))
            }
        }
    }

    fun initPageReward() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            walletRepository.getRewardRedeemed().catch { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error = Event(exception.message ?: "Telah Terjadi Kesalahan"),
                    rewardPoint = decimalFormat(getUserInfo()?.point.orZero())
                )
            }.collect {
                uiState = uiState.copy(
                    isLoading = false,
                    transactions = it,
                    rewardPoint = decimalFormat(getUserInfo()?.point.orZero())
                )
            }
        }
    }

    fun initPageListReward() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            walletRepository.getRewards().catch { exception ->
                uiState = uiState.copy(
                    isLoading = false,
                    error =Event(exception.message ?: "Telah Terjadi Kesalahan"),
                    rewardPoint = decimalFormat(getUserInfo()?.point.orZero())
                )
            }.collect {
                uiState = uiState.copy(
                    isLoading = false,
                    rewards = it,
                    rewardPoint = decimalFormat(getUserInfo()?.point.orZero())
                )
            }
        }
    }

    fun redeemReward(id: String) {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch {
            walletRepository.redeemReward(id).catch { exception ->
                uiState = uiState.copy(isLoading = false, error = Event(exception.message ?: "Telah Terjadi Kesalahan"))
            }.collect {
                uiState = uiState.copy(isLoading = false, successRedeem = Event(Unit))
            }
        }
    }
}

data class WalletUiState(
    val isLoading: Boolean = false,
    val error: Event<String>? = null,
    val successRedeem: Event<Unit>? = null,
    val rewardPoint: String = "",
    val historyPoint: List<ItemRewardTransaction> = emptyList(),
    val transactions: List<ItemRewardRedeemed> = emptyList(),
    val rewards: List<ItemReward> = emptyList(),
)