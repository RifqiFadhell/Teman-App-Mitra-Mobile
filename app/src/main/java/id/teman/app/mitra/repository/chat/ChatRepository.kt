package id.teman.app.mitra.repository.chat

import id.teman.app.mitra.data.dto.chat.ChatRequestDto
import id.teman.app.mitra.data.remote.chat.ChatRemoteDataSource
import id.teman.app.mitra.domain.model.chat.ChatMessageSpec
import id.teman.app.mitra.ui.transport.common.getChatCurrentTime
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class ChatRepository @Inject constructor(
    private val chatRemoteDataSource: ChatRemoteDataSource
) {

    suspend fun getChatMessages(
        currentUserId: String,
        requestId: String
    ): Flow<List<ChatMessageSpec>> = flow {
        chatRemoteDataSource.getChatMessages(requestId)
            .flowOn(Dispatchers.IO)
            .catch { exception -> throw exception }
            .collect {
                it.data?.let { messages ->
                    val chatMessages = messages.map { message ->
                        ChatMessageSpec(
                            isSelfMessage = message.userId == currentUserId,
                            sendTime = getChatCurrentTime(message.sentTime),
                            message = message.text.orEmpty()
                        )
                    }
                    emit(chatMessages)
                }
            }
    }

    suspend fun sendChatMessage(
        currentUserId: String,
        requestId: String,
        message: String
    ): Flow<ChatMessageSpec> =
        chatRemoteDataSource.sendMessage(requestId, ChatRequestDto(message)).flowOn(Dispatchers.IO).map {
            ChatMessageSpec(
                isSelfMessage = currentUserId == it.userId,
                sendTime = getChatCurrentTime(it.sentTime),
                message = it.text.orEmpty()
            )
        }
}