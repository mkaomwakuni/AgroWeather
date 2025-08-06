package iz.est.mkao.agroweather.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iz.est.mkao.agroweather.data.preferences.UserPreferences
import iz.est.mkao.agroweather.data.repository.CityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherSettingsViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherSettingsUiState())
    val uiState: StateFlow<WeatherSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        loadCities()
    }

    private fun loadSettings() {
        _uiState.value = _uiState.value.copy(
            useCurrentLocation = userPreferences.useCurrentLocation,
            isCelsius = userPreferences.isCelsius,
            isDarkTheme = userPreferences.isDarkTheme,
            language = userPreferences.language,
            defaultLocation = userPreferences.defaultLocation,
            defaultLatitude = userPreferences.defaultLatitude,
            defaultLongitude = userPreferences.defaultLongitude,
            irrigationNotificationsEnabled = userPreferences.irrigationNotificationsEnabled,
            urgentIrrigationAlertsEnabled = userPreferences.urgentIrrigationAlertsEnabled,
            dailyIrrigationSummaryEnabled = userPreferences.dailyIrrigationSummaryEnabled,
        )
    }

    private fun loadCities() {
        viewModelScope.launch {
            val cities = cityRepository.getAllCities()
            _uiState.value = _uiState.value.copy(availableCities = cities)
        }
    }

    fun updateUseCurrentLocation(useCurrentLocation: Boolean) {
        _uiState.value = _uiState.value.copy(useCurrentLocation = useCurrentLocation)
    }

    fun updateTemperatureUnit(isCelsius: Boolean) {
        _uiState.value = _uiState.value.copy(isCelsius = isCelsius)
    }

    fun updateDarkTheme(isDarkTheme: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkTheme = isDarkTheme)
    }

    fun updateLanguage(language: String) {
        _uiState.value = _uiState.value.copy(language = language)
    }

    fun updateLocation(location: String, latitude: Double, longitude: Double) {
        _uiState.value = _uiState.value.copy(
            defaultLocation = location,
            defaultLatitude = latitude,
            defaultLongitude = longitude,
        )
    }

    fun updateIrrigationNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(irrigationNotificationsEnabled = enabled)
    }

    fun updateUrgentIrrigationAlerts(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(urgentIrrigationAlertsEnabled = enabled)
    }

    fun updateDailyIrrigationSummary(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(dailyIrrigationSummaryEnabled = enabled)
    }

    fun showLanguageDialog() {
        _uiState.value = _uiState.value.copy(showLanguageDialog = true)
    }

    fun hideLanguageDialog() {
        _uiState.value = _uiState.value.copy(showLanguageDialog = false)
    }

    fun showLocationDialog() {
        _uiState.value = _uiState.value.copy(showLocationDialog = true)
    }

    fun hideLocationDialog() {
        _uiState.value = _uiState.value.copy(showLocationDialog = false)
    }

    fun showFAQsDialog() {
        _uiState.value = _uiState.value.copy(showFAQsDialog = true)
    }

    fun hideFAQsDialog() {
        _uiState.value = _uiState.value.copy(showFAQsDialog = false)
    }

    fun showAboutDialog() {
        _uiState.value = _uiState.value.copy(showAboutDialog = true)
    }

    fun hideAboutDialog() {
        _uiState.value = _uiState.value.copy(showAboutDialog = false)
    }

    fun saveSettings() {
        viewModelScope.launch {
            val currentState = _uiState.value
            userPreferences.useCurrentLocation = currentState.useCurrentLocation
            userPreferences.isCelsius = currentState.isCelsius
            userPreferences.isDarkTheme = currentState.isDarkTheme
            userPreferences.language = currentState.language
            userPreferences.irrigationNotificationsEnabled = currentState.irrigationNotificationsEnabled
            userPreferences.urgentIrrigationAlertsEnabled = currentState.urgentIrrigationAlertsEnabled
            userPreferences.dailyIrrigationSummaryEnabled = currentState.dailyIrrigationSummaryEnabled
            userPreferences.updateLocationSettings(
                useCurrentLocation = currentState.useCurrentLocation,
                location = currentState.defaultLocation,
                latitude = currentState.defaultLatitude,
                longitude = currentState.defaultLongitude,
            )

            _uiState.value = _uiState.value.copy(
                isSaved = true,
                saveMessage = "Settings saved successfully!",
            )

            // Clear the save message after a delay
            kotlinx.coroutines.delay(2000)
            _uiState.value = _uiState.value.copy(
                isSaved = false,
                saveMessage = null,
            )
        }
    }

    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(
            isSaved = false,
            saveMessage = null,
        )
    }
}
