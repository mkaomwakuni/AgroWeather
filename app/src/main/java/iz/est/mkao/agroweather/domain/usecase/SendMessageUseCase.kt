package iz.est.mkao.agroweather.domain.usecase

import iz.est.mkao.agroweather.data.model.ChatMessageUi
import iz.est.mkao.agroweather.data.model.WeatherResponse
import iz.est.mkao.agroweather.data.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for sending a message to the AI assistant
 */
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    /**
     * Send a message to the AI assistant
     *
     * @param message The message content
     * @param sessionId The chat session ID
     * @param weatherData Optional weather data to provide context
     * @return Result containing the AI response
     */
    suspend operator fun invoke(
        message: String,
        sessionId: String,
        weatherData: WeatherResponse? = null,
    ): Result<ChatMessageUi> {
        if (message.isBlank()) {
            return Result.failure(IllegalArgumentException("Message cannot be empty"))
        }

        return chatRepository.sendMessage(message, sessionId, weatherData)
    }
}
