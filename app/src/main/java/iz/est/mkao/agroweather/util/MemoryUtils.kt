package iz.est.mkao.agroweather.util

import android.app.ActivityManager
import android.content.Context

/**
 * Simple memory management utilities
 */
object MemoryUtils {

    private const val TAG = "MemoryUtils"

    /**
     * Check if device is running low on memory
     */
    fun isLowMemory(context: Context): Boolean {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            memoryInfo.lowMemory
        } catch (e: Exception) {
            SecureLogger.e(TAG, "Failed to check memory status", e)
            false
        }
    }

    /**
     * Get available memory in MB
     */
    fun getAvailableMemoryMB(context: Context): Long {
        return try {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            memoryInfo.availMem / (1024 * 1024) // Convert to MB
        } catch (e: Exception) {
            SecureLogger.e(TAG, "Failed to get memory info", e)
            0L
        }
    }

    /**
     * Clean up cache if low on memory
     */
    fun cleanupIfNeeded(context: Context) {
        if (isLowMemory(context)) {
            try {
                // Clear app cache
                context.cacheDir.deleteRecursively()
                SecureLogger.d(TAG, "Cache cleared due to low memory")
            } catch (e: Exception) {
                SecureLogger.e(TAG, "Failed to cleanup cache", e)
            }
        }
    }
}
