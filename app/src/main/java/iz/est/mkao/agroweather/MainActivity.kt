package iz.est.mkao.agroweather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import iz.est.mkao.agroweather.data.preferences.UserPreferences
import iz.est.mkao.agroweather.presentation.navigation.FarmWeatherNavigation
import iz.est.mkao.agroweather.ui.theme.FarmWeatherTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AgroWeatherApp()
        }
    }

    @Composable
    private fun AgroWeatherApp() {
        // Observe theme changes from UserPreferences StateFlow
        val isDarkTheme by userPreferences.isDarkThemeFlow.collectAsStateWithLifecycle()

        FarmWeatherTheme(darkTheme = isDarkTheme) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Transparent,
            ) {
                FarmWeatherNavigation()
            }
        }
    }
}
