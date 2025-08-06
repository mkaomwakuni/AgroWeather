package iz.est.mkao.agroweather.presentation.chat

import android.util.Log
import iz.est.mkao.agroweather.util.SecureLogger
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iz.est.mkao.agroweather.AgroWeatherApp
import iz.est.mkao.agroweather.data.model.WeatherResponse
import iz.est.mkao.agroweather.data.preferences.UserPreferences
import iz.est.mkao.agroweather.domain.repository.WeatherRepository
import iz.est.mkao.agroweather.domain.usecase.GenerateFarmingSuggestionsUseCase
import iz.est.mkao.agroweather.domain.usecase.GetChatMessagesUseCase
import iz.est.mkao.agroweather.domain.usecase.ManageChatSessionsUseCase
import iz.est.mkao.agroweather.domain.usecase.SendMessageUseCase
import iz.est.mkao.agroweather.presentation.common.LoadingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val manageChatSessionsUseCase: ManageChatSessionsUseCase,
    private val generateFarmingSuggestionsUseCase: GenerateFarmingSuggestionsUseCase,
    private val weatherRepository: WeatherRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    companion object {
        private const val TAG = "ChatViewModel"
    }

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private var currentWeatherData: WeatherResponse? = null

    init {
        // Immediately create a session synchronously to avoid timing issues
        createInitialSession()

        viewModelScope.launch {
            // Load sessions
            loadChatSessions()

            // Try to fetch current weather data (will be used for context)
            fetchCurrentWeather()
        }
    }

    /**
     * Create initial session immediately to avoid timing issues with navigation
     */
    private fun createInitialSession() {
        if (_uiState.value.currentSessionId == null) {
            viewModelScope.launch {
                try {
                    val sessionId = manageChatSessionsUseCase.createNewSession()
                    _uiState.update { it.copy(currentSessionId = sessionId) }

                    if (AgroWeatherApp.DEBUG_MODE) {
                        Log.d(TAG, "Initial session created: $sessionId")
                    }

                    // Load messages for the new session
                    loadMessagesForCurrentSession()
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create initial session: ${e.message}", e)
                    _uiState.update { it.copy(error = "Failed to initialize chat session") }
                }
            }
        }
    }

    /**
     * Handle chat-related events from the UI
     */
    fun onEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.OnMessageInputChange -> {
                _uiState.update { it.copy(currentInput = event.input) }
            }

            is ChatEvent.OnMessageSend -> {
                sendMessage(event.message)
            }

            is ChatEvent.OnGenerateSuggestionClick -> {
                generateFarmingSuggestion()
            }

            is ChatEvent.OnCreateNewSession -> {
                viewModelScope.launch {
                    val sessionId = manageChatSessionsUseCase.createNewSession()
                    _uiState.update {
                        it.copy(
                            currentSessionId = sessionId,
                            isSessionListVisible = false,
                        )
                    }

                    // Load messages for the new session
                    loadMessagesForCurrentSession()
                }
            }

            is ChatEvent.OnSessionSelect -> {
                _uiState.update {
                    it.copy(
                        currentSessionId = event.sessionId,
                        isSessionListVisible = false,
                    )
                }

                // Load messages for the selected session
                loadMessagesForCurrentSession()
            }

            is ChatEvent.OnSessionDelete -> {
                viewModelScope.launch {
                    manageChatSessionsUseCase.deleteSession(event.sessionId)

                    // If we deleted the current session, create a new one
                    if (_uiState.value.currentSessionId == event.sessionId) {
                        val newSessionId = manageChatSessionsUseCase.createNewSession()
                        _uiState.update { it.copy(currentSessionId = newSessionId) }
                        loadMessagesForCurrentSession()
                    }
                }
            }

            is ChatEvent.OnToggleSessionList -> {
                _uiState.update { it.copy(isSessionListVisible = !it.isSessionListVisible) }
            }

            is ChatEvent.OnSaveSession -> {
                saveCurrentSession()
            }

            is ChatEvent.OnErrorDismiss -> {
                _uiState.update { it.copy(error = null) }
            }
        }
    }

    /**
     * Send a message to the AI assistant with improved error handling
     */
    private fun sendMessage(message: String) {
        if (AgroWeatherApp.DEBUG_MODE) {
            SecureLogger.d(TAG, "Sending message: [MESSAGE_CONTENT_REDACTED]")
        }

        // Clear input field and set loading state immediately for responsiveness
        _uiState.update {
            it.copy(
                currentInput = "",
                sendMessageLoadingState = LoadingState.Loading,
                error = null,
            )
        }

        // If no session exists yet, wait for it or create one
        val currentSessionId = _uiState.value.currentSessionId
        if (currentSessionId == null) {
            if (AgroWeatherApp.DEBUG_MODE) {
                Log.d(TAG, "No session available, creating one first...")
            }

            viewModelScope.launch {
                try {
                    val sessionId = manageChatSessionsUseCase.createNewSession()
                    _uiState.update { it.copy(currentSessionId = sessionId) }

                    if (AgroWeatherApp.DEBUG_MODE) {
                        Log.d(TAG, "Created new session for message: $sessionId")
                    }

                    // Now send the message with the new session
                    sendMessageWithSession(message, sessionId)
                } catch (e: Exception) {
                    val errorMessage = "Failed to create session: ${e.localizedMessage}"
                    _uiState.update { state ->
                        state.copy(
                            sendMessageLoadingState = LoadingState.Error(errorMessage, e),
                            error = errorMessage,
                        )
                    }
                    Log.e(TAG, errorMessage, e)
                }
            }
            return
        }

        // Send message with existing session
        sendMessageWithSession(message, currentSessionId)
    }

    /**
     * Send message with a guaranteed session ID
     */
    private fun sendMessageWithSession(message: String, sessionId: String) {
        viewModelScope.launch {
            try {
                sendMessageUseCase(
                    message = message,
                    sessionId = sessionId,
                    weatherData = currentWeatherData,
                )
                    .onSuccess {
                        _uiState.update { state ->
                            state.copy(
                                sendMessageLoadingState = LoadingState.Success,
                            )
                        }
                        if (AgroWeatherApp.DEBUG_MODE) {
                            Log.d(TAG, "Message sent successfully")
                        }
                        // Reload messages to show the new user message and AI response
                        loadMessagesForCurrentSession()
                    }
                    .onFailure { error ->
                        val errorMessage = "Failed to send message: ${error.localizedMessage}"
                        _uiState.update { state ->
                            state.copy(
                                sendMessageLoadingState = LoadingState.Error(errorMessage, error),
                                error = errorMessage,
                            )
                        }
                        Log.e(TAG, errorMessage, error)
                    }
            } catch (e: Exception) {
                val errorMessage = "Unexpected error: ${e.localizedMessage}"
                _uiState.update { state ->
                    state.copy(
                        sendMessageLoadingState = LoadingState.Error(errorMessage, e),
                        error = errorMessage,
                    )
                }
                Log.e(TAG, errorMessage, e)
            }
        }
    }

    /**
     * Generate farming suggestions based on current weather
     */
    private fun generateFarmingSuggestion() {
        val weatherData = currentWeatherData ?: run {
            val errorMessage = "No weather data available for suggestions. Please wait for weather data to load."
            _uiState.update {
                it.copy(
                    error = errorMessage,
                    sendMessageLoadingState = LoadingState.Error(errorMessage),
                )
            }
            return
        }

        val currentSessionId = _uiState.value.currentSessionId ?: return

        if (AgroWeatherApp.DEBUG_MODE) {
            Log.d(TAG, "Generating farming suggestion for location: ${weatherData.resolvedAddress}")
        }

        _uiState.update {
            it.copy(
                sendMessageLoadingState = LoadingState.Loading,
                error = null,
            )
        }

        viewModelScope.launch {
            try {
                generateFarmingSuggestionsUseCase(weatherData, currentSessionId)
                    .onSuccess {
                        _uiState.update { state ->
                            state.copy(sendMessageLoadingState = LoadingState.Success)
                        }
                        if (AgroWeatherApp.DEBUG_MODE) {
                            Log.d(TAG, "Farming suggestion generated successfully")
                        }
                        // Reload messages to show the new AI suggestion
                        loadMessagesForCurrentSession()
                    }
                    .onFailure { error ->
                        val errorMessage = "Failed to generate suggestion: ${error.localizedMessage}"
                        _uiState.update { state ->
                            state.copy(
                                sendMessageLoadingState = LoadingState.Error(errorMessage, error),
                                error = errorMessage,
                            )
                        }
                        Log.e(TAG, errorMessage, error)
                    }
            } catch (e: Exception) {
                val errorMessage = "Unexpected error generating suggestion: ${e.localizedMessage}"
                _uiState.update { state ->
                    state.copy(
                        sendMessageLoadingState = LoadingState.Error(errorMessage, e),
                        error = errorMessage,
                    )
                }
                Log.e(TAG, errorMessage, e)
            }
        }
    }

    /**
     * Load chat sessions
     */
    private fun loadChatSessions() {
        viewModelScope.launch {
            manageChatSessionsUseCase.getAllSessions()
                .catch { error ->
                    _uiState.update { it.copy(error = "Failed to load sessions: ${error.message}") }
                }
                .collect { sessions ->
                    _uiState.update { it.copy(sessions = sessions) }
                }
        }
    }

    /**
     * Load messages for the current session
     */
    private fun loadMessagesForCurrentSession() {
        val sessionId = _uiState.value.currentSessionId ?: return

        viewModelScope.launch {
            getChatMessagesUseCase(sessionId)
                .catch { error ->
                    _uiState.update { it.copy(error = "Failed to load messages: ${error.message}") }
                }
                .collect { messages ->
                    _uiState.update { it.copy(messages = messages) }
                }
        }
    }

    /**
     * Fetch current weather data to use as context for AI with proper error handling
     */
    private fun fetchCurrentWeather() {
        val locationSettings = userPreferences.getLocationSettings()
        val location = if (locationSettings.useCurrentLocation) {
            // Use current location coordinates
            "${locationSettings.defaultLatitude},${locationSettings.defaultLongitude}"
        } else {
            // Use selected location
            locationSettings.defaultLocation
        }
        
        if (AgroWeatherApp.DEBUG_MODE) {
            Log.d(TAG, "Fetching current weather for location: $location")
        }

        _uiState.update { it.copy(weatherLoadingState = LoadingState.Loading) }

        viewModelScope.launch {
            try {
                weatherRepository.getCurrentWeather(location)
                    .onSuccess { weatherResponse ->
                        currentWeatherData = weatherResponse
                        _uiState.update {
                            it.copy(weatherLoadingState = LoadingState.Success)
                        }
                        if (AgroWeatherApp.DEBUG_MODE) {
                            Log.d(TAG, "Weather data fetched successfully for ${weatherResponse.resolvedAddress}")
                        }
                    }
                    .onFailure { error ->
                        val errorMessage = "Failed to fetch weather data: ${error.localizedMessage}"
                        _uiState.update {
                            it.copy(weatherLoadingState = LoadingState.Error(errorMessage, error))
                        }
                        Log.w(TAG, errorMessage, error)

                    }
            } catch (e: Exception) {
                val errorMessage = "Error fetching weather data: ${e.localizedMessage}"
                _uiState.update {
                    it.copy(weatherLoadingState = LoadingState.Error(errorMessage, e))
                }
                Log.w(TAG, errorMessage, e)
            }
        }
    }

    /**
     * Save the current session to favorites
     */
    private fun saveCurrentSession() {
        val currentSessionId = _uiState.value.currentSessionId ?: return
        val messages = _uiState.value.messages

        if (messages.isEmpty()) {
            _uiState.update { it.copy(error = "Cannot save empty chat session") }
            return
        }

        viewModelScope.launch {
            try {
                // Mark session as saved in UI state
                _uiState.update { it.copy(isCurrentSessionSaved = true) }

                if (AgroWeatherApp.DEBUG_MODE) {
                    Log.d(TAG, "Session $currentSessionId marked as saved")
                }
            } catch (e: Exception) {
                val errorMessage = "Failed to save session: ${e.localizedMessage}"
                _uiState.update { it.copy(error = errorMessage) }
                Log.e(TAG, errorMessage, e)
            }
        }
    }
}
