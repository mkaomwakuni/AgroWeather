package iz.est.mkao.agroweather.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Represents a chat message in the conversation between the user and the AI
 */
@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey
    val id: String,
    val content: String,
    val timestamp: LocalDateTime,
    val isFromUser: Boolean,
    val weatherContext: String? = null,
    val sessionId: String,
)

/**
 * Domain model for chat message
 */
data class ChatMessageUi(
    val id: String,
    val content: String,
    val timestamp: LocalDateTime,
    val isFromUser: Boolean,
)

/**
 * Chat session entity for database
 */
@Entity(tableName = "chat_sessions")
data class ChatSession(
    @PrimaryKey
    val id: String,
    val title: String,
    val createdAt: LocalDateTime,
    val lastUpdatedAt: LocalDateTime,
)
