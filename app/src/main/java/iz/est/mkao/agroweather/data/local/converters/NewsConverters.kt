package iz.est.mkao.agroweather.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import iz.est.mkao.agroweather.data.model.NewsArticle
import iz.est.mkao.agroweather.data.model.NewsSource

class NewsConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromNewsArticleList(newsArticleList: List<NewsArticle>): String {
        return gson.toJson(newsArticleList)
    }

    @TypeConverter
    fun toNewsArticleList(newsArticleListString: String): List<NewsArticle> {
        val listType = object : TypeToken<List<NewsArticle>>() {}.type
        return gson.fromJson(newsArticleListString, listType)
    }

    @TypeConverter
    fun fromNewsSource(newsSource: NewsSource?): String? {
        return newsSource?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toNewsSource(newsSourceString: String?): NewsSource? {
        return newsSourceString?.let { gson.fromJson(it, NewsSource::class.java) }
    }
}
