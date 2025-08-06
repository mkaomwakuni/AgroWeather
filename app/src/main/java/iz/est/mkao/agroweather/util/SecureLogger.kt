package iz.est.mkao.agroweather.util

import android.util.Log
import iz.est.mkao.agroweather.BuildConfig

/**
 * Secure logging utility that prevents logging in production builds
 * and sanitizes sensitive information from logs
 */
object SecureLogger {
    
    private const val TAG_PREFIX = "FarmWeather"

    private val SENSITIVE_PATTERNS = listOf(
        "api[_-]?key",
        "password",
        "token",
        "secret",
        "credential",
        "auth",
        "bearer",
        "session",
        "cookie"
    ).map { it.toRegex(RegexOption.IGNORE_CASE) }
    
    /**
     * Log debug messages (only in debug builds)
     */
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("$TAG_PREFIX:$tag", sanitizeMessage(message))
        }
    }
    
    /**
     * Log info messages (only in debug/staging builds)
     */
    fun i(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i("$TAG_PREFIX:$tag", sanitizeMessage(message))
        }
    }
    
    /**
     * Log warning messages
     */
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            if (throwable != null) {
                Log.w("$TAG_PREFIX:$tag", sanitizeMessage(message), sanitizeThrowable(throwable))
            } else {
                Log.w("$TAG_PREFIX:$tag", sanitizeMessage(message))
            }
        }
    }
    
    /**
     * Log error messages (always logged but sanitized)
     */
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (throwable != null) {
            Log.e("$TAG_PREFIX:$tag", sanitizeMessage(message), sanitizeThrowable(throwable))
        } else {
            Log.e("$TAG_PREFIX:$tag", sanitizeMessage(message))
        }
    }
    
    /**
     * Log network requests (only in debug, heavily sanitized)
     */
    fun network(tag: String, method: String, url: String, headers: Map<String, String>? = null) {
        if (BuildConfig.DEBUG) {
            val sanitizedUrl = sanitizeUrl(url)
            val sanitizedHeaders = headers?.let { sanitizeHeaders(it) }
            d(tag, "Network: $method $sanitizedUrl ${sanitizedHeaders ?: ""}")
        }
    }
    
    /**
     * Sanitize message to remove sensitive information
     */
    private fun sanitizeMessage(message: String): String {
        var sanitized = message
        SENSITIVE_PATTERNS.forEach { pattern ->
            sanitized = sanitized.replace(pattern, "[REDACTED]")
        }
        return sanitized
    }
    
    /**
     * Sanitize throwable stack traces
     */
    private fun sanitizeThrowable(throwable: Throwable): Throwable {
        return if (BuildConfig.DEBUG) {
            throwable
        } else {
            // In production, return sanitized exception
            Exception("Error occurred: ${throwable.javaClass.simpleName}")
        }
    }
    
    /**
     * Sanitize URLs to remove query parameters that might contain sensitive data
     */
    private fun sanitizeUrl(url: String): String {
        return try {
            val uri = android.net.Uri.parse(url)
            "${uri.scheme}://${uri.host}${uri.path}?[QUERY_PARAMS_REDACTED]"
        } catch (e: Exception) {
            "[URL_REDACTED]"
        }
    }
    
    /**
     * Sanitize HTTP headers
     */
    private fun sanitizeHeaders(headers: Map<String, String>): String {
        return headers.keys.joinToString(", ") { key ->
            if (SENSITIVE_PATTERNS.any { it.containsMatchIn(key) }) {
                "$key: [REDACTED]"
            } else {
                "$key: ${headers[key]?.take(10)}..."
            }
        }
    }
    
    /**
     * Check if logging is enabled for current build
     */
    fun isLoggingEnabled(): Boolean {
        return BuildConfig.DEBUG
    }
}
