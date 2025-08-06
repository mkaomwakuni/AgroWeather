package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Provides dynamic weather gradients based on weather conditions and temperature
 */
object WeatherGradientProvider {
    
    @Composable
    fun getWeatherGradient(iconCode: String, temperature: Double): Brush {
        return remember(iconCode, temperature) {
            when {
                // Sunny/Clear conditions - Hot
                iconCode.contains("clear") && temperature > 30 -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFD700), // Gold
                            Color(0xFFFF8C00), // Dark orange
                            Color(0xFFFF6B35), // Red orange
                            Color(0x33FFFFFF)
                        )
                    )
                }
                // Sunny/Clear conditions - Warm
                iconCode.contains("clear") && temperature > 20 -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFD700), // Gold
                            Color(0xFFFFA726), // Orange
                            Color(0xFF42A5F5), // Light blue
                            Color(0x33FFFFFF)
                        )
                    )
                }
                // Clear but cool
                iconCode.contains("clear") -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF87CEEB), // Sky blue
                            Color(0xFF4682B4), // Steel blue
                            Color(0xFF1E88E5), // Blue
                            Color(0x33FFFFFF)
                        )
                    )
                }
                // Rainy conditions
                iconCode.contains("rain") || iconCode.contains("showers") -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF708090), // Slate gray
                            Color(0xFF4682B4), // Steel blue
                            Color(0xFF2F4F4F), // Dark slate gray
                            Color(0x4DFFFFFF)
                        )
                    )
                }
                // Thunderstorm conditions
                iconCode.contains("thunder") || iconCode.contains("storm") -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2F2F2F), // Dark gray
                            Color(0xFF4B0082), // Indigo
                            Color(0xFF8B008B), // Dark magenta
                            Color(0x66FFFFFF)
                        )
                    )
                }
                // Snow conditions
                iconCode.contains("snow") -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF0F8FF), // Alice blue
                            Color(0xFFE6E6FA), // Lavender
                            Color(0xFFD3D3D3), // Light gray
                            Color(0x33FFFFFF)
                        )
                    )
                }
                // Cloudy/Overcast conditions
                iconCode.contains("cloudy") || iconCode.contains("overcast") -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFB0C4DE), // Light steel blue
                            Color(0xFF778899), // Light slate gray
                            Color(0xFF696969), // Dim gray
                            Color(0x4DFFFFFF)
                        )
                    )
                }
                // Partly cloudy conditions
                iconCode.contains("partly") -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF87CEFA), // Light sky blue
                            Color(0xFF4A90E2), // Blue
                            Color(0xFF1976D2), // Primary blue
                            Color(0x33FFFFFF)
                        )
                    )
                }
                // Night conditions
                iconCode.contains("night") -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF191970), // Midnight blue
                            Color(0xFF483D8B), // Dark slate blue
                            Color(0xFF2F2F4F), // Dark slate gray
                            Color(0x66FFFFFF)
                        )
                    )
                }
                // Fog/Mist conditions
                iconCode.contains("fog") || iconCode.contains("mist") -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF5F5F5), // White smoke
                            Color(0xFFDCDCDC), // Gainsboro
                            Color(0xFF708090), // Slate gray
                            Color(0x4DFFFFFF)
                        )
                    )
                }
                // Windy conditions
                iconCode.contains("wind") -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF87CEEB), // Sky blue
                            Color(0xFF4682B4), // Steel blue
                            Color(0xFF2F4F4F), // Dark slate gray
                            Color(0x33FFFFFF)
                        )
                    )
                }
                // Default gradient for unknown conditions
                else -> {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF4A90E2), // Default blue
                            Color(0xFF7BB3F7), // Light blue
                            Color(0x33FFFFFF)
                        )
                    )
                }
            }
        }
    }
    
    /**
     * Get gradient colors for specific weather condition types
     */
    fun getConditionColors(condition: String): List<Color> {
        return when {
            condition.contains("clear", ignoreCase = true) -> listOf(
                Color(0xFFFFD700), Color(0xFFFF8C00), Color(0xFFFF6B35)
            )
            condition.contains("rain", ignoreCase = true) || 
            condition.contains("shower", ignoreCase = true) -> listOf(
                Color(0xFF708090), Color(0xFF4682B4), Color(0xFF2F4F4F)
            )
            condition.contains("cloud", ignoreCase = true) -> listOf(
                Color(0xFFB0C4DE), Color(0xFF778899), Color(0xFF696969)
            )
            condition.contains("storm", ignoreCase = true) || 
            condition.contains("thunder", ignoreCase = true) -> listOf(
                Color(0xFF2F2F2F), Color(0xFF4B0082), Color(0xFF8B008B)
            )
            condition.contains("snow", ignoreCase = true) -> listOf(
                Color(0xFFF0F8FF), Color(0xFFE6E6FA), Color(0xFFD3D3D3)
            )
            else -> listOf(
                Color(0xFF4A90E2), Color(0xFF7BB3F7), Color(0xFF87CEEB)
            )
        }
    }
}
