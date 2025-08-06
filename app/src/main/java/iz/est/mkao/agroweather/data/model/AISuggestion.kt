package iz.est.mkao.agroweather.data.model

import java.time.LocalDate

/**
 * Represents a farming suggestion generated based on weather and soil data
 */
data class AISuggestion(
    val id: String,
    val title: String,
    val description: String,
    val suggestionType: SuggestionType,
    val relevantDate: LocalDate,
    val priority: SuggestionPriority,
    val weatherConditions: String,
    val soilConditions: String?,
    val actions: List<String>,
)

/**
 * Types of suggestions that can be provided to farmers
 */
enum class SuggestionType {
    IRRIGATION,
    PEST_CONTROL,
    FERTILIZATION,
    PLANTING,
    HARVESTING,
    FROST_PROTECTION,
    HEAT_PROTECTION,
    GENERAL,
}

/**
 * Priority levels for suggestions
 */
enum class SuggestionPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT,
}
