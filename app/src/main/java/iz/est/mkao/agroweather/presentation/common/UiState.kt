package iz.est.mkao.agroweather.presentation.common

/**
 * Common UI state classes for consistent state management across the app
 */

/**
 * Represents the loading state of an operation
 */
sealed class LoadingState {
    object Idle : LoadingState()
    object Loading : LoadingState()
    object Success : LoadingState()
    data class Error(val message: String, val throwable: Throwable? = null) : LoadingState()
}

/**
 * Generic wrapper for UI state with loading, error, and success states
 */
data class UiState<T>(
    val data: T? = null,
    val loadingState: LoadingState = LoadingState.Idle,
    val isRefreshing: Boolean = false,
) {
    val isLoading: Boolean get() = loadingState is LoadingState.Loading
    val isError: Boolean get() = loadingState is LoadingState.Error
    val isSuccess: Boolean get() = loadingState is LoadingState.Success
    val errorMessage: String? get() = (loadingState as? LoadingState.Error)?.message
}

/**
 * Extension functions for easier state management
 */
fun <T> UiState<T>.loading(): UiState<T> = copy(loadingState = LoadingState.Loading)
fun <T> UiState<T>.success(data: T): UiState<T> = copy(data = data, loadingState = LoadingState.Success)
fun <T> UiState<T>.error(message: String, throwable: Throwable? = null): UiState<T> =
    copy(loadingState = LoadingState.Error(message, throwable))
fun <T> UiState<T>.idle(): UiState<T> = copy(loadingState = LoadingState.Idle)
fun <T> UiState<T>.refreshing(isRefreshing: Boolean): UiState<T> = copy(isRefreshing = isRefreshing)
