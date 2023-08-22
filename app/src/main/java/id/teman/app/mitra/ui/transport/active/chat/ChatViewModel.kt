package id.teman.app.mitra.ui.transport.active.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.teman.app.mitra.domain.model.chat.ChatMessageSpec
import id.teman.app.mitra.domain.model.user.UserInfo
import id.teman.app.mitra.preference.Preference
import id.teman.app.mitra.repository.chat.ChatRepository
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val json: Json,
    private val preference: Preference
): ViewModel() {
    var isChatPollerActive by mutableStateOf(true)
        private set

    var chatUiState by mutableStateOf(ChatUiSpec())
        private set

    fun stopEmitData() {
        isChatPollerActive = false
    }

    fun initData(requestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            chatUiState = chatUiState.copy(loading = true)
            var userInfo: UserInfo? = null
            val userInfoJson = runBlocking {  preference.getUserInfo.first() }
            if (userInfoJson.isNotBlank()) {
                userInfo = json.decodeFromString<UserInfo>(userInfoJson)
            }
            if (!isChatPollerActive) isChatPollerActive = true

            while (isChatPollerActive) {
                delay(3000)
                chatRepository.getChatMessages(userInfo!!.userId, requestId)
                    .catch { chatUiState = chatUiState.copy(loading = false) }
                    .collect {
                        chatUiState = chatUiState.copy(loading = false, chatMessages = it)
                    }
            }
        }
    }

    fun sendMessage(requestId: String, text: String) {
        val userInfoJson = runBlocking {  preference.getUserInfo.first() }
        if (userInfoJson.isNotBlank()) {
            val userInfo = json.decodeFromString<UserInfo>(userInfoJson)
            viewModelScope.launch(Dispatchers.IO) {
                chatRepository.sendChatMessage(userInfo.userId, requestId, text)
                    .catch { exception ->
                        print(exception)
                    }.collect { item ->
                        val chatMessage = chatUiState.chatMessages.toMutableList()
                        chatMessage.add(item)
                        chatUiState = chatUiState.copy(loading = false, chatMessages = chatMessage)
                    }
            }
        }
    }
}

data class ChatUiSpec(
    val loading: Boolean = false,
    val chatMessages: List<ChatMessageSpec> = emptyList(),
)