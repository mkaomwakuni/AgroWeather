# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep BuildConfig class
-keep class iz.est.mkao.agroweather.BuildConfig { *; }

# Keep data models for Gson serialization
-keep class iz.est.mkao.agroweather.data.model.** { *; }
-keep class iz.est.mkao.agroweather.data.remote.dto.** { *; }

# Keep Retrofit service interfaces
-keep interface iz.est.mkao.agroweather.data.api.** { *; }

# Gson specific rules
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Retrofit specific rules
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keep interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp specific rules
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }

# Room specific rules
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Hilt specific rules
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp
-keepclasseswithmembernames class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# Gemini AI specific rules
-keep class com.google.ai.client.generativeai.** { *; }

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# Keep Compose specific classes
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Remove logging in release builds for security
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# Remove println statements
-assumenosideeffects class java.io.PrintStream {
    public void println(%);
    public void println(**);
}

# Remove printStackTrace calls in release
-assumenosideeffects class java.lang.Throwable {
    public void printStackTrace();
}

# Security: Obfuscate sensitive classes
-keep class iz.est.mkao.agroweather.util.SecurityUtils { *; }
-keep class iz.est.mkao.agroweather.data.local.** { *; }

# Security: Remove debug information but keep crash reporting
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Security: Aggressive obfuscation
-overloadaggressively
-repackageclasses ''
-allowaccessmodification

# Security: Remove unused code
-dontwarn **
-ignorewarnings