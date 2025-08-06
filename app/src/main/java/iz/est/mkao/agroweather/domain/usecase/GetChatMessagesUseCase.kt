package iz.est.mkao.agroweather.domain.usecase

import iz.est.mkao.agroweather.data.model.ChatMessageUi
import iz.est.mkao.agroweather.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all messages in a chat session
 */
class GetChatMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    /**
     * Get all messages for a specific chat session
     *
     * @param sessionId The chat session ID
     * @return Flow of chat messages
     */
    operator fun invoke(sessionId: String): Flow<List<ChatMessageUi>> {
        return chatRepository.getMessagesForSession(sessionId)
    }
}
