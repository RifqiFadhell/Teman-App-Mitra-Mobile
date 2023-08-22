package id.teman.app.mitra.ui.wallet.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.domain.model.wallet.WalletHistoryItemSpec
import id.teman.app.mitra.repository.wallet.WalletRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
): ViewModel() {

    var uiState by mutableStateOf(WalletUiState())
    private set

    fun initPage() {
        uiState = uiState.copy(isLoading = true)
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.getWalletBalance()
                .zip(walletRepository.getWalletTransactions()) { balance, histories ->
                return@zip WalletSpec(balance, histories)
            }.flowOn(Dispatchers.IO)
                .catch { exception ->
                    uiState = uiState.copy(isLoading = false, error = Event(exception.message ?: "telah terjadi kesalahan"))
                }.collect {
                    uiState = uiState.copy(isLoading = false, balance = it.balance, transactions = it.transactions)
                }
        }
    }
}

data class WalletSpec(
    val balance: Double = 0.0,
    val transactions: List<WalletHistoryItemSpec> = emptyList()
)

data class WalletUiState(
    val isLoading: Boolean = false,
    val balance: Double = 0.0,
    val transactions: List<WalletHistoryItemSpec> = emptyList(),
    val error: Event<String>? = null
)