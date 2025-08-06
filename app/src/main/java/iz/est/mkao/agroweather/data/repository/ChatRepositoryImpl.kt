package iz.est.mkao.agroweather.data.repository

import android.R.attr.text
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import iz.est.mkao.agroweather.data.local.ChatDao
import iz.est.mkao.agroweather.data.model.ChatMessage
import iz.est.mkao.agroweather.data.model.ChatMessageUi
import iz.est.mkao.agroweather.data.model.ChatSession
import iz.est.mkao.agroweather.data.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val generativeModel: GenerativeModel,
) : ChatRepository {

    override suspend fun sendMessage(
        message: String,
        sessionId: String,
        weatherContext: WeatherResponse?,
    ): Result<ChatMessageUi> {
        return try {
            // Save user message to database
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = message,
                timestamp = LocalDateTime.now(),
                isFromUser = true,
                weatherContext = weatherContext?.toString(),
                sessionId = sessionId,
            )
            chatDao.insertMessage(userMessage)

            // Update session last activity time
            chatDao.getSessionById(sessionId)?.let { session ->
                chatDao.insertSession(session.copy(lastUpdatedAt = LocalDateTime.now()))
            }

            // Create prompt with weather context if available
            val prompt = buildPrompt(message, weatherContext)

            // Send message to Gemini AI
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                },
            )

            // Save AI response to database
            val aiResponse = saveAiResponse(response, sessionId)

            Result.success(aiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMessagesForSession(sessionId: String): Flow<List<ChatMessageUi>> {
        return chatDao.getMessagesForSession(sessionId).map { messages ->
            messages.map { it.toUiModel() }
        }
    }

    override fun getAllSessions(): Flow<List<ChatSession>> {
        return chatDao.getAllSessions()
    }

    override suspend fun createNewSession(initialMessage: String?): String {
        val sessionId = UUID.randomUUID().toString()
        val now = LocalDateTime.now()

        val session = ChatSession(
            id = sessionId,
            title = initialMessage?.take(30)?.plus("...") ?: "New Chat",
            createdAt = now,
            lastUpdatedAt = now,
        )

        if (initialMessage != null) {
            val message = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = initialMessage,
                timestamp = now,
                isFromUser = true,
                sessionId = sessionId,
            )
            chatDao.createNewSessionWithFirstMessage(session, message)

            // Generate AI response to initial message
            sendMessage(initialMessage, sessionId)
        } else {
            chatDao.insertSession(session)
        }

        return sessionId
    }

    override suspend fun deleteSession(sessionId: String) {
        chatDao.deleteSessionWithMessages(sessionId)
    }

    override suspend fun generateFarmingSuggestion(
        weatherData: WeatherResponse,
        sessionId: String,
    ): Result<ChatMessageUi> {
        return try {
            val weatherSummary = buildWeatherSummary(weatherData)

            val prompt = """
                As a farming assistant, provide actionable suggestions and advice based on the following weather data:
                
                $weatherSummary
                
                Please provide:
                1. Key observations about current conditions
                2. Specific farming activities recommended for these conditions
                3. Any potential issues or concerns to be aware of
                4. Preparation advice for upcoming weather changes
            """.trimIndent()

            // Send message to Gemini AI
            val response = generativeModel.generateContent(
                content {
                    text(prompt)
                },
            )

            // Save AI response to database with a system-generated user query
            val userQuery = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = "Generate farming suggestions based on current weather",
                timestamp = LocalDateTime.now(),
                isFromUser = true,
                weatherContext = weatherData.toString(),
                sessionId = sessionId,
            )
            chatDao.insertMessage(userQuery)

            // Save AI response
            val aiResponse = saveAiResponse(response, sessionId)

            Result.success(aiResponse)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun buildPrompt(userMessage: String, weatherContext: WeatherResponse?): String {
        return if (weatherContext != null) {
            val weatherSummary = buildWeatherSummary(weatherContext)
            """
            You are an AI farming assistant that helps farmers with agricultural advice.
            
            Current weather context:
            $weatherSummary
            
            User query: $userMessage
            
            Please provide helpful, specific advice based on the weather conditions.
            """.trimIndent()
        } else {
            """
            You are an AI farming assistant that helps farmers with agricultural advice.
            
            User query: $userMessage
            
            Please provide helpful, specific advice for farmers.
            """.trimIndent()
        }
    }

    private fun buildWeatherSummary(weatherData: WeatherResponse): String {
        val current = weatherData.currentConditions
        return """
            Location: ${weatherData.resolvedAddress}
            Current Temperature: ${current.temp}°C
            Feels Like: ${current.feelslike}°C
            Humidity: ${current.humidity}%
            Wind Speed: ${current.windspeed} km/h
            Wind Direction: ${current.winddir}°
            Conditions: ${current.conditions}
            Precipitation: ${current.precip} mm
            Precipitation Probability: ${current.precipprob}%
            ${current.soilmoisture?.let { "Soil Moisture: ${it * 100}%" } ?: ""}
            ${current.soiltemp?.let { "Soil Temperature: $it°C" } ?: ""}
            
            Forecast: ${weatherData.description ?: ""}
        """.trimIndent()
    }

    private suspend fun saveAiResponse(
        response: GenerateContentResponse,
        sessionId: String,
    ): ChatMessageUi {
        val responseText = response.text ?: "No response generated"

        val aiMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = responseText,
            timestamp = LocalDateTime.now(),
            isFromUser = false,
            sessionId = sessionId,
        )

        chatDao.insertMessage(aiMessage)

        return aiMessage.toUiModel()
    }

    private fun ChatMessage.toUiModel(): ChatMessageUi {
        return ChatMessageUi(
            id = id,
            content = content,
            timestamp = timestamp,
            isFromUser = isFromUser,
        )
    }
}
