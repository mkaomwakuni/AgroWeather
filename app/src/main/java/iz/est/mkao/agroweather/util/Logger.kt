package iz.est.mkao.agroweather.util

import iz.est.mkao.agroweather.AgroWeatherApp

/**
 * Centralized logging utility following Android best practices
 */
object Logger {

//    private const val TAG_PREFIX = "FarmWeather"

    fun d(tag: String, message: String, throwable: Throwable? = null) {
        if (AgroWeatherApp.DEBUG_MODE) {
//            Log.d("$TAG_PREFIX:$tag", message, throwable)
        }
    }

    fun i(tag: String, message: String, throwable: Throwable? = null) {
        if (shouldLog("INFO")) {
//            Log.i("$TAG_PREFIX:$tag", message, throwable)
        }
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (shouldLog("WARN")) {
//            Log.w("$TAG_PREFIX:$tag", message, throwable)
        }
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (shouldLog("ERROR")) {
//            Log.e("$TAG_PREFIX:$tag", message, throwable)
        }
    }

    private fun shouldLog(level: String): Boolean {
        val currentLevel = AgroWeatherApp.LOG_LEVEL
        return when (currentLevel) {
            "DEBUG" -> true
            "INFO" -> level in listOf("INFO", "WARN", "ERROR")
            "WARN" -> level in listOf("WARN", "ERROR")
            "ERROR" -> level == "ERROR"
            else -> false
        }
    }
}
