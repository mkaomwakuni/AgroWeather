package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import iz.est.mkao.agroweather.util.Constants

@Composable
fun WeatherIcon(
    iconCode: String,
    modifier: Modifier = Modifier,
    tint: Color? = null,
) {
    val (icon, defaultColor, description) = remember(iconCode) {
        getIconColorAndDescription(iconCode)
    }

    Icon(
        imageVector = icon,
        contentDescription = description,
        modifier = modifier.semantics {
            contentDescription = description
        },
        tint = tint ?: defaultColor,
    )
}

private data class WeatherIconInfo(
    val icon: ImageVector,
    val color: Color,
    val description: String,
)

private fun getIconColorAndDescription(iconCode: String): Triple<ImageVector, Color, String> {
    val info = when (iconCode.lowercase()) {
        // Clear conditions
        "clear-day", "clear", "sunny" -> WeatherIconInfo(
            Icons.Default.WbSunny,
            Color(0xFFFFD700), // Gold
            "Clear sunny weather"
        )
        "clear-night" -> WeatherIconInfo(
            Icons.Default.WbTwilight,
            Color(0xFF483D8B), // Dark slate blue
            "Clear night sky"
        )
        
        // Cloudy conditions
        "cloudy", "overcast" -> WeatherIconInfo(
            Icons.Default.Cloud,
            Color(0xFF696969), // Dim gray
            "Cloudy sky"
        )
        "partly-cloudy-day", "partly-cloudy" -> WeatherIconInfo(
            Icons.Default.FilterDrama,
            Color(0xFF87CEFA), // Light sky blue
            "Partly cloudy day"
        )
        "partly-cloudy-night" -> WeatherIconInfo(
            Icons.Default.FilterDrama,
            Color(0xFF2F4F4F), // Dark slate gray
            "Partly cloudy night"
        )
        
        // Rain conditions
        "rain", "showers-day", "showers-night", "light-rain" -> WeatherIconInfo(
            Icons.Default.Grain,
            Color(0xFF4169E1), // Royal blue
            "Rainy weather"
        )
        "heavy-rain", "downpour" -> WeatherIconInfo(
            Icons.Default.Opacity,
            Color(0xFF191970), // Midnight blue
            "Heavy rain"
        )
        "drizzle", "light-drizzle" -> WeatherIconInfo(
            Icons.Default.InvertColors,
            Color(0xFF4FC3F7), // Light blue
            "Light drizzle"
        )
        
        // Snow conditions
        "snow", "snow-showers-day", "snow-showers-night", "light-snow" -> WeatherIconInfo(
            Icons.Default.AcUnit,
            Color(0xFF87CEEB), // Sky blue
            "Snow"
        )
        "heavy-snow", "blizzard" -> WeatherIconInfo(
            Icons.Default.AcUnit,
            Color(0xFFB0E0E6), // Powder blue
            "Heavy snow"
        )
        "sleet", "freezing-rain" -> WeatherIconInfo(
            Icons.Default.AcUnit,
            Color(0xFF81D4FA), // Light blue
            "Sleet or freezing rain"
        )
        "hail" -> WeatherIconInfo(
            Icons.Default.Grain,
            Color(0xFF81D4FA), // Light blue
            "Hail"
        )
        
        // Storm conditions
        "thunderstorms", "thunderstorm", "lightning" -> WeatherIconInfo(
            Icons.Default.FlashOn,
            Color(0xFFFFEB3B), // Yellow
            "Thunderstorm"
        )
        "severe-thunderstorm" -> WeatherIconInfo(
            Icons.Default.FlashOn,
            Color(0xFFFF5722), // Deep orange
            "Severe thunderstorm"
        )
        
        // Wind conditions
        "wind", "windy", "breezy" -> WeatherIconInfo(
            Icons.Default.Air,
            Color(0xFF87CEEB), // Sky blue
            "Windy conditions"
        )
        "tornado" -> WeatherIconInfo(
            Icons.Default.Cyclone,
            Color(0xFF9C27B0), // Purple
            "Tornado warning"
        )
        "hurricane", "typhoon" -> WeatherIconInfo(
            Icons.Default.Cyclone,
            Color(0xFFE91E63), // Pink
            "Hurricane conditions"
        )
        
        // Visibility conditions
        "fog", "foggy" -> WeatherIconInfo(
            Icons.Default.Visibility,
            Color(0xFF708090), // Slate gray
            "Foggy conditions"
        )
        "mist", "misty" -> WeatherIconInfo(
            Icons.Default.Visibility,
            Color(0xFF708090), // Slate gray
            "Misty conditions"
        )
        "haze", "hazy" -> WeatherIconInfo(
            Icons.Default.Visibility,
            Color(0xFFBDBDBD), // Light gray
            "Hazy conditions"
        )
        
        // Dust and smoke
        "dust", "dusty", "sandstorm" -> WeatherIconInfo(
            Icons.Default.Cloud,
            Color(0xFF8D6E63), // Brown
            "Dusty conditions"
        )
        "smoke", "smoky" -> WeatherIconInfo(
            Icons.Default.Cloud,
            Color(0xFF757575), // Gray
            "Smoky conditions"
        )
        
        // Temperature extremes
        "hot", "heat-wave" -> WeatherIconInfo(
            Icons.Default.Whatshot,
            Color(0xFFFF5722), // Deep orange
            "Very hot weather"
        )
        "cold", "freeze", "frost" -> WeatherIconInfo(
            Icons.Default.AcUnit,
            Color(0xFF03A9F4), // Light blue
            "Very cold weather"
        )
        
        // Default fallback
        else -> WeatherIconInfo(
            Icons.Default.WbSunny,
            Color(0xFFFFD700), // Gold
            "Weather conditions"
        )
    }
    
    return Triple(info.icon, info.color, info.description)
}

/**
 * Get weather icon based on temperature and conditions for better context
 */
@Composable
fun WeatherIconWithTemperature(
    iconCode: String,
    temperature: Double,
    modifier: Modifier = Modifier,
) {
    val (icon, color, description) = remember(iconCode, temperature) {
        getIconColorAndDescription(iconCode).let { (baseIcon, baseColor, baseDescription) ->
            // Adjust color based on temperature
            val adjustedColor = when {
                temperature > Constants.Weather.HEAT_STRESS_THRESHOLD -> Color(0xFFFF5722) // Hot - Deep orange
                temperature < Constants.Weather.COLD_STRESS_THRESHOLD -> Color(0xFF03A9F4) // Cold - Light blue
                else -> baseColor
            }
            
            val temperatureContext = when {
                temperature > Constants.Weather.HEAT_STRESS_THRESHOLD -> " with hot temperature"
                temperature < Constants.Weather.COLD_STRESS_THRESHOLD -> " with cold temperature"
                else -> ""
            }
            
            Triple(baseIcon, adjustedColor, baseDescription + temperatureContext)
        }
    }
    
    Icon(
        imageVector = icon,
        contentDescription = description,
        modifier = modifier.semantics {
            contentDescription = description
        },
        tint = color,
    )
}
