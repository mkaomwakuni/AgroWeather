package iz.est.mkao.agroweather.data.local.dao

import androidx.room.*
import iz.est.mkao.agroweather.data.local.entities.NewsCacheEntity
import iz.est.mkao.agroweather.util.Constants
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface NewsCacheDao {
    
    @Query("SELECT * FROM ${Constants.Database.NEWS_CACHE_TABLE} WHERE category = :category")
    suspend fun getNewsCache(category: String): NewsCacheEntity?
    
    @Query("SELECT * FROM ${Constants.Database.NEWS_CACHE_TABLE} WHERE category = :category")
    fun getNewsCacheFlow(category: String): Flow<NewsCacheEntity?>
    
    @Query("SELECT * FROM ${Constants.Database.NEWS_CACHE_TABLE}")
    suspend fun getAllNewsCache(): List<NewsCacheEntity>
    
    @Query("SELECT * FROM ${Constants.Database.NEWS_CACHE_TABLE}")
    fun getAllNewsCacheFlow(): Flow<List<NewsCacheEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsCache(newsCache: NewsCacheEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsCacheList(newsCacheList: List<NewsCacheEntity>)
    
    @Update
    suspend fun updateNewsCache(newsCache: NewsCacheEntity)
    
    @Delete
    suspend fun deleteNewsCache(newsCache: NewsCacheEntity)
    
    @Query("DELETE FROM ${Constants.Database.NEWS_CACHE_TABLE} WHERE category = :category")
    suspend fun deleteNewsCacheByCategory(category: String)
    
    @Query("DELETE FROM ${Constants.Database.NEWS_CACHE_TABLE}")
    suspend fun deleteAllNewsCache()
    
    @Query("DELETE FROM ${Constants.Database.NEWS_CACHE_TABLE} WHERE expiresAt < :currentDateTime")
    suspend fun deleteExpiredNewsCache(currentDateTime: LocalDateTime = LocalDateTime.now())
    
    @Query("SELECT COUNT(*) FROM ${Constants.Database.NEWS_CACHE_TABLE}")
    suspend fun getNewsCacheCount(): Int
    
    @Query("SELECT COUNT(*) FROM ${Constants.Database.NEWS_CACHE_TABLE} WHERE expiresAt > :currentDateTime")
    suspend fun getValidNewsCacheCount(currentDateTime: LocalDateTime = LocalDateTime.now()): Int
    
    @Query("SELECT * FROM ${Constants.Database.NEWS_CACHE_TABLE} WHERE expiresAt > :currentDateTime ORDER BY cachedAt DESC")
    suspend fun getValidNewsCache(currentDateTime: LocalDateTime = LocalDateTime.now()): List<NewsCacheEntity>
    
    @Query("SELECT * FROM ${Constants.Database.NEWS_CACHE_TABLE} WHERE expiresAt > :currentDateTime ORDER BY cachedAt DESC")
    fun getValidNewsCacheFlow(currentDateTime: LocalDateTime = LocalDateTime.now()): Flow<List<NewsCacheEntity>>
}
