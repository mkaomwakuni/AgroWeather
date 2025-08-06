package iz.est.mkao.agroweather.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import iz.est.mkao.agroweather.data.local.converters.WeatherConverters
import iz.est.mkao.agroweather.data.local.converters.NewsConverters
import iz.est.mkao.agroweather.data.local.dao.*
import iz.est.mkao.agroweather.data.local.entities.*
import iz.est.mkao.agroweather.data.model.ChatMessage
import iz.est.mkao.agroweather.data.model.ChatSession
import iz.est.mkao.agroweather.util.Constants

@Database(
    entities = [
        ChatMessage::class,
        ChatSession::class,
        WeatherCacheEntity::class,
        NewsCacheEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
@TypeConverters(
    RoomConverters::class,
    WeatherConverters::class,
    NewsConverters::class
)
abstract class AgroWeatherDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
    abstract fun weatherCacheDao(): WeatherCacheDao
    abstract fun newsCacheDao(): NewsCacheDao

    companion object {
        @Volatile
        private var INSTANCE: AgroWeatherDatabase? = null

        fun getInstance(context: Context): AgroWeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AgroWeatherDatabase::class.java,
                    Constants.Database.DATABASE_NAME,
                )
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration() // For development - remove in production
                    .build()

                INSTANCE = instance
                instance
            }
        }
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create weather cache table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `${Constants.Database.WEATHER_CACHE_TABLE}` (
                        `locationKey` TEXT NOT NULL,
                        `weatherData` TEXT NOT NULL,
                        `cachedAt` TEXT NOT NULL,
                        `expiresAt` TEXT NOT NULL,
                        PRIMARY KEY(`locationKey`)
                    )
                """.trimIndent())
                
                // Create news cache table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS `${Constants.Database.NEWS_CACHE_TABLE}` (
                        `category` TEXT NOT NULL,
                        `articles` TEXT NOT NULL,
                        `cachedAt` TEXT NOT NULL,
                        `expiresAt` TEXT NOT NULL,
                        PRIMARY KEY(`category`)
                    )
                """.trimIndent())
            }
        }
    }
}
