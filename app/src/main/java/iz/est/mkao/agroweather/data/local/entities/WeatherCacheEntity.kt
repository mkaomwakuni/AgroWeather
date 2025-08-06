package iz.est.mkao.agroweather.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import iz.est.mkao.agroweather.data.local.RoomConverters
import iz.est.mkao.agroweather.data.local.converters.WeatherConverters
import iz.est.mkao.agroweather.data.model.WeatherResponse
import iz.est.mkao.agroweather.util.Constants
import java.time.LocalDateTime

@Entity(tableName = Constants.Database.WEATHER_CACHE_TABLE)
@TypeConverters(WeatherConverters::class, RoomConverters::class)
data class WeatherCacheEntity(
    @PrimaryKey
    val locationKey: String,
    val weatherData: WeatherResponse,
    val cachedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusMinutes(30)
) {
    /**
     * Check if the cached data is still valid
     */
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }
    
    /**
     * Check if the cached data is still fresh (within 15 minutes)
     */
    fun isFresh(): Boolean {
        return LocalDateTime.now().isBefore(cachedAt.plusMinutes(15))
    }
    
    companion object {
        /**
         * Create a location key from coordinates
         */
        fun createLocationKey(latitude: Double, longitude: Double): String {
            return "${latitude.format(4)},${longitude.format(4)}"
        }
        
        /**
         * Create a location key from city name
         */
        fun createLocationKey(cityName: String): String {
            return cityName.lowercase().trim()
        }
        
        private fun Double.format(digits: Int) = "%.${digits}f".format(this)
    }
}
