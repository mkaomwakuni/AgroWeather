package iz.est.mkao.agroweather.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import iz.est.mkao.agroweather.presentation.chat.ChatScreen
import iz.est.mkao.agroweather.presentation.prompt.AgricultureActivitiesScreen
import iz.est.mkao.agroweather.presentation.settings.WeatherSettingsScreen
import iz.est.mkao.agroweather.presentation.suggestion.SuggestionScreen
import iz.est.mkao.agroweather.presentation.weather.WeatherApp
import java.net.URLDecoder

@Composable
fun AgroWeatherNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Weather.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        // Weather Screen (Home)
        weatherGraph(navController)

        // Prompt/AI Helper Screens
        promptGraph(navController)

        // Chat Screens
        chatGraph(navController)

        // Other Screens
        otherScreensGraph(navController)
    }
}

private fun NavGraphBuilder.weatherGraph(navController: NavHostController) {
    composable(Screen.Weather.route) {
        WeatherApp(navController = navController)
    }

    // Weather day details route with arguments
    composable(
        route = "weather_day_details/{dayData}",
        arguments = listOf(
            navArgument("dayData") {
                type = NavType.StringType
            },
        ),
    ) { backStackEntry ->
        val dayDataJson = backStackEntry.arguments?.getString("dayData") ?: ""

        val dayWeather = try {
            com.google.gson.Gson().fromJson(
                java.net.URLDecoder.decode(dayDataJson, "UTF-8"),
                iz.est.mkao.farmweather.data.model.DayWeather::class.java,
            )
        } catch (e: Exception) {
            null
        }

        if (dayWeather != null) {
            iz.est.mkao.farmweather.presentation.weather.WeatherDayDetailsScreen(
                navController = navController,
                dayWeather = dayWeather,
            )
        } else {
            // If parsing fails, navigate back
            LaunchedEffect(Unit) {
                navController.popBackStack()
            }
        }
    }
}

private fun NavGraphBuilder.promptGraph(navController: NavHostController) {
    // Base route for prompt screen
    composable(Screen.Prompt.route) {
        AgricultureActivitiesScreen(
            navController = navController,
            cityName = "",
            latitude = 38.9697,
            longitude = -77.385,
        )
    }

    // Route with parameters for prompt screen
    composable(
        route = "prompt/{cityName}/{latitude}/{longitude}",
        arguments = listOf(
            navArgument("cityName") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("latitude") {
                type = NavType.FloatType
                defaultValue = 38.9697f
            },
            navArgument("longitude") {
                type = NavType.FloatType
                defaultValue = -77.385f
            },
        ),
    ) { backStackEntry ->
        val cityName = backStackEntry.arguments?.getString("cityName")?.let {
            if (it.trim() == "" || it == " ") "" else it
        } ?: ""
        val latitude = backStackEntry.arguments?.getFloat("latitude")?.toDouble() ?: 38.9697
        val longitude = backStackEntry.arguments?.getFloat("longitude")?.toDouble() ?: -77.385

        AgricultureActivitiesScreen(
            navController = navController,
            cityName = cityName,
            latitude = latitude,
            longitude = longitude,
        )
    }
}

private fun NavGraphBuilder.chatGraph(navController: NavHostController) {
    // Base route for chat goes to actual chat screen
    composable(Screen.Chat.route) {
        ChatScreen(
            navController = navController,
            suggestion = "",
            cityName = "",
            latitude = 38.9697,
            longitude = -77.385,
        )
    }

    // Simplified chat route
    composable(
        route = "chat?suggestion={suggestion}&cityName={cityName}&latitude={latitude}&longitude={longitude}",
        arguments = listOf(
            navArgument("suggestion") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("cityName") {
                type = NavType.StringType
                defaultValue = ""
            },
            navArgument("latitude") {
                type = NavType.StringType
                defaultValue = "38.9697"
            },
            navArgument("longitude") {
                type = NavType.StringType
                defaultValue = "-77.385"
            },
        ),
    ) { backStackEntry ->
        val suggestion = backStackEntry.arguments?.getString("suggestion")?.let {
            try {
                URLDecoder.decode(it, "UTF-8")
            } catch (e: Exception) {
                it // Return original string if decoding fails
            }
        } ?: ""
        val cityName = backStackEntry.arguments?.getString("cityName")?.let {
            try {
                URLDecoder.decode(it, "UTF-8")
            } catch (e: Exception) {
                it // Return original string if decoding fails
            }
        } ?: ""
        val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull() ?: 38.9697
        val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull() ?: -77.385

        ChatScreen(
            navController = navController,
            suggestion = suggestion,
            cityName = cityName,
            latitude = latitude,
            longitude = longitude,
        )
    }

    // News details route
    composable(
        route = "news_details/{articleId}",
        arguments = listOf(
            navArgument("articleId") {
                type = NavType.StringType
            },
        ),
        deepLinks = listOf(navDeepLink { uriPattern = "farmweather://news/{articleId}" }),
    ) { backStackEntry ->
        val articleId = backStackEntry.arguments?.getString("articleId") ?: ""
        iz.est.mkao.farmweather.presentation.news.NewsDetailsScreen(
            navController = navController,
            articleId = articleId,
        )
    }
}

private fun NavGraphBuilder.otherScreensGraph(navController: NavHostController) {
    composable(Screen.AISuggestions.route) {
        SuggestionScreen(
            navController = navController,
            viewModel = hiltViewModel(),
        )
    }

    composable(Screen.Radar.route) {
        iz.est.mkao.farmweather.presentation.radar.WeatherRadarScreen(
            navController = navController,
        )
    }

    composable(Screen.Settings.route) {
        WeatherSettingsScreen(navController = navController)
    }
}
