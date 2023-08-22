package id.teman.app.mitra.ui.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.common.Event
import id.teman.app.mitra.common.convertToAllowedIndonesianNumber
import id.teman.app.mitra.repository.user.UserRepository
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    var uiState by mutableStateOf(LoginUiEvent())
        private set

    fun doLogin(number: String) = viewModelScope.launch {
        val editedNumber = number.convertToAllowedIndonesianNumber()
        uiState = uiState.copy(loading = true)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                val rootCauseMessage = when (task.exception) {
                    is java.io.IOException -> "Tidak di temukan internet. Silahkan coba lagi"
                    else -> task.exception?.message ?: "Gagal mendapatkan token"
                }
                uiState = uiState.copy(
                    loading = false,
                    error = rootCauseMessage
                )
                return@OnCompleteListener
            }
            val token = task.result
            viewModelScope.launch {
                delay(1000)
                userRepository.login(editedNumber, token.orEmpty())
                    .catch { exception ->
                        uiState = uiState.copy(
                            loading = false,
                            error = exception.message.orEmpty()
                        )
                    }
                    .collect {
                        uiState = uiState.copy(loading = false, SuccessLogin = Event(Unit))
                    }
            }
        })
    }

    fun updateErrorMessage(errorMessage: String) {
        if (errorMessage.isNotEmpty()) {
            uiState = uiState.copy(error = "")
        }
    }
}

data class LoginUiEvent(
    val loading: Boolean = false,
    val error: String = "",
    val SuccessLogin: Event<Unit>? = null
)