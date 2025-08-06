package iz.est.mkao.agroweather.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Represents a set of colors extracted from weather conditions
 * for theming UI components dynamically
 */
data class WeatherThemeColors(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val surface: Color = Color.Transparent,
    val onSurface: Color = Color.White,
)
