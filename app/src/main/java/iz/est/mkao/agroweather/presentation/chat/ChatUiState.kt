package iz.est.mkao.agroweather.presentation.chat

import iz.est.mkao.agroweather.data.model.ChatMessageUi
import iz.est.mkao.agroweather.data.model.ChatSession
import iz.est.mkao.agroweather.presentation.common.LoadingState

/**
 * Represents the UI state for the chat screen with proper state management
 */
data class ChatUiState(
    val messages: List<ChatMessageUi> = emptyList(),
    val sessions: List<ChatSession> = emptyList(),

    val currentInput: String = "",
    val currentSessionId: String? = null,
    val isSessionListVisible: Boolean = false,
    val isCurrentSessionSaved: Boolean = false,

    val messagesLoadingState: LoadingState = LoadingState.Idle,
    val sendMessageLoadingState: LoadingState = LoadingState.Idle,
    val sessionsLoadingState: LoadingState = LoadingState.Idle,
    val weatherLoadingState: LoadingState = LoadingState.Idle,

    // Error states
    val error: String? = null,
) {
    val isLoading: Boolean get() =
        messagesLoadingState is LoadingState.Loading ||
            sendMessageLoadingState is LoadingState.Loading ||
            sessionsLoadingState is LoadingState.Loading ||
            weatherLoadingState is LoadingState.Loading

    val canSendMessage: Boolean get() =
        currentInput.isNotBlank() &&
            sendMessageLoadingState !is LoadingState.Loading &&
            currentSessionId != null
}

/**
 * Events that can be triggered from the chat UI
 */
sealed class ChatEvent {
    data class OnMessageSend(val message: String) : ChatEvent()
    data class OnMessageInputChange(val input: String) : ChatEvent()
    data object OnGenerateSuggestionClick : ChatEvent()
    data object OnCreateNewSession : ChatEvent()
    data class OnSessionSelect(val sessionId: String) : ChatEvent()
    data class OnSessionDelete(val sessionId: String) : ChatEvent()

    data object OnToggleSessionList : ChatEvent()
    data object OnSaveSession : ChatEvent()
    data object OnErrorDismiss : ChatEvent()
}
