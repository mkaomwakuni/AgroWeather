package iz.est.mkao.agroweather.util

/**
 * A generic wrapper around success and error states for API calls and data operations
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
