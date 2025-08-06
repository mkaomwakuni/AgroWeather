package iz.est.mkao.agroweather.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Weather : Screen("weather", "Weather", Icons.Default.Home)
    object Prompt : Screen("agriculture_activities", "AI Helper", Icons.Default.Search) {
        fun createRoute(cityName: String = "", latitude: Double = 0.0, longitude: Double = 0.0): String {
            val encodedCity = URLEncoder.encode(cityName.ifEmpty { " " }, StandardCharsets.UTF_8.toString())
            return "prompt/$encodedCity/$latitude/$longitude"
        }
    }
    object Chat : Screen("chat", "Activities", Icons.Default.ChatBubble) {
        fun createRoute(suggestion: String = "", cityName: String = "", latitude: Double = 0.0, longitude: Double = 0.0): String {
            val encodedSuggestion = URLEncoder.encode(suggestion.ifEmpty { " " }, StandardCharsets.UTF_8.toString())
            val encodedCity = URLEncoder.encode(cityName.ifEmpty { " " }, StandardCharsets.UTF_8.toString())
            return "chat?suggestion=$encodedSuggestion&cityName=$encodedCity&latitude=$latitude&longitude=$longitude"
        }
    }
    object Radar : Screen("radar", "Radar", Icons.Default.Map)
    object AISuggestions : Screen("ai_suggestions", "AI Suggestions", Icons.Default.AutoAwesome)
    object NewsDetails : Screen("news_details", "News Details", Icons.Default.Article) {
        fun createRoute(articleId: String): String {
            return "news_details/$articleId"
        }
    }

    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    Screen.Weather,
    Screen.AISuggestions,
    Screen.Prompt,
    Screen.Radar,
    Screen.Settings,
)
