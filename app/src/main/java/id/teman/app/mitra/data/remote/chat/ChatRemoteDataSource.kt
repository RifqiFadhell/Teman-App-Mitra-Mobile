package id.teman.app.mitra.data.remote.chat

import id.teman.app.mitra.data.dto.chat.ChatRequestDto
import id.teman.app.mitra.data.dto.chat.ChatResponseDto
import id.teman.app.mitra.data.dto.chat.SendMessageResponseDto
import id.teman.app.mitra.data.remote.ApiServiceInterface
import id.teman.app.mitra.data.remote.handleRequestOnFlow
import kotlinx.coroutines.flow.Flow

interface ChatRemoteDataSource {
    suspend fun getChatMessages(requestId: String): Flow<ChatResponseDto>
    suspend fun sendMessage(
        requestId: String,
        requestDto: ChatRequestDto
    ): Flow<SendMessageResponseDto>
}

class DefaultChatRemoteDataSource(
    private val httpClient: ApiServiceInterface
) : ChatRemoteDataSource {
    override suspend fun getChatMessages(requestId: String): Flow<ChatResponseDto> =
        handleRequestOnFlow { httpClient.getChatMessages(requestId) }

    override suspend fun sendMessage(
        requestId: String,
        requestDto: ChatRequestDto
    ): Flow<SendMessageResponseDto> =
        handleRequestOnFlow { httpClient.sendChatMessage(requestDto, requestId) }

}