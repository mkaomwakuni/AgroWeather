package iz.est.mkao.agroweather.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import iz.est.mkao.agroweather.data.local.RoomConverters
import iz.est.mkao.agroweather.data.local.converters.NewsConverters
import iz.est.mkao.agroweather.data.model.NewsArticle
import iz.est.mkao.agroweather.util.Constants
import java.time.LocalDateTime

@Entity(tableName = Constants.Database.NEWS_CACHE_TABLE)
@TypeConverters(NewsConverters::class, RoomConverters::class)
data class NewsCacheEntity(
    @PrimaryKey
    val category: String,
    val articles: List<NewsArticle>,
    val cachedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime = LocalDateTime.now().plusHours(1)
) {
    /**
     * Check if the cached data is still valid
     */
    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt)
    }
    
    /**
     * Check if the cached data is still fresh (within 30 minutes)
     */
    fun isFresh(): Boolean {
        return LocalDateTime.now().isBefore(cachedAt.plusMinutes(30))
    }
}
