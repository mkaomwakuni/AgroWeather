package iz.est.mkao.agroweather.data.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // StateFlow for theme changes
    private val _isDarkThemeFlow = MutableStateFlow(prefs.getBoolean(KEY_DARK_THEME, false))
    val isDarkThemeFlow: StateFlow<Boolean> = _isDarkThemeFlow.asStateFlow()

    companion object {
        private const val PREFS_NAME = "farm_weather_prefs"
        private const val KEY_USE_CURRENT_LOCATION = "use_current_location"
        private const val KEY_DEFAULT_LOCATION = "default_location"
        private const val KEY_DEFAULT_LATITUDE = "default_latitude"
        private const val KEY_DEFAULT_LONGITUDE = "default_longitude"
        private const val KEY_TEMPERATURE_UNIT = "temperature_unit"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_LANGUAGE = "language"
        private const val KEY_IRRIGATION_NOTIFICATIONS = "irrigation_notifications"
        private const val KEY_URGENT_IRRIGATION_ALERTS = "urgent_irrigation_alerts"
        private const val KEY_DAILY_IRRIGATION_SUMMARY = "daily_irrigation_summary"

        const val DEFAULT_LOCATION = "Nairobi"
        const val DEFAULT_LATITUDE = 51.5074
        const val DEFAULT_LONGITUDE = -0.1278
        const val DEFAULT_LANGUAGE = "en"
    }

    // Location preferences
    var useCurrentLocation: Boolean
        get() = prefs.getBoolean(KEY_USE_CURRENT_LOCATION, true)
        set(value) = prefs.edit().putBoolean(KEY_USE_CURRENT_LOCATION, value).apply()

    var defaultLocation: String
        get() = prefs.getString(KEY_DEFAULT_LOCATION, DEFAULT_LOCATION) ?: DEFAULT_LOCATION
        set(value) = prefs.edit().putString(KEY_DEFAULT_LOCATION, value).apply()

    var defaultLatitude: Double
        get() = prefs.getFloat(KEY_DEFAULT_LATITUDE, DEFAULT_LATITUDE.toFloat()).toDouble()
        set(value) = prefs.edit().putFloat(KEY_DEFAULT_LATITUDE, value.toFloat()).apply()

    var defaultLongitude: Double
        get() = prefs.getFloat(KEY_DEFAULT_LONGITUDE, DEFAULT_LONGITUDE.toFloat()).toDouble()
        set(value) = prefs.edit().putFloat(KEY_DEFAULT_LONGITUDE, value.toFloat()).apply()

    // Temperature unit preferences (true = Celsius, false = Fahrenheit)
    var isCelsius: Boolean
        get() = prefs.getBoolean(KEY_TEMPERATURE_UNIT, true)
        set(value) = prefs.edit().putBoolean(KEY_TEMPERATURE_UNIT, value).apply()

    // Theme preferences
    var isDarkTheme: Boolean
        get() = prefs.getBoolean(KEY_DARK_THEME, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()
            _isDarkThemeFlow.value = value
        }

    // Language preferences
    var language: String
        get() = prefs.getString(KEY_LANGUAGE, DEFAULT_LANGUAGE) ?: DEFAULT_LANGUAGE
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    // Irrigation notification preferences
    var irrigationNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_IRRIGATION_NOTIFICATIONS, true)
        set(value) = prefs.edit().putBoolean(KEY_IRRIGATION_NOTIFICATIONS, value).apply()

    var urgentIrrigationAlertsEnabled: Boolean
        get() = prefs.getBoolean(KEY_URGENT_IRRIGATION_ALERTS, true)
        set(value) = prefs.edit().putBoolean(KEY_URGENT_IRRIGATION_ALERTS, value).apply()

    var dailyIrrigationSummaryEnabled: Boolean
        get() = prefs.getBoolean(KEY_DAILY_IRRIGATION_SUMMARY, true)
        set(value) = prefs.edit().putBoolean(KEY_DAILY_IRRIGATION_SUMMARY, value).apply()

    // Method to update location setting
    fun updateLocationSettings(
        useCurrentLocation: Boolean,
        location: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
    ) {
        prefs.edit().apply {
            putBoolean(KEY_USE_CURRENT_LOCATION, useCurrentLocation)
            location?.let { putString(KEY_DEFAULT_LOCATION, it) }
            latitude?.let { putFloat(KEY_DEFAULT_LATITUDE, it.toFloat()) }
            longitude?.let { putFloat(KEY_DEFAULT_LONGITUDE, it.toFloat()) }
            apply()
        }
    }

    // Method to get current location settings
    data class LocationSettings(
        val useCurrentLocation: Boolean,
        val defaultLocation: String,
        val defaultLatitude: Double,
        val defaultLongitude: Double,
    )

    fun getLocationSettings(): LocationSettings {
        return LocationSettings(
            useCurrentLocation = useCurrentLocation,
            defaultLocation = defaultLocation,
            defaultLatitude = defaultLatitude,
            defaultLongitude = defaultLongitude,
        )
    }
}
