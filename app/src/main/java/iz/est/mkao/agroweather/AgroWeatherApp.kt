package iz.est.mkao.agroweather

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import iz.est.mkao.agroweather.data.preferences.UserPreferences
import iz.est.mkao.agroweather.data.scheduler.IrrigationNotificationScheduler
import iz.est.mkao.agroweather.util.AppLifecycleObserver
import javax.inject.Inject

@HiltAndroidApp
class AgroWeatherApp : Application() {

    @Inject
    lateinit var irrigationNotificationScheduler: IrrigationNotificationScheduler

    @Inject
    lateinit var userPreferences: UserPreferences

    private lateinit var lifecycleObserver: AppLifecycleObserver

    override fun onCreate() {
        super.onCreate()


        lifecycleObserver = AppLifecycleObserver(this)
        lifecycleObserver.register()

        // Validate API keys on startup
        validateApiKeys()


        initializeIrrigationNotifications()


    }

    private fun validateApiKeys() {
        val missingKeys = mutableListOf<String>()

        if (WEATHER_API_KEY.isBlank()) missingKeys.add("WEATHER_API_KEY")
        if (GEMINI_API_KEY.isBlank()) missingKeys.add("GEMINI_API_KEY")
        if (NEWS_API_KEY.isBlank()) missingKeys.add("NEWS_API_KEY")

        if (missingKeys.isNotEmpty()) {
            val message = "Missing API keys: ${missingKeys.joinToString(", ")}. Please check local.properties file."
            Log.e(TAG, message)

            if (!DEBUG_MODE) {
                // In production, we should handle missing API keys gracefully
                throw IllegalStateException(message)
            }
        }
    }

    /**
     * Initialize irrigation notification scheduling based on user preferences
     */
    private fun initializeIrrigationNotifications() {
        try {
            if (userPreferences.irrigationNotificationsEnabled) {

                irrigationNotificationScheduler.enableNotifications()

                // Schedule urgent checks during growing season (March-October)
                val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH)
                if (currentMonth in 2..9) { // March (2) to October (9)
                    irrigationNotificationScheduler.scheduleUrgentChecks()

                }
            } else {

                irrigationNotificationScheduler.disableNotifications()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing irrigation notifications", e)
        }
    }

    companion object {
        private const val TAG = "AgroWeatherApp"

        // API keys loaded securely from BuildConfig
        val WEATHER_API_KEY: String get() = BuildConfig.WEATHER_API_KEY
        val GEMINI_API_KEY: String get() = BuildConfig.GEMINI_API_KEY
        val NEWS_API_KEY: String get() = BuildConfig.NEWS_API_KEY

        // Debug and logging configuration
        val DEBUG_MODE: Boolean get() = BuildConfig.DEBUG_MODE
        val LOG_LEVEL: String get() = BuildConfig.LOG_LEVEL
    }
}
