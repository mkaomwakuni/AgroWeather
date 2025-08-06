package iz.est.mkao.agroweather.domain.usecase

import iz.est.mkao.agroweather.data.model.ChatSession
import iz.est.mkao.agroweather.data.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for managing chat sessions
 */
class ManageChatSessionsUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    /**
     * Get all chat sessions
     *
     * @return Flow of chat sessions sorted by last updated time
     */
    fun getAllSessions(): Flow<List<ChatSession>> {
        return chatRepository.getAllSessions()
    }

    /**
     * Create a new chat session
     *
     * @param initialMessage Optional initial message to start the conversation
     * @return The created session ID
     */
    suspend fun createNewSession(initialMessage: String? = null): String {
        return chatRepository.createNewSession(initialMessage)
    }

    /**
     * Delete a chat session and all its messages
     *
     * @param sessionId The ID of the session to delete
     */
    suspend fun deleteSession(sessionId: String) {
        chatRepository.deleteSession(sessionId)
    }
}
