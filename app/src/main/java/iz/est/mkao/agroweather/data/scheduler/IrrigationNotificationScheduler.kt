package iz.est.mkao.agroweather.data.scheduler

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import iz.est.mkao.agroweather.data.worker.IrrigationNotificationWorker
import iz.est.mkao.agroweather.util.Logger
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Scheduler for managing irrigation notification background tasks
 */
@Singleton
class IrrigationNotificationScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    companion object {
        private const val TAG = "IrrigationNotificationScheduler"
        private const val DAILY_CHECK_WORK_NAME = "daily_irrigation_check"
        private const val MORNING_CHECK_WORK_NAME = "morning_irrigation_check"
        private const val EVENING_CHECK_WORK_NAME = "evening_irrigation_check"
    }

    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule periodic irrigation notification checks
     */
    fun schedulePeriodicNotifications() {
        Logger.d(TAG, "Scheduling periodic irrigation notifications")
        scheduleDailyMorningCheck()
        scheduleDailyEveningCheck()
    }

    /**
     * Schedule morning irrigation check (7 AM daily)
     * Checks for today's irrigation conditions and sends immediate notifications
     */
    private fun scheduleDailyMorningCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val morningCheckRequest = PeriodicWorkRequestBuilder<IrrigationNotificationWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        )
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelayForMorning(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            MORNING_CHECK_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            morningCheckRequest,
        )

        Logger.d(TAG, "Scheduled morning irrigation check")
    }

    /**
     * Schedule evening irrigation planning check (6 PM daily)
     * Provides next-day irrigation recommendations
     */
    private fun scheduleDailyEveningCheck() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val eveningCheckRequest = PeriodicWorkRequestBuilder<IrrigationNotificationWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        )
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelayForEvening(), TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            EVENING_CHECK_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            eveningCheckRequest,
        )

        Logger.d(TAG, "Scheduled evening irrigation planning check")
    }

    /**
     * Schedule urgent irrigation check (runs every 6 hours during growing season)
     * For critical conditions that need immediate attention
     */
    fun scheduleUrgentChecks() {
        Logger.d(TAG, "Scheduling urgent irrigation checks")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val urgentCheckRequest = PeriodicWorkRequestBuilder<IrrigationNotificationWorker>(
            repeatInterval = 6,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            "urgent_irrigation_check",
            ExistingPeriodicWorkPolicy.UPDATE,
            urgentCheckRequest,
        )

        Logger.d(TAG, "Scheduled urgent irrigation checks every 6 hours")
    }

    /**
     * Cancel all scheduled irrigation notifications
     */
    fun cancelAllNotifications() {
        Logger.d(TAG, "Cancelling all irrigation notification schedules")

        workManager.cancelUniqueWork(MORNING_CHECK_WORK_NAME)
        workManager.cancelUniqueWork(EVENING_CHECK_WORK_NAME)
        workManager.cancelUniqueWork("urgent_irrigation_check")

        Logger.d(TAG, "All irrigation notification schedules cancelled")
    }

    /**
     * Enable irrigation notifications with default schedule
     */
    fun enableNotifications() {
        Logger.d(TAG, "Enabling irrigation notifications")
        schedulePeriodicNotifications()
    }

    /**
     * Disable irrigation notifications
     */
    fun disableNotifications() {
        Logger.d(TAG, "Disabling irrigation notifications")
        cancelAllNotifications()
    }

    /**
     * Check if notifications are currently scheduled
     */
    fun areNotificationsScheduled(): Boolean {
        val workInfos = workManager.getWorkInfosForUniqueWork(MORNING_CHECK_WORK_NAME).get()
        return workInfos.isNotEmpty() && workInfos.any { !it.state.isFinished }
    }

    /**
     * Calculate initial delay to schedule morning check at 7 AM
     */
    private fun calculateInitialDelayForMorning(): Long {
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()

        // Set to 7 AM today
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 7)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

        var targetTime = calendar.timeInMillis

        // If 7 AM has passed today, schedule for tomorrow
        if (targetTime <= now) {
            targetTime += 24 * 60 * 60 * 1000 // Add 24 hours
        }

        return targetTime - now
    }

    /**
     * Calculate initial delay to schedule evening check at 6 PM
     */
    private fun calculateInitialDelayForEvening(): Long {
        val now = System.currentTimeMillis()
        val calendar = java.util.Calendar.getInstance()

        // Set to 6 PM today
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 18)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

        var targetTime = calendar.timeInMillis

        // If 6 PM has passed today, schedule for tomorrow
        if (targetTime <= now) {
            targetTime += 24 * 60 * 60 * 1000 // Add 24 hours
        }

        return targetTime - now
    }

    /**
     * Trigger immediate irrigation check (for testing or manual refresh)
     */
    fun triggerImmediateCheck() {
        Logger.d(TAG, "Triggering immediate irrigation check")

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val immediateRequest = OneTimeWorkRequestBuilder<IrrigationNotificationWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(immediateRequest)

        Logger.d(TAG, "Immediate irrigation check triggered")
    }
}
