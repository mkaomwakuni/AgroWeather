package iz.est.mkao.agroweather.presentation.news

import android.content.Context
import android.content.Intent
import iz.est.mkao.agroweather.data.model.NewsArticle
import java.text.SimpleDateFormat
import java.util.Locale

fun formatPublishedDate(publishedAt: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        val date = inputFormat.parse(publishedAt)
        date?.let { outputFormat.format(it) } ?: publishedAt
    } catch (e: Exception) {
        publishedAt
    }
}

fun shareArticle(article: NewsArticle, context: Context) {
    val shareText = buildString {
        append(article.title)
        append("\n\n")
        article.description?.let { description ->
            append(description)
            append("\n\n")
        }
        article.url?.let { url ->
            append("Read more: ")
            append(url)
        }
        append("\n\nShared from FarmWeather App")
    }
    
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, shareText)
        putExtra(Intent.EXTRA_SUBJECT, article.title)
    }
    
    context.startActivity(Intent.createChooser(shareIntent, "Share article"))
}
