package iz.est.mkao.agroweather.data.local.dao

import androidx.room.*
import iz.est.mkao.agroweather.data.local.entities.WeatherCacheEntity
import iz.est.mkao.agroweather.util.Constants
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface WeatherCacheDao {
    
    @Query("SELECT * FROM ${Constants.Database.WEATHER_CACHE_TABLE} WHERE locationKey = :locationKey")
    suspend fun getWeatherCache(locationKey: String): WeatherCacheEntity?
    
    @Query("SELECT * FROM ${Constants.Database.WEATHER_CACHE_TABLE} WHERE locationKey = :locationKey")
    fun getWeatherCacheFlow(locationKey: String): Flow<WeatherCacheEntity?>
    
    @Query("SELECT * FROM ${Constants.Database.WEATHER_CACHE_TABLE}")
    suspend fun getAllWeatherCache(): List<WeatherCacheEntity>
    
    @Query("SELECT * FROM ${Constants.Database.WEATHER_CACHE_TABLE}")
    fun getAllWeatherCacheFlow(): Flow<List<WeatherCacheEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherCache(weatherCache: WeatherCacheEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeatherCacheList(weatherCacheList: List<WeatherCacheEntity>)
    
    @Update
    suspend fun updateWeatherCache(weatherCache: WeatherCacheEntity)
    
    @Delete
    suspend fun deleteWeatherCache(weatherCache: WeatherCacheEntity)
    
    @Query("DELETE FROM ${Constants.Database.WEATHER_CACHE_TABLE} WHERE locationKey = :locationKey")
    suspend fun deleteWeatherCacheByLocation(locationKey: String)
    
    @Query("DELETE FROM ${Constants.Database.WEATHER_CACHE_TABLE}")
    suspend fun deleteAllWeatherCache()
    
    @Query("DELETE FROM ${Constants.Database.WEATHER_CACHE_TABLE} WHERE expiresAt < :currentDateTime")
    suspend fun deleteExpiredWeatherCache(currentDateTime: LocalDateTime = LocalDateTime.now())
    
    @Query("SELECT COUNT(*) FROM ${Constants.Database.WEATHER_CACHE_TABLE}")
    suspend fun getWeatherCacheCount(): Int
    
    @Query("SELECT COUNT(*) FROM ${Constants.Database.WEATHER_CACHE_TABLE} WHERE expiresAt > :currentDateTime")
    suspend fun getValidWeatherCacheCount(currentDateTime: LocalDateTime = LocalDateTime.now()): Int
    
    @Query("SELECT * FROM ${Constants.Database.WEATHER_CACHE_TABLE} WHERE expiresAt > :currentDateTime ORDER BY cachedAt DESC")
    suspend fun getValidWeatherCache(currentDateTime: LocalDateTime = LocalDateTime.now()): List<WeatherCacheEntity>
    
    @Query("SELECT * FROM ${Constants.Database.WEATHER_CACHE_TABLE} WHERE expiresAt > :currentDateTime ORDER BY cachedAt DESC")
    fun getValidWeatherCacheFlow(currentDateTime: LocalDateTime = LocalDateTime.now()): Flow<List<WeatherCacheEntity>>
}
