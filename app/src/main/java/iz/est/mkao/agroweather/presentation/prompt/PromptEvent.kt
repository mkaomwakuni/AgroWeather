package iz.est.mkao.agroweather.presentation.prompt

sealed class PromptEvent {
    data class OnSuggestionSelected(val suggestion: String) : PromptEvent()
    data class OnLocationChanged(
        val cityName: String,
        val latitude: Double,
        val longitude: Double,
    ) : PromptEvent()
    object OnLoadSuggestions : PromptEvent()
}
