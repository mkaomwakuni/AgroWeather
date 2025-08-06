package iz.est.mkao.agroweather.util

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * App lifecycle observer for memory and performance optimization
 */
class AppLifecycleObserver(
    private val context: Context,
) : DefaultLifecycleObserver {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val tag = "AppLifecycleObserver"

    fun register() {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        SecureLogger.d(tag, "App moved to foreground")
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        SecureLogger.d(tag, "App moved to background")


        scope.launch {
            performBackgroundCleanup()
        }
    }

    private suspend fun performBackgroundCleanup() {
        try {
            // Clean up cache if needed
            MemoryUtils.cleanupIfNeeded(context)

            // Force garbage collection if low memory
            if (MemoryUtils.isLowMemory(context)) {
                System.gc()
                SecureLogger.d(tag, "Performed garbage collection due to low memory")
            }

            SecureLogger.d(tag, "Background cleanup completed")
        } catch (e: Exception) {
            SecureLogger.e(tag, "Error during background cleanup", e)
        }
    }
}
