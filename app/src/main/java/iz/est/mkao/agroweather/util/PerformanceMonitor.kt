package iz.est.mkao.agroweather.util

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import dagger.hilt.android.qualifiers.ApplicationContext
import iz.est.mkao.agroweather.AgroWeatherApp
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance monitoring utility for tracking app performance metrics
 */
@Singleton
class PerformanceMonitor @Inject constructor(
    @ApplicationContext private val context: Context
) : DefaultLifecycleObserver {

    companion object {
        private const val TAG = "PerformanceMonitor"
    }

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

    // Performance metrics
    private val _memoryUsage = MutableStateFlow(MemoryUsage())
    val memoryUsage: StateFlow<MemoryUsage> = _memoryUsage.asStateFlow()

    private val _performanceMetrics = MutableStateFlow(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()

    // Crash tracking
    private val crashCount = AtomicInteger(0)
    private val lastCrashTime = AtomicLong(0)

    // Performance monitoring state
    private var monitoringJob: Job? = null
    private var isMonitoring = false

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        setupUncaughtExceptionHandler()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        startMonitoring()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        stopMonitoring()
    }

    /**
     * Start performance monitoring
     */
    fun startMonitoring() {
        if (isMonitoring) return
        
        isMonitoring = true
        monitoringJob = coroutineScope.launch {
            while (isActive && isMonitoring) {
                try {
                    updateMemoryMetrics()
                    updatePerformanceMetrics()
                    delay(Constants.Performance.MEMORY_MONITORING_INTERVAL)
                } catch (e: Exception) {
                    if (AgroWeatherApp.DEBUG_MODE) {
                        Log.e(TAG, "Error in performance monitoring", e)
                    }
                }
            }
        }
        
        if (AgroWeatherApp.DEBUG_MODE) {
            Log.d(TAG, "Performance monitoring started")
        }
    }

    /**
     * Stop performance monitoring
     */
    fun stopMonitoring() {
        isMonitoring = false
        monitoringJob?.cancel()
        monitoringJob = null
        
        if (AgroWeatherApp.DEBUG_MODE) {
            Log.d(TAG, "Performance monitoring stopped")
        }
    }

    /**
     * Update memory usage metrics
     */
    private fun updateMemoryMetrics() {
        try {
            val memoryInfo = ActivityManager.MemoryInfo()
            activityManager.getMemoryInfo(memoryInfo)
            
            val runtime = Runtime.getRuntime()
            val usedMemory = runtime.totalMemory() - runtime.freeMemory()
            val maxMemory = runtime.maxMemory()
            val availableMemory = maxMemory - usedMemory
            
            val memoryUsage = MemoryUsage(
                usedMemoryMB = usedMemory / (1024 * 1024),
                totalMemoryMB = runtime.totalMemory() / (1024 * 1024),
                maxMemoryMB = maxMemory / (1024 * 1024),
                availableMemoryMB = availableMemory / (1024 * 1024),
                systemAvailableMemoryMB = memoryInfo.availMem / (1024 * 1024),
                isLowMemory = memoryInfo.lowMemory,
                memoryUsagePercentage = (usedMemory.toFloat() / maxMemory * 100).toInt()
            )
            
            _memoryUsage.value = memoryUsage
            
            // Log warnings if memory usage is high
            if (memoryUsage.availableMemoryMB < Constants.Performance.LOW_MEMORY_THRESHOLD_MB) {
                if (AgroWeatherApp.DEBUG_MODE) {
                    Log.w(TAG, "Low memory warning: ${memoryUsage.availableMemoryMB}MB available")
                }
            }
            
            if (memoryUsage.availableMemoryMB < Constants.Performance.CRITICAL_MEMORY_THRESHOLD_MB) {
                if (AgroWeatherApp.DEBUG_MODE) {
                    Log.e(TAG, "Critical memory warning: ${memoryUsage.availableMemoryMB}MB available")
                }
                // Could trigger memory cleanup here
                suggestGarbageCollection()
            }
            
        } catch (e: Exception) {
            if (AgroWeatherApp.DEBUG_MODE) {
                Log.e(TAG, "Error updating memory metrics", e)
            }
        }
    }

    /**
     * Update performance metrics
     */
    private fun updatePerformanceMetrics() {
        try {
            val currentMetrics = _performanceMetrics.value
            val timestamp = System.currentTimeMillis()
            
            val updatedMetrics = currentMetrics.copy(
                lastUpdated = timestamp,
                uptime = System.currentTimeMillis() - currentMetrics.startTime,
                crashCount = crashCount.get()
            )
            
            _performanceMetrics.value = updatedMetrics
            
        } catch (e: Exception) {
            if (AgroWeatherApp.DEBUG_MODE) {
                Log.e(TAG, "Error updating performance metrics", e)
            }
        }
    }

    /**
     * Suggest garbage collection if memory is low
     */
    private fun suggestGarbageCollection() {
        try {
            System.gc()
            if (AgroWeatherApp.DEBUG_MODE) {
                Log.d(TAG, "Garbage collection suggested due to low memory")
            }
        } catch (e: Exception) {
            if (AgroWeatherApp.DEBUG_MODE) {
                Log.e(TAG, "Error suggesting garbage collection", e)
            }
        }
    }

    /**
     * Track a crash occurrence
     */
    fun trackCrash(throwable: Throwable) {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastCrash = currentTime - lastCrashTime.get()
        
        // Only count if it's been more than the cooldown period since last crash
        if (timeSinceLastCrash > Constants.Performance.CRASH_REPORT_COOLDOWN_MS) {
            val count = crashCount.incrementAndGet()
            lastCrashTime.set(currentTime)
            
            if (AgroWeatherApp.DEBUG_MODE) {
                Log.e(TAG, "Crash tracked (#$count): ${throwable.message}", throwable)
            }
            
            // Update performance metrics
            val currentMetrics = _performanceMetrics.value
            _performanceMetrics.value = currentMetrics.copy(
                crashCount = count,
                lastCrashTime = currentTime
            )
        }
    }

    /**
     * Get current performance summary
     */
    fun getPerformanceSummary(): String {
        val memory = _memoryUsage.value
        val metrics = _performanceMetrics.value
        
        return buildString {
            appendLine("=== Performance Summary ===")
            appendLine("Memory Usage: ${memory.usedMemoryMB}MB / ${memory.maxMemoryMB}MB (${memory.memoryUsagePercentage}%)")
            appendLine("Available Memory: ${memory.availableMemoryMB}MB")
            appendLine("System Available: ${memory.systemAvailableMemoryMB}MB")
            appendLine("Low Memory: ${memory.isLowMemory}")
            appendLine("Uptime: ${metrics.uptime / 1000}s")
            appendLine("Crashes: ${metrics.crashCount}")
        }
    }

    /**
     * Setup uncaught exception handler for crash tracking
     */
    private fun setupUncaughtExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            trackCrash(throwable)
            
            // Call the default handler to maintain normal crash behavior
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        stopMonitoring()
        coroutineScope.cancel()
    }

    data class MemoryUsage(
        val usedMemoryMB: Long = 0,
        val totalMemoryMB: Long = 0,
        val maxMemoryMB: Long = 0,
        val availableMemoryMB: Long = 0,
        val systemAvailableMemoryMB: Long = 0,
        val isLowMemory: Boolean = false,
        val memoryUsagePercentage: Int = 0
    )

    data class PerformanceMetrics(
        val startTime: Long = System.currentTimeMillis(),
        val lastUpdated: Long = System.currentTimeMillis(),
        val uptime: Long = 0,
        val crashCount: Int = 0,
        val lastCrashTime: Long = 0
    )
}
