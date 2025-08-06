package iz.est.mkao.agroweather.util

object Constants {
    
    object Network {
        const val CONNECT_TIMEOUT = 30L // seconds
        const val READ_TIMEOUT = 30L // seconds
        const val WRITE_TIMEOUT = 30L // seconds
        const val CACHE_SIZE = 10L * 1024 * 1024 // 10 MB
        const val CACHE_MAX_AGE = 60 // 1 minute in seconds
        const val CACHE_MAX_STALE = 60 * 60 * 24 * 7 // 1 week in seconds
    }
    
    object Weather {
        // Temperature thresholds (Celsius)
        const val HEAT_STRESS_THRESHOLD = 30.0
        const val COLD_STRESS_THRESHOLD = 5.0
        const val FREEZING_POINT = 0.0
        
        // Precipitation thresholds
        const val HIGH_PRECIPITATION_PROBABILITY = 70.0
        const val LOW_PRECIPITATION_PROBABILITY = 20.0
        
        // Wind speed thresholds (km/h)
        const val HIGH_WIND_SPEED_THRESHOLD = 25.0
        const val MAX_WIND_SPEED_FOR_IRRIGATION = 25.0
        
        // Humidity thresholds (%)
        const val HIGH_HUMIDITY_THRESHOLD = 80.0
        const val LOW_HUMIDITY_THRESHOLD = 40.0
        
        // UV Index thresholds
        const val MODERATE_UV_INDEX = 3.0
        const val HIGH_UV_INDEX = 6.0
        const val VERY_HIGH_UV_INDEX = 8.0
        const val EXTREME_UV_INDEX = 11.0
        
        // Forecast limits
        const val MAX_FORECAST_DAYS = 16
        const val DEFAULT_FORECAST_DAYS = 7
    }
    
    object Irrigation {
        // Soil temperature thresholds (°C)
        const val WARM_SEASON_CROP_MIN_SOIL_TEMP = 15.0 // 60°F
        const val COOL_SEASON_CROP_MIN_SOIL_TEMP = 4.0  // 40°F
        const val FROZEN_SOIL_THRESHOLD = 0.0
        
        // Soil moisture thresholds (as percentage of field capacity)
        const val SANDY_SOIL_IRRIGATE_THRESHOLD = 0.5   // 50% depletion
        const val LOAMY_SOIL_IRRIGATE_THRESHOLD = 0.35  // 65% depletion  
        const val CLAY_SOIL_IRRIGATE_THRESHOLD = 0.45   // 55% depletion
        const val WATERLOGGED_THRESHOLD = 0.8           // 80% field capacity
        const val DROUGHT_STRESS_THRESHOLD = 0.2        // 20% field capacity
        
        // Environmental conditions
        const val HIGH_PRECIPITATION_THRESHOLD = 0.5    // 0.5 inches (12.7mm)
        const val HIGH_HUMIDITY_IRRIGATION_THRESHOLD = 80.0 // 80-90% RH
        const val HEAT_STRESS_TEMPERATURE = 35.0        // Above 35°C
        const val COLD_STRESS_TEMPERATURE = 5.0         // Below 5°C
        
        // Default values when data not available
        const val DEFAULT_SOIL_MOISTURE = 0.4
        const val DEFAULT_SOIL_TEMPERATURE = 18.0
    }
    
    object UI {
        // Animation durations (milliseconds)
        const val SHORT_ANIMATION_DURATION = 150
        const val MEDIUM_ANIMATION_DURATION = 300
        const val LONG_ANIMATION_DURATION = 500
        
        // Dimensions (dp)
        const val SMALL_PADDING = 8
        const val MEDIUM_PADDING = 16
        const val LARGE_PADDING = 24
        const val EXTRA_LARGE_PADDING = 32
        
        const val SMALL_CORNER_RADIUS = 8
        const val MEDIUM_CORNER_RADIUS = 12
        const val LARGE_CORNER_RADIUS = 16
        
        const val WEATHER_CARD_HEIGHT = 120
        const val FORECAST_ITEM_HEIGHT = 80
        const val IRRIGATION_CARD_HEIGHT = 200
        
        // Icon sizes
        const val SMALL_ICON_SIZE = 24
        const val MEDIUM_ICON_SIZE = 48
        const val LARGE_ICON_SIZE = 64
        const val EXTRA_LARGE_ICON_SIZE = 96
        
        // Text sizes (sp)
        const val CAPTION_TEXT_SIZE = 12
        const val BODY_SMALL_TEXT_SIZE = 14
        const val BODY_TEXT_SIZE = 16
        const val TITLE_SMALL_TEXT_SIZE = 18
        const val TITLE_TEXT_SIZE = 20
        const val HEADLINE_TEXT_SIZE = 24
        const val DISPLAY_TEXT_SIZE = 32
    }
    
    object Database {
        const val DATABASE_NAME = "farm_weather_database"
        const val DATABASE_VERSION = 1
        
        // Table names
        const val CHAT_MESSAGES_TABLE = "chat_messages"
        const val CHAT_SESSIONS_TABLE = "chat_sessions"
        const val WEATHER_CACHE_TABLE = "weather_cache"
        const val NEWS_CACHE_TABLE = "news_cache"
        
        // Cache durations (milliseconds)
        const val WEATHER_CACHE_DURATION = 30 * 60 * 1000L // 30 minutes
        const val NEWS_CACHE_DURATION = 60 * 60 * 1000L // 1 hour
        const val CHAT_CACHE_DURATION = 24 * 60 * 60 * 1000L // 24 hours
    }
    
    object Performance {
        // Memory thresholds
        const val LOW_MEMORY_THRESHOLD_MB = 50
        const val CRITICAL_MEMORY_THRESHOLD_MB = 25
        
        // Performance monitoring intervals (milliseconds)
        const val PERFORMANCE_MONITORING_INTERVAL = 30 * 1000L // 30 seconds
        const val MEMORY_MONITORING_INTERVAL = 60 * 1000L // 1 minute
        
        // Crash reporting
        const val MAX_CRASH_REPORTS_PER_SESSION = 5
        const val CRASH_REPORT_COOLDOWN_MS = 60 * 1000L // 1 minute
    }
    
    object Notifications {
        // Channel IDs
        const val IRRIGATION_CHANNEL_ID = "irrigation_notifications"
        const val WEATHER_ALERTS_CHANNEL_ID = "weather_alerts"
        const val NEWS_UPDATES_CHANNEL_ID = "news_updates"
        
        // Notification IDs
        const val IRRIGATION_NOTIFICATION_ID = 1001
        const val WEATHER_ALERT_NOTIFICATION_ID = 1002
        const val NEWS_UPDATE_NOTIFICATION_ID = 1003
        
        // Work manager tags
        const val IRRIGATION_CHECK_WORK_TAG = "irrigation_check_work"
        const val WEATHER_UPDATE_WORK_TAG = "weather_update_work"
        const val NEWS_UPDATE_WORK_TAG = "news_update_work"
    }
    
    object Accessibility {
        // Content descriptions
        const val WEATHER_ICON_CONTENT_DESCRIPTION = "Weather condition icon"
        const val TEMPERATURE_CONTENT_DESCRIPTION = "Temperature reading"
        const val HUMIDITY_CONTENT_DESCRIPTION = "Humidity level"
        const val WIND_SPEED_CONTENT_DESCRIPTION = "Wind speed"
        const val PRECIPITATION_CONTENT_DESCRIPTION = "Precipitation probability"
        const val UV_INDEX_CONTENT_DESCRIPTION = "UV index level"
        
        // Semantic labels
        const val IRRIGATION_STATUS_SUITABLE = "Irrigation conditions are suitable"
        const val IRRIGATION_STATUS_NOT_SUITABLE = "Irrigation conditions are not suitable"
        const val WEATHER_LOADING = "Loading weather data"
        const val WEATHER_ERROR = "Error loading weather data"
    }
}
