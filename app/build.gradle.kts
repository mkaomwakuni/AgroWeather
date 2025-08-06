import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.spotless)
}

android {
    namespace = "iz.est.mkao.agroweather"
    compileSdk = 36

    defaultConfig {
        applicationId = "iz.est.mkao.agroweather"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }


        val localProperties = Properties()
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localProperties.load(localPropertiesFile.inputStream())
        }

        buildConfigField("String", "WEATHER_API_KEY", "\"${localProperties.getProperty("WEATHER_API_KEY", "your_weather_api_key_here")}\"")
        buildConfigField("String", "GEMINI_API_KEY", "\"${localProperties.getProperty("GEMINI_API_KEY", "your_gemini_api_key_here")}\"")
        buildConfigField("String", "NEWS_API_KEY", "\"${localProperties.getProperty("NEWS_API_KEY", "your_news_api_key_here")}\"")
    }

    buildTypes {
        debug {
            isDebuggable = true
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            buildConfigField("boolean", "DEBUG_MODE", "true")
            buildConfigField("String", "LOG_LEVEL", "\"DEBUG\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "true")
            

            manifestPlaceholders["usesCleartextTraffic"] = true
        }

        release {

            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            
            buildConfigField("boolean", "DEBUG_MODE", "false")
            buildConfigField("String", "LOG_LEVEL", "\"ERROR\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")
            

            manifestPlaceholders["usesCleartextTraffic"] = false
            

            buildConfigField("boolean", "STRIP_DEBUG", "true")
        }

        create("staging") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            buildConfigField("boolean", "DEBUG_MODE", "true")
            buildConfigField("String", "LOG_LEVEL", "\"INFO\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"


        freeCompilerArgs += listOf(
            "-opt-in=kotlin.RequiresOptIn",
            "-Xjsr305=strict",
        )
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint("0.50.0").editorConfigOverride(
            mapOf(
                "indent_size" to "4",
                "continuation_indent_size" to "4",
                "ktlint_standard_no-wildcard-imports" to "disabled",
            ),
        )
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("0.50.0")
    }
    format("xml") {
        target("**/*.xml")
        targetExclude("**/build/**/*.xml")
        eclipseWtp(com.diffplug.spotless.extra.wtp.EclipseWtpFormatterStep.XML)
    }
    format("misc") {
        target("**/*.gradle", "**/*.md", "**/.gitignore")
        targetExclude("**/build/**")
        trimTrailingWhitespace()
        indentWithSpaces(4)
        endWithNewline()
    }
}

dependencies {
    // Core Android dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.process)

    // Retrofit for API calls
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)

    // OkHttp for logging
    implementation(libs.okhttp.logging)

    // Coil for image loading
    implementation(libs.coil.compose)

    // System UI Controller for transparent status bar
    implementation(libs.accompanist.systemuicontroller)

    // Charts for data visualization - using working alternatives
    implementation(libs.androidx.material)
    implementation(libs.ycharts)

    // Gemini AI
    implementation(libs.gemini.ai)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Room for database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    ksp(libs.room.compiler)

    // Dagger Hilt for DI
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    // WorkManager for background tasks
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler.additional)

    // OSMDroid for maps
    implementation("org.osmdroid:osmdroid-android:6.1.16")
    implementation("androidx.preference:preference-ktx:1.2.1")
    
    // Lottie for animations
    implementation("com.airbnb.android:lottie-compose:6.2.0")



    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
