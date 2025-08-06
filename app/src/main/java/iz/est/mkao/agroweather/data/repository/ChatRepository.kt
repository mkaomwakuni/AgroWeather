package iz.est.mkao.agroweather.data.repository

import iz.est.mkao.agroweather.data.model.ChatMessageUi
import iz.est.mkao.agroweather.data.model.ChatSession
import iz.est.mkao.agroweather.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for chat operations
 */
interface ChatRepository {
    /**
     * Send a message to the AI model and get a response
     * @param message The message to send
     * @param sessionId The current chat session ID
     * @param weatherContext Optional weather data to provide context for the AI
     * @return The AI response message
     */
    suspend fun sendMessage(
        message: String,
        sessionId: String,
        weatherContext: WeatherResponse? = null,
    ): Result<ChatMessageUi>

    /**
     * Get all messages for a specific chat session
     * @param sessionId The chat session ID
     * @return Flow of list of chat messages
     */
    fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageUi>>

    /**
     * Get all chat sessions
     * @return Flow of list of chat sessions
     */
    fun getAllSessions(): Flow<List<ChatSession>>

    /**
     * Create a new chat session
     * @param initialMessage Optional initial message to start the conversation
     * @return The created session ID
     */
    suspend fun createNewSession(initialMessage: String? = null): String

    /**
     * Delete a chat session and all its messages
     * @param sessionId The ID of the session to delete
     */
    suspend fun deleteSession(sessionId: String)

    /**
     * Generate a farming suggestion based on weather data
     * @param weatherData Current weather data
     * @param sessionId The current chat session ID
     * @return AI-generated suggestion
     */
    suspend fun generateFarmingSuggestion(
        weatherData: WeatherResponse,
        sessionId: String,
    ): Result<ChatMessageUi>
}
