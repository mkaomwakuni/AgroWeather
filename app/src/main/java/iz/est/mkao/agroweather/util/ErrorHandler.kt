package iz.est.mkao.agroweather.util

import androidx.compose.ui.res.stringResource
import iz.est.mkao.agroweather.R
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Simple centralized error handling utility
 */
object ErrorHandler {

    private const val TAG = "ErrorHandler"

    /**
     * Convert exceptions to user-friendly error messages
     */
    fun getErrorMessage(throwable: Throwable): String {
        SecureLogger.e(TAG, "Error occurred", throwable)

        return when (throwable) {
            is UnknownHostException,
            is ConnectException -> "No internet connection. Please check your network."

            is SocketTimeoutException -> "Request timed out. Please try again."

            is HttpException -> when (throwable.code()) {
                401 -> "Authentication failed. Please check your API keys."
                403 -> "Access forbidden. Please check your permissions."
                404 -> "Requested resource not found."
                429 -> "Too many requests. Please try again later."
                500, 502, 503, 504 -> "Server error. Please try again later."
                else -> "Server error (${throwable.code()}). Please try again."
            }

            is IOException -> "Network error. Please check your connection."

            is SecurityException -> "Permission denied. Please check app permissions."

            else -> throwable.message ?: "An unexpected error occurred."
        }
    }

    /**
     * Check if error is recoverable (can retry)
     */
    fun isRecoverableError(throwable: Throwable): Boolean {
        return when (throwable) {
            is SocketTimeoutException,
            is UnknownHostException,
            is ConnectException,
            is IOException -> true

            is HttpException -> throwable.code() in listOf(408, 429, 500, 502, 503, 504)

            else -> false
        }
    }

    /**
     * Determine retry delay based on error type
     */
    fun getRetryDelay(throwable: Throwable, attempt: Int): Long {
        val baseDelay = when (throwable) {
            is HttpException -> when (throwable.code()) {
                429 -> 60000L // Rate limited - wait 1 minute
                else -> 1000L
            }
            is SocketTimeoutException -> 2000L
            else -> 1000L
        }

        // Exponential backoff with jitter
        return baseDelay * (attempt + 1) + (0..1000).random()
    }
}
