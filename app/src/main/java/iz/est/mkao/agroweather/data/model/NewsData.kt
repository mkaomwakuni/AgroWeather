package iz.est.mkao.agroweather.data.model

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    val status: String,
    val totalResults: Int?,
    val articles: List<NewsArticle>,
) {
    // Compatibility property for existing code
    val results: List<NewsArticle> get() = articles
}

data class NewsArticle(
    val title: String,
    val description: String?,
    val content: String?,
    val url: String,
    @SerializedName("urlToImage")
    val urlToImage: String?,
    @SerializedName("publishedAt")
    val publishedAt: String,
    val source: NewsSource,
    val author: String?,
) {
    // Computed properties for backward compatibility
    val id: Long get() = url.hashCode().toLong()
    val body: String? get() = content
    val href: String get() = url
    val image: String? get() = urlToImage
    val imageUrl: String? get() = urlToImage
}

data class NewsSource(
    val id: String?,
    val name: String,
) {
    // Computed properties for backward compatibility
    val domain: String? get() = null
    val homePageUrl: String? get() = null
    val favicon: String? get() = null
    val url: String? get() = null
    val icon: String? get() = null
}
