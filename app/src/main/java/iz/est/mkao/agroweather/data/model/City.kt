package iz.est.mkao.agroweather.data.model

/**
 * Represents a city with its location coordinates
 */
data class City(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val country: String = "Kenya",
    val region: String = "",
    val isDefault: Boolean = false,
) {
    /**
     * Returns formatted location string for API calls
     */
    fun getLocationString(): String = "$latitude,$longitude"

    /**
     * Returns display name with region if available
     */
    fun getDisplayName(): String = if (region.isNotEmpty()) "$name, $region" else name
}
