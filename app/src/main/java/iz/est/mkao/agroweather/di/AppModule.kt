package iz.est.mkao.agroweather.di

import android.content.Context
import com.google.ai.client.generativeai.GenerativeModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import iz.est.mkao.agroweather.AgroWeatherApp
import iz.est.mkao.agroweather.data.api.NewsApiService
import iz.est.mkao.agroweather.data.api.WeatherApiService
import iz.est.mkao.agroweather.data.local.ChatDao
import iz.est.mkao.agroweather.data.local.AgroWeatherDatabase
import iz.est.mkao.agroweather.data.preferences.UserPreferences
import iz.est.mkao.agroweather.data.repository.ChatRepository
import iz.est.mkao.agroweather.data.repository.ChatRepositoryImpl
import iz.est.mkao.agroweather.data.repository.CityRepository
import iz.est.mkao.agroweather.data.repository.NewsRepository
import iz.est.mkao.agroweather.data.repository.NewsRepositoryImpl
import iz.est.mkao.agroweather.data.repository.WeatherRepositoryImpl
import iz.est.mkao.agroweather.domain.repository.WeatherRepository
import iz.est.mkao.agroweather.util.Constants
import iz.est.mkao.agroweather.util.Logger
import iz.est.mkao.agroweather.util.NetworkUtils
import iz.est.mkao.agroweather.util.TemperatureUtils
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAgroWeatherDatabase(
        @ApplicationContext context: Context,
    ): AgroWeatherDatabase {
        return AgroWeatherDatabase.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideChatDao(database: AgroWeatherDatabase): ChatDao {
        return database.chatDao()
    }

    @Provides
    @Singleton
    fun provideWeatherCacheDao(database: AgroWeatherDatabase): iz.est.mkao.farmweather.data.local.dao.WeatherCacheDao {
        return database.weatherCacheDao()
    }

    @Provides
    @Singleton
    fun provideNewsCacheDao(database: AgroWeatherDatabase): iz.est.mkao.farmweather.data.local.dao.NewsCacheDao {
        return database.newsCacheDao()
    }

    @Provides
    @Singleton
    fun provideGenerativeModel(): GenerativeModel {
        // Uses Gemini 1.5 Flash Latest model
        return GenerativeModel(
            modelName = "gemini-2.0-flash",
            apiKey = AgroWeatherApp.GEMINI_API_KEY,
        )
    }

    @Provides
    @Singleton
    fun provideChatRepository(
        chatDao: ChatDao,
        generativeModel: GenerativeModel,
    ): ChatRepository {
        return ChatRepositoryImpl(chatDao, generativeModel)
    }

    @Provides
    @Singleton
    fun provideCacheDir(@ApplicationContext context: Context): File {
        return File(context.cacheDir, "http_cache")
    }

    @Provides
    @Singleton
    fun provideCache(cacheDir: File): Cache {
        return Cache(cacheDir, Constants.Network.CACHE_SIZE)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        cache: Cache,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(Constants.Network.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Constants.Network.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Constants.Network.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .cache(cache)
            .addInterceptor { chain ->
                var request = chain.request()

                // Add caching headers based on network availability
                request = if (NetworkUtils.isNetworkAvailable(context)) {
                    request.newBuilder()
                        .header("Cache-Control", "public, max-age=${Constants.Network.CACHE_MAX_AGE}")
                        .build()
                } else {
                    request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=${Constants.Network.CACHE_MAX_STALE}")
                        .build()
                }
                chain.proceed(request)
            }

        // Add logging interceptor only in debug mode
        if (AgroWeatherApp.DEBUG_MODE) {
            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Logger.d("HTTP", message)
            }
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            builder.addInterceptor(loggingInterceptor)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService {
        return retrofit.create(WeatherApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideWeatherRepository(
        weatherApiService: WeatherApiService,
    ): WeatherRepository {
        return WeatherRepositoryImpl(weatherApiService)
    }

    @Provides
    @Singleton
    fun provideCityRepository(): CityRepository {
        return CityRepository()
    }

    // Domain use cases
    @Provides
    @Singleton
    fun provideCalculateIrrigationSuitabilityUseCase(): iz.est.mkao.farmweather.domain.usecase.irrigation.CalculateIrrigationSuitabilityUseCase {
        return iz.est.mkao.farmweather.domain.usecase.irrigation.CalculateIrrigationSuitabilityUseCase()
    }

    @Provides
    @Singleton
    fun provideGetWeatherGradientUseCase(): iz.est.mkao.farmweather.domain.usecase.weather.GetWeatherGradientUseCase {
        return iz.est.mkao.farmweather.domain.usecase.weather.GetWeatherGradientUseCase()
    }

    @Provides
    @Singleton
    fun provideGetWeatherDescriptionsUseCase(): iz.est.mkao.farmweather.domain.usecase.weather.GetWeatherDescriptionsUseCase {
        return iz.est.mkao.farmweather.domain.usecase.weather.GetWeatherDescriptionsUseCase()
    }


    @Provides 
    @Singleton
    fun provideNewsRepository(newsApiService: NewsApiService): NewsRepository {
        return NewsRepositoryImpl(newsApiService)
    }

    @Provides
    @Singleton
    @NewsRetrofit
    fun provideNewsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNewsApiService(@NewsRetrofit retrofit: Retrofit): NewsApiService {
        return retrofit.create(NewsApiService::class.java)
    }



    @Provides
    @Singleton
    fun provideTemperatureUtils(
        userPreferences: UserPreferences,
    ): TemperatureUtils {
        return TemperatureUtils(userPreferences)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(
        @ApplicationContext context: Context,
    ): UserPreferences {
        return UserPreferences(context)
    }

    @Provides
    @Singleton
    fun providePerformanceMonitor(
        @ApplicationContext context: Context,
    ): iz.est.mkao.farmweather.util.PerformanceMonitor {
        return iz.est.mkao.farmweather.util.PerformanceMonitor(context)
    }
}
