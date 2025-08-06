package iz.est.mkao.agroweather.data.repository

import android.util.Log
import iz.est.mkao.agroweather.util.SecureLogger
import iz.est.mkao.agroweather.BuildConfig
import iz.est.mkao.agroweather.data.api.NewsApiService
import iz.est.mkao.agroweather.data.model.NewsArticle
import iz.est.mkao.agroweather.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production-ready NewsRepository implementation with proper error handling,
 * caching, and network optimizations for agricultural news data.
 */
@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
) : NewsRepository {

    companion object {
        private const val TAG = "NewsRepository"
    }

    private var cachedNews: List<NewsArticle> = emptyList()

    override suspend fun getLatestAgriculturalNews(): Result<List<NewsArticle>> {
        return try {
            SecureLogger.d(TAG, "Fetching latest agricultural news")

            val response = newsApiService.getAgriculturalNews(
                apiKey = BuildConfig.NEWS_API_KEY,
                query = "agriculture OR farming OR crop OR livestock OR agricultural OR agronomy OR irrigation OR farm",
                sortBy = "publishedAt",
                pageSize = 20,
            )

            if (response.isSuccessful) {
                val newsResponse = response.body()
                if (newsResponse != null && newsResponse.status == "ok") {
                    cachedNews = newsResponse.results
                    Log.d(TAG, "Successfully fetched ${newsResponse.results.size} agricultural news articles")
                    Result.Success(newsResponse.results)
                } else {
                    val errorMessage = "Unknown error occurred"
                    Log.e(TAG, "API returned error: $errorMessage")
                    Result.Error(Exception("Failed to fetch news: $errorMessage"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Network error: ${response.code()} - $errorBody")
                Result.Error(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception fetching agricultural news", e)
            Result.Error(e)
        }
    }

    override suspend fun searchAgriculturalNews(query: String): Result<List<NewsArticle>> {
        return try {
            Log.d(TAG, "Searching agricultural news with query: $query")

            val response = newsApiService.searchAgriculturalNews(
                apiKey = BuildConfig.NEWS_API_KEY,
                query = "$query AND (agriculture OR farming OR crop OR livestock)",
                sortBy = "relevancy",
                pageSize = 15,
            )

            if (response.isSuccessful) {
                val newsResponse = response.body()
                if (newsResponse != null && newsResponse.status == "ok") {
                    Log.d(TAG, "Successfully found ${newsResponse.results.size} articles for query: $query")
                    Result.Success(newsResponse.results)
                } else {
                    val errorMessage = "Unknown error occurred"
                    Log.e(TAG, "API returned error for search: $errorMessage")
                    Result.Error(Exception("Failed to search news: $errorMessage"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Network error in search: ${response.code()} - $errorBody")
                Result.Error(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception searching agricultural news", e)
            Result.Error(e)
        }
    }

    override fun getCachedNews(): Flow<List<NewsArticle>> {
        return flowOf(cachedNews)
    }
}
