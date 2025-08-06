package iz.est.mkao.agroweather.data.repository

import iz.est.mkao.agroweather.data.model.NewsArticle
import iz.est.mkao.agroweather.util.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for news operations
 */
interface NewsRepository {

    /**
     * Get latest agricultural news articles
     */
    suspend fun getLatestAgriculturalNews(): Result<List<NewsArticle>>

    /**
     * Search for specific agricultural news topics
     */
    suspend fun searchAgriculturalNews(query: String): Result<List<NewsArticle>>

    /**
     * Get cached news articles as a flow
     */
    fun getCachedNews(): Flow<List<NewsArticle>>
}
