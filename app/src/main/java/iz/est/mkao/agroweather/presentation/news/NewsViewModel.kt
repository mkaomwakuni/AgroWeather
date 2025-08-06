package iz.est.mkao.agroweather.presentation.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import iz.est.mkao.agroweather.data.model.NewsArticle
import iz.est.mkao.agroweather.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadLatestNews()
    }

    fun loadLatestNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = newsRepository.getLatestAgriculturalNews()) {
                is iz.est.mkao.farmweather.util.Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        news = result.data,
                        isLoading = false,
                        error = null,
                    )
                }
                is iz.est.mkao.farmweather.util.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to load news",
                    )
                }
                is iz.est.mkao.farmweather.util.Result.Loading -> {
                    // Keep loading state
                }
            }
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            when (val result = newsRepository.searchAgriculturalNews(query)) {
                is iz.est.mkao.farmweather.util.Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        news = result.data,
                        isLoading = false,
                        error = null,
                    )
                }
                is iz.est.mkao.farmweather.util.Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.exception.message ?: "Failed to search news",
                    )
                }
                is iz.est.mkao.farmweather.util.Result.Loading -> {
                    // Keep loading state
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class NewsUiState(
    val news: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)
