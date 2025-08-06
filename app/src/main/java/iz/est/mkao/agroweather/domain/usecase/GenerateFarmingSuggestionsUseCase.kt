package iz.est.mkao.agroweather.domain.usecase

import iz.est.mkao.agroweather.data.model.ChatMessageUi
import iz.est.mkao.agroweather.data.model.WeatherResponse
import iz.est.mkao.agroweather.data.repository.ChatRepository
import javax.inject.Inject

/**
 * Use case for generating farming suggestions based on weather data
 */
class GenerateFarmingSuggestionsUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    /**
     * Generate AI-powered farming suggestions based on current weather data
     *
     * @param weatherData Current weather and forecast data
     * @param sessionId Current chat session ID
     * @return Result containing the AI-generated suggestions
     */
    suspend operator fun invoke(
        weatherData: WeatherResponse,
        sessionId: String,
    ): Result<ChatMessageUi> {
        return chatRepository.generateFarmingSuggestion(weatherData, sessionId)
    }
}
