package iz.est.mkao.agroweather.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.delay
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 *Network utilities
 */
object NetworkUtils {

    private const val TAG = "NetworkUtils"
    private const val MAX_RETRY_ATTEMPTS = 3

    /**
     * Check if device has active internet connection
     */
    fun isNetworkAvailable(context: Context): Boolean {
        return try {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } catch (e: Exception) {
            SecureLogger.e(TAG, "Failed to check network availability", e)
            false
        }
    }

    /**
     * Execute API call with simple retry logic
     */
    suspend fun <T> executeWithRetry(
        maxRetries: Int = MAX_RETRY_ATTEMPTS,
        call: suspend () -> Response<T>,
    ): Response<T> {
        var lastException: Exception? = null

        repeat(maxRetries) { attempt ->
            try {
                val response = call()
                if (response.isSuccessful || !isRetryableError(response)) {
                    return response
                }
                SecureLogger.w(TAG, "Attempt ${attempt + 1} failed with code: ${response.code()}")
            } catch (e: Exception) {
                lastException = e
                SecureLogger.w(TAG, "Attempt ${attempt + 1} failed", e)

                if (!isRetryableException(e)) {
                    throw e
                }
            }

            if (attempt < maxRetries - 1) {
                delay((1000L * (attempt + 1)).toLong()) // Simple exponential backoff
            }
        }

        throw lastException ?: IOException("All retry attempts failed")
    }

    /**
     * Check if HTTP response error is retryable
     */
    private fun isRetryableError(response: Response<*>): Boolean {
        return response.code() in listOf(408, 429, 500, 502, 503, 504)
    }

    /**
     * Check if exception is retryable
     */
    private fun isRetryableException(exception: Exception): Boolean {
        return when (exception) {
            is SocketTimeoutException,
            is UnknownHostException,
            is IOException -> true
            is HttpException -> isRetryableError(Response.error<Any>(exception.code(), exception.response()?.errorBody()!!))
            else -> false
        }
    }
}
