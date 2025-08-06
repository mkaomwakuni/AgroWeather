package iz.est.mkao.agroweather.presentation.settings

import iz.est.mkao.agroweather.data.model.City

data class WeatherSettingsUiState(
    val useCurrentLocation: Boolean = true,
    val isCelsius: Boolean = true,
    val isDarkTheme: Boolean = false,
    val language: String = "en",
    val defaultLocation: String = "Nairobi",
    val defaultLatitude: Double = 51.5074,
    val defaultLongitude: Double = -0.1278,
    val availableLanguages: List<Language> = listOf(
        Language("en", "English"),
        Language("es", "Español"),
        Language("fr", "Français"),
        Language("de", "Deutsch"),
        Language("it", "Italiano"),
        Language("pt", "Português"),
        Language("zh", "中文"),
        Language("ja", "日本語"),
        Language("ko", "한국어"),
        Language("ar", "العربية"),
    ),
    val availableCities: List<City> = emptyList(),
    val irrigationNotificationsEnabled: Boolean = true,
    val urgentIrrigationAlertsEnabled: Boolean = true,
    val dailyIrrigationSummaryEnabled: Boolean = true,
    val showLanguageDialog: Boolean = false,
    val showLocationDialog: Boolean = false,
    val showFAQsDialog: Boolean = false,
    val showAboutDialog: Boolean = false,
    val isSaved: Boolean = false,
    val saveMessage: String? = null,
    val error: String? = null,
)

data class Language(
    val code: String,
    val name: String,
)
