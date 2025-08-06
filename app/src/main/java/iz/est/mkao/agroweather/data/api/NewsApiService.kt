package iz.est.mkao.agroweather.data.api

import iz.est.mkao.agroweather.data.model.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * News API service interface for NewsAPI.org
 * Documentation: https://newsapi.org/docs
 */
interface NewsApiService {

    /**
     * Get latest agricultural news articles using everything endpoint
     * @param apiKey The NewsAPI.org API key
     * @param q Search query for agricultural content
     * @param sortBy Sort articles by (publishedAt, relevancy, popularity)
     * @param language Language code (default: en)
     * @param pageSize Number of articles per page (max 100)
     * @param page Page number for pagination
     */
    @GET("everything")
    suspend fun getAgriculturalNews(
        @Query("apiKey") apiKey: String,
        @Query("q") query: String = "agriculture OR farming OR crop OR livestock OR agricultural OR agronomy OR irrigation OR farm",
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("language") language: String = "en",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1,
    ): Response<NewsResponse>

    /**
     * Get news articles by specific agricultural topics
     * @param apiKey The NewsAPI.org API key
     * @param query Specific search terms
     * @param sortBy Sort articles by
     * @param language Language code
     * @param pageSize Number of articles per page
     * @param page Page number
     */
    @GET("everything")
    suspend fun searchAgriculturalNews(
        @Query("apiKey") apiKey: String,
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "relevancy",
        @Query("language") language: String = "en",
        @Query("pageSize") pageSize: Int = 20,
        @Query("page") page: Int = 1,
    ): Response<NewsResponse>
}
