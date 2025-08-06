package iz.est.mkao.agroweather.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import iz.est.mkao.agroweather.MainActivity
import iz.est.mkao.agroweather.R
import iz.est.mkao.agroweather.data.model.IrrigationRecommendation
import iz.est.mkao.agroweather.data.preferences.UserPreferences
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for managing irrigation weather notifications
 */
@Singleton
class IrrigationNotificationService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferences: UserPreferences,
) {

    companion object {
        private const val CHANNEL_ID = "irrigation_alerts"
        private const val CHANNEL_NAME = "Irrigation Weather Alerts"
        private const val CHANNEL_DESCRIPTION = "Notifications for optimal irrigation days based on weather conditions"
        private const val NOTIFICATION_ID_BASE = 2000
    }

    init {
        createNotificationChannel()
    }

    /**
     * Create notification channel for irrigation alerts
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Send notification for optimal irrigation day
     */
    fun sendIrrigationRecommendationNotification(recommendation: IrrigationRecommendation) {
        if (!areNotificationsEnabled()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")
        val formattedDate = recommendation.date.format(dateFormatter)

        val title = "Optimal Irrigation Day"
        val message = buildNotificationMessage(recommendation, formattedDate)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop) // You'll need to add this icon
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(buildDetailedNotificationMessage(recommendation, formattedDate)),
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationId = NOTIFICATION_ID_BASE + recommendation.date.dayOfYear

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Handle case where notification permission is not granted
            e.printStackTrace()
        }
    }

    /**
     * Send daily irrigation summary notification
     */
    fun sendDailyIrrigationSummary(recommendations: List<IrrigationRecommendation>) {
        if (!areNotificationsEnabled() || recommendations.isEmpty()) return

        val bestDays = recommendations.filter { it.score >= 0.7f }.take(3)
        if (bestDays.isEmpty()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val title = "Weekly Irrigation Forecast"
        val message = buildSummaryMessage(bestDays)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(buildDetailedSummaryMessage(bestDays)),
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BASE + 1000, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Send urgent irrigation alert for critically dry conditions
     */
    fun sendUrgentIrrigationAlert(recommendation: IrrigationRecommendation) {
        if (!areNotificationsEnabled()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            2,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMM d")
        val formattedDate = recommendation.date.format(dateFormatter)

        val title = "🚨 Urgent: Irrigation Needed"
        val message = "Critical conditions detected for $formattedDate. Immediate irrigation recommended."

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(buildUrgentNotificationMessage(recommendation, formattedDate)),
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BASE + 2000, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Clear all irrigation notifications
     */
    fun clearAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }

    /**
     * Check if notifications are enabled
     */
    private fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    /**
     * Build notification message for single recommendation
     */
    private fun buildNotificationMessage(recommendation: IrrigationRecommendation, formattedDate: String): String {
        val scorePercent = (recommendation.score * 100).toInt()
        return "$formattedDate - $scorePercent% optimal conditions. ${getConditionSummary(recommendation)}"
    }

    /**
     * Build detailed notification message
     */
    private fun buildDetailedNotificationMessage(recommendation: IrrigationRecommendation, formattedDate: String): String {
        val scorePercent = (recommendation.score * 100).toInt()
        val soilMoisture = recommendation.soilMoisture?.let { "${(it * 100).toInt()}%" } ?: "N/A"
        val precipChance = "${recommendation.precipitationProbability.toInt()}%"
        val windSpeed = "${recommendation.windSpeed.toInt()} km/h"
        val temp = "${recommendation.temperature.toInt()}°C"

        return "$formattedDate - $scorePercent% optimal conditions\n\n" +
            "🌡️ Temperature: $temp\n" +
            "💧 Soil Moisture: $soilMoisture\n" +
            "🌧️ Rain Chance: $precipChance\n" +
            "💨 Wind Speed: $windSpeed\n\n" +
            recommendation.recommendationReason
    }

    /**
     * Build summary message for multiple recommendations
     */
    private fun buildSummaryMessage(bestDays: List<IrrigationRecommendation>): String {
        val dateFormatter = DateTimeFormatter.ofPattern("EEE d")
        val daysList = bestDays.take(2).joinToString(", ") { it.date.format(dateFormatter) }
        val moreCount = (bestDays.size - 2).coerceAtLeast(0)

        return if (moreCount > 0) {
            "Best days: $daysList + $moreCount more"
        } else {
            "Best days: $daysList"
        }
    }

    /**
     * Build detailed summary message
     */
    private fun buildDetailedSummaryMessage(bestDays: List<IrrigationRecommendation>): String {
        val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
        val sb = StringBuilder("Optimal irrigation days this week:\n\n")

        bestDays.forEachIndexed { index, recommendation ->
            val formattedDate = recommendation.date.format(dateFormatter)
            val scorePercent = (recommendation.score * 100).toInt()
            val conditionSummary = getConditionSummary(recommendation)

            sb.append("${index + 1}. $formattedDate ($scorePercent%)\n")
            sb.append("   $conditionSummary\n\n")
        }

        return sb.toString().trim()
    }

    /**
     * Build urgent notification message
     */
    private fun buildUrgentNotificationMessage(recommendation: IrrigationRecommendation, formattedDate: String): String {
        val soilMoisture = recommendation.soilMoisture?.let { "${(it * 100).toInt()}%" } ?: "N/A"
        val temp = "${recommendation.temperature.toInt()}°C"

        return "🚨 URGENT IRRIGATION NEEDED\n\n" +
            "$formattedDate\n" +
            "🌡️ Temperature: $temp\n" +
            "💧 Soil Moisture: $soilMoisture (Critical)\n\n" +
            "Immediate action recommended to prevent crop stress."
    }

    /**
     * Get condition summary for recommendation
     */
    private fun getConditionSummary(recommendation: IrrigationRecommendation): String {
        val soilMoisture = recommendation.soilMoisture ?: 0.5
        val precipChance = recommendation.precipitationProbability

        return when {
            soilMoisture < 0.2 && precipChance < 10 -> "Dry soil, no rain expected"
            soilMoisture < 0.3 && precipChance < 20 -> "Low soil moisture, minimal rain"
            precipChance < 15 -> "Good conditions, low rain chance"
            recommendation.windSpeed < 10 -> "Calm winds, ideal for irrigation"
            else -> "Favorable irrigation conditions"
        }
    }
}
