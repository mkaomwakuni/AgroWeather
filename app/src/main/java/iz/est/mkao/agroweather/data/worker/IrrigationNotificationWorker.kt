package iz.est.mkao.agroweather.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import iz.est.mkao.agroweather.data.preferences.UserPreferences
import iz.est.mkao.agroweather.data.repository.WeatherRepositoryImpl
import iz.est.mkao.agroweather.data.service.IrrigationNotificationService
import iz.est.mkao.agroweather.data.service.IrrigationService
import iz.est.mkao.agroweather.util.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * WorkManager worker that checks weather conditions and sends irrigation notifications
 */
@HiltWorker
class IrrigationNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val weatherRepository: WeatherRepositoryImpl,
    private val irrigationService: IrrigationService,
    private val notificationService: IrrigationNotificationService,
    private val userPreferences: UserPreferences,
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "IrrigationNotificationWorker"
        const val WORK_NAME = "irrigation_notification_work"
    }

    override suspend fun doWork(): ListenableWorker.Result = withContext(Dispatchers.IO) {
        try {
            Logger.d(TAG, "Starting irrigation notification check")

            // Get user's location settings
            val locationSettings = userPreferences.getLocationSettings()
            val location = if (locationSettings.useCurrentLocation) {
                // Default to London if current location is not available
                "${locationSettings.defaultLatitude},${locationSettings.defaultLongitude}"
            } else {
                "${locationSettings.defaultLatitude},${locationSettings.defaultLongitude}"
            }

            Logger.d(TAG, "Checking weather for location: $location")

            // Get weather data for the next 7 days
            val weatherResult = weatherRepository.getWeatherData(location)

            val weatherData = weatherResult.getOrElse { exception ->
                Logger.e(TAG, "Failed to get weather data: $exception")
                return@withContext ListenableWorker.Result.retry()
            }

            // Analyze irrigation recommendations
            val recommendations = irrigationService.analyzeBestDaysToIrrigate(weatherData)

            Logger.d(TAG, "Found ${recommendations.size} irrigation recommendations")

            // Check for urgent irrigation needs (very high score or very low soil moisture)
            val urgentRecommendations = recommendations.filter { recommendation ->
                recommendation.score >= 0.9f ||
                    (recommendation.soilMoisture != null && recommendation.soilMoisture < 0.15)
            }

            // Send urgent notifications if needed
            urgentRecommendations.forEach { recommendation ->
                Logger.d(TAG, "Sending urgent notification for ${recommendation.date}")
                notificationService.sendUrgentIrrigationAlert(recommendation)
            }

            // Send daily summary for high-quality recommendations
            val goodRecommendations = recommendations.filter { it.score >= 0.7f }
            if (goodRecommendations.isNotEmpty()) {
                Logger.d(TAG, "Sending daily summary with ${goodRecommendations.size} good recommendations")
                notificationService.sendDailyIrrigationSummary(goodRecommendations)
            }

            // Send individual notifications for today's excellent conditions
            val todayRecommendations = recommendations.filter { recommendation ->
                recommendation.date == java.time.LocalDate.now() && recommendation.score >= 0.8f
            }

            todayRecommendations.forEach { recommendation ->
                Logger.d(TAG, "Sending today's irrigation notification")
                notificationService.sendIrrigationRecommendationNotification(recommendation)
            }

            Logger.d(TAG, "Irrigation notification check completed successfully")
            ListenableWorker.Result.success()
        } catch (e: Exception) {
            Logger.e(TAG, "Error in irrigation notification worker", e)
            ListenableWorker.Result.retry()
        }
    }
}
