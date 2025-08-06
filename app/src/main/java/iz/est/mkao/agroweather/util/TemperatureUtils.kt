package iz.est.mkao.agroweather.util

import iz.est.mkao.agroweather.data.preferences.UserPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TemperatureUtils @Inject constructor(
    private val userPreferences: UserPreferences,
) {

    /**
     * Converts temperature to the user's preferred unit
     * @param celsius Temperature in Celsius
     * @return Temperature in user's preferred unit
     */
    fun convertTemperature(celsius: Double): Double {
        return if (userPreferences.isCelsius) {
            celsius
        } else {
            celsiusToFahrenheit(celsius)
        }
    }

    /**
     * Gets the temperature with the appropriate unit symbol
     * @param celsius Temperature in Celsius
     * @return Formatted temperature string with unit
     */
    fun formatTemperature(celsius: Double): String {
        val converted = convertTemperature(celsius)
        val unit = if (userPreferences.isCelsius) "°C" else "°F"
        return "${converted.toInt()}$unit"
    }

    /**
     * Gets the temperature with the appropriate unit symbol (with decimal)
     * @param celsius Temperature in Celsius
     * @return Formatted temperature string with unit and decimal
     */
    fun formatTemperatureWithDecimal(celsius: Double): String {
        val converted = convertTemperature(celsius)
        val unit = if (userPreferences.isCelsius) "°C" else "°F"
        return "${String.format("%.1f", converted)}$unit"
    }

    /**
     * Gets just the temperature unit symbol
     * @return Temperature unit symbol
     */
    fun getTemperatureUnit(): String {
        return if (userPreferences.isCelsius) "°C" else "°F"
    }

    /**
     * Gets just the temperature unit symbol without degree sign
     * @return Temperature unit letter
     */
    fun getTemperatureUnitLetter(): String {
        return if (userPreferences.isCelsius) "C" else "F"
    }

    /**
     * Converts Celsius to Fahrenheit
     */
    private fun celsiusToFahrenheit(celsius: Double): Double {
        return celsius * 9.0 / 5.0 + 32.0
    }

    /**
     * Converts Fahrenheit to Celsius
     */
    fun fahrenheitToCelsius(fahrenheit: Double): Double {
        return (fahrenheit - 32.0) * 5.0 / 9.0
    }
}
