# AgroWeather
AgroWeather is an AI Agriculture Android application specifically designed to optimize irrigation and spray operations using real-time weather information. Built with  Jetpack Compose and leveraging modern Android libraries, this app combines comprehensive weather data with AI-powered recommendations to help farmers make precise decisions about when and how to irrigate crops and apply sprays.

## Structural design pattern
The app is built with the Model-View-ViewModel (MVVM) as its structural design pattern that separates objects into three distinct groups:

- **Models** hold application data. They're usually structs or simple classes.
- **Views** display visual elements and controls on the screen. They're typically subclasses of UIView.
- **View models** transform model information into values that can be displayed on a view. They're usually classes, so they can be passed around as references.

## 📸 App Screenshots

<table>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/a37e3dd4-cd6b-4c28-91e7-ddde5df09f44" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/7290f626-356d-413f-b851-7810120307b6" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/507d75a0-d8a3-4285-ace3-2bbfda2bd068" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/75d7d916-a419-4fc5-907a-6497de37ad7e" width="200"/></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/eb5f49e2-9edb-4e5a-8d58-68a1699ff66d" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/db73a1cc-9466-4d2e-8aba-2fe358e37eb3" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/5669a06f-032e-46fe-8a44-634865f0682c" width="200"/></td>
    <td><img src="https://github.com/user-attachments/assets/315bec36-efd0-4709-a6fa-cee9883229d1" width="200"/></td>
  </tr>
</table>

## Tech Stack

**[Kotlin](https://kotlinlang.org/)** - Kotlin is a programming language that can run on JVM. Google has announced Kotlin as one of its officially supported programming languages in Android Studio.

**[Jetpack Compose](https://developer.android.com/jetpack/compose)** - Modern declarative UI toolkit for building native Android UI.

**[Material Design 3](https://m3.material.io/)** - Latest Material Design system implementation for consistent and beautiful UI.

**[Hilt](https://dagger.dev/hilt/)** - A dependency injection library for Android that reduces the boilerplate of doing manual dependency injection in your project.

**[ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)** - The ViewModel class is designed to store and manage UI-related data in a lifecycle conscious way.

**[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)** - Handles in-app navigation with type-safe navigation between screens.

**[Room Database](https://developer.android.com/training/data-storage/room)** - The Room persistence library provides an abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite.

**[Retrofit](https://square.github.io/retrofit/)** - A type-safe HTTP client for Android and Java for API communication.

**[Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)** - A concurrency design pattern that you can use on Android to simplify code that executes asynchronously.

**[Lottie Animations](https://lottiefiles.com/android)** - Library for adding smooth animations and micro-interactions to enhance user experience.

**[Coil](https://coil-kt.github.io/coil/)** - Image loading library for Android backed by Kotlin Coroutines.

**[Gson](https://github.com/google/gson)** - A Java serialization/deserialization library to convert Java Objects into JSON and back.

**[DataStore](https://developer.android.com/topic/libraries/architecture/datastore)** - Modern preferences and settings management replacing SharedPreferences.

**[Open-Meteo Weather API](https://open-meteo.com/)** - Primary weather data source providing:
- Real-time weather conditions
- 16-day agricultural forecasts
- Hourly weather data including soil moisture, soil temperature
- Solar radiation, UV index, precipitation, wind data

**[Google Gemini API](https://ai.google.dev/)** - Powers the AI agriculture assistant for:
- Intelligent farm recommendations
- Weather-based farming advice and chat responses
- Natural language processing for agricultural queries

**[NewsAPI](https://newsapi.org/)** - Provides agricultural news and updates:
- Latest farming industry news
- Weather-related agricultural articles

## Setup Requirements
- Android device or emulator
- Android Studio
- JDK 17 or higher

## APIs Used

**Weather Data API:**
- **Open-Meteo Weather API** (https://open-meteo.com/)
    - Free weather service, no API key required
    - Provides comprehensive agricultural weather data
    - Endpoints: Current weather, hourly forecasts, daily forecasts
    - Parameters: soil moisture, soil temperature, solar radiation, precipitation

**AI Services:**
- **Google Gemini API** (https://aistudio.google.com/app/apikey/)
    - Requires API key from Google AI Studio
    - Used for intelligent weather based farm recommendations

**News Services:**
- **NewsAPI** (https://newsapi.org/)
    - Requires API key from NewsAPI platform
    - Provides agricultural related news

## Getting Started
In order to get the app running yourself, you need to:

1. Clone this project
2. Import the project into Android Studio
3. Create `local.properties` file in root directory and add your API keys:
   ```
   # API Keys - Add your actual API keys here
   WEATHER_API_KEY="your_weather_api_key"
   GEMINI_API_KEY="your_gemini_api_key"
   NEWS_API_KEY="your_news_api_key"
   ```
   Note: Visual Crossing for ApiKey and Open-Meteo Weather APIs
4. Connect your Android device with USB or start your emulator
5. After the project has finished setting up, click the run button

## Support
Found this project useful? Support by clicking the ⭐️ button on the upper right of this page.

Notice anything else missing? File an issue

Feel free to contribute in any way to the project from typos in docs to code review are all welcome.





## Contributing

We welcome contributions! Please follow these guidelines:


## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 Mkao

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

