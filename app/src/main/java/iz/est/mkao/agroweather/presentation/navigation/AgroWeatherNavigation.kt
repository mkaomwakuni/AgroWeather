package iz.est.mkao.agroweather.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// Dynamic color schemes for different screens
object DynamicColors {
    val weatherColors = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2196F3), // Blue
            Color(0xFF03DAC5), // Teal
        ),
    )

    val aiSuggestionsColors = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF9C27B0), // Purple
            Color(0xFFE91E63), // Pink
        ),
    )

    val chatColors = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2196F3), // Blue
            Color(0xFF03DAC5), // Teal
        ),
    )

    val favoritesColors = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFF9800), // Orange
            Color(0xFFFFEB3B), // Yellow
        ),
    )

    val settingsColors = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF607D8B), // Blue Grey
            Color(0xFF9E9E9E), // Grey
        ),
    )

    val radarColors = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4CAF50), // Green
            Color(0xFF2196F3), // Blue
        ),
    )

    fun getColorForScreen(route: String?): Brush {
        return when (route) {
            Screen.Weather.route -> weatherColors
            Screen.AISuggestions.route -> aiSuggestionsColors
            Screen.Chat.route -> chatColors
            Screen.Radar.route -> radarColors
            Screen.Settings.route -> settingsColors
            else -> weatherColors // Default
        }
    }
}

@Composable
fun AgroWeatherNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(modifier = Modifier.fillMaxSize()) {
        AgroWeatherNavGraph(
            navController = navController,
            modifier = Modifier.padding(bottom = 80.dp),
        )

        NavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            containerColor = MaterialTheme.colorScheme.surface,
            windowInsets = WindowInsets(0.dp),
        ) {
            bottomNavItems.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = null) },
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                    onClick = {
                        try {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        } catch (e: IllegalArgumentException) {
                            // Handle navigation error gracefully
                            navController.navigate(screen.route) {
                                launchSingleTop = true
                            }
                        } catch (e: IllegalStateException) {
                            // Handle NavController not initialized error
                            navController.navigate(screen.route)
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onSurface,
                        selectedTextColor = MaterialTheme.colorScheme.onSurface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color(0xFFF0F0F0), // Light gray background for selected item
                    ),
                )
            }
        }
    }
}

// CompositionLocal for providing dynamic colors
val LocalDynamicColors = compositionLocalOf<Brush> {
    DynamicColors.weatherColors
}

@Preview
@Composable
fun AgroWeatherAppPreview() {
    FarmWeatherNavigation()
}
