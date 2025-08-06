package iz.est.mkao.agroweather.data.repository

import iz.est.mkao.agroweather.data.model.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for managing city data
 * In a real app, this could load from local database, remote API, or assets
 */
@Singleton
class CityRepository @Inject constructor() {

    /**
     * Get list of available cities
     * This could be loaded from database, API, or local assets
     */
    fun getCities(): Flow<List<City>> = flow {
        emit(getAllCities())
    }

    /**
     * Get default city
     */
    suspend fun getDefaultCity(): City {
        return getAllCities().first { it.isDefault }
    }

    /**
     * Find city by name
     */
    suspend fun getCityByName(name: String): City? {
        return getAllCities().find { it.name.equals(name, ignoreCase = true) }
    }

    /**
     * Find city by ID
     */
    suspend fun getCityById(id: String): City? {
        return getAllCities().find { it.id == id }
    }

    /**
     * Search cities by name
     */
    fun searchCities(query: String): Flow<List<City>> = flow {
        val cities = getAllCities()
        val filteredCities = if (query.isBlank()) {
            cities
        } else {
            cities.filter {
                it.name.contains(query, ignoreCase = true) ||
                    it.region.contains(query, ignoreCase = true)
            }
        }
        emit(filteredCities)
    }

    /**
     * Comprehensive list of major global cities and agricultural centers
     * In production, this could be loaded from assets, database, or API
     * Made public to avoid duplication across the app
     */
    fun getAllCities(): List<City> = listOf(
        // North America - Major Agricultural Centers
        City(
            id = "des_moines",
            name = "Des Moines",
            latitude = 41.5868,
            longitude = -93.6250,
            region = "Iowa, USA",
            isDefault = true,
        ),
        City(
            id = "omaha",
            name = "Omaha",
            latitude = 41.2565,
            longitude = -95.9345,
            region = "Nebraska, USA",
        ),
        City(
            id = "kansas_city",
            name = "Kansas City",
            latitude = 39.0997,
            longitude = -94.5786,
            region = "Missouri, USA",
        ),
        City(
            id = "fresno",
            name = "Fresno",
            latitude = 36.7378,
            longitude = -119.7871,
            region = "California, USA",
        ),
        City(
            id = "sacramento",
            name = "Sacramento",
            latitude = 38.5816,
            longitude = -121.4944,
            region = "California, USA",
        ),
        City(
            id = "winnipeg",
            name = "Winnipeg",
            latitude = 49.8951,
            longitude = -97.1384,
            region = "Manitoba, Canada",
        ),
        City(
            id = "calgary",
            name = "Calgary",
            latitude = 51.0447,
            longitude = -114.0719,
            region = "Alberta, Canada",
        ),
        
        // Europe - Agricultural Regions
        City(
            id = "toulouse",
            name = "Toulouse",
            latitude = 43.6047,
            longitude = 1.4442,
            region = "France",
        ),
        City(
            id = "stuttgart",
            name = "Stuttgart",
            latitude = 48.7758,
            longitude = 9.1829,
            region = "Germany",
        ),
        City(
            id = "valencia",
            name = "Valencia",
            latitude = 39.4699,
            longitude = -0.3763,
            region = "Spain",
        ),
        City(
            id = "modena",
            name = "Modena",
            latitude = 44.6471,
            longitude = 10.9252,
            region = "Italy",
        ),
        City(
            id = "amsterdam",
            name = "Amsterdam",
            latitude = 52.3676,
            longitude = 4.9041,
            region = "Netherlands",
        ),

        // Asia Pacific - Agricultural Powerhouses
        City(
            id = "bangalore",
            name = "Bangalore",
            latitude = 12.9716,
            longitude = 77.5946,
            region = "Karnataka, India",
        ),
        City(
            id = "punjab_ludhiana",
            name = "Ludhiana",
            latitude = 30.9010,
            longitude = 75.8573,
            region = "Punjab, India",
        ),
        City(
            id = "brisbane",
            name = "Brisbane",
            latitude = -27.4698,
            longitude = 153.0251,
            region = "Queensland, Australia",
        ),
        City(
            id = "adelaide",
            name = "Adelaide",
            latitude = -34.9285,
            longitude = 138.6007,
            region = "South Australia",
        ),
        City(
            id = "christchurch",
            name = "Christchurch",
            latitude = -43.5321,
            longitude = 172.6362,
            region = "Canterbury, New Zealand",
        ),
        City(
            id = "tokyo",
            name = "Tokyo",
            latitude = 35.6762,
            longitude = 139.6503,
            region = "Japan",
        ),
        City(
            id = "beijing",
            name = "Beijing",
            latitude = 39.9042,
            longitude = 116.4074,
            region = "China",
        ),

        // South America - Agricultural Centers
        City(
            id = "sao_paulo",
            name = "São Paulo",
            latitude = -23.5505,
            longitude = -46.6333,
            region = "Brazil",
        ),
        City(
            id = "buenos_aires",
            name = "Buenos Aires",
            latitude = -34.6118,
            longitude = -58.3960,
            region = "Argentina",
        ),
        City(
            id = "santiago",
            name = "Santiago",
            latitude = -33.4489,
            longitude = -70.6693,
            region = "Chile",
        ),
        City(
            id = "cordoba_arg",
            name = "Córdoba",
            latitude = -31.4201,
            longitude = -64.1888,
            region = "Argentina",
        ),
        City(
            id = "rosario",
            name = "Rosario",
            latitude = -32.9468,
            longitude = -60.6393,
            region = "Argentina",
        ),

        // Africa - Major Agricultural Centers
        City(
            id = "nairobi",
            name = "Nairobi",
            latitude = -1.2921,
            longitude = 36.8219,
            region = "Kenya",
        ),
        City(
            id = "cape_town",
            name = "Cape Town",
            latitude = -33.9249,
            longitude = 18.4241,
            region = "South Africa",
        ),
        City(
            id = "johannesburg",
            name = "Johannesburg",
            latitude = -26.2041,
            longitude = 28.0473,
            region = "South Africa",
        ),
        City(
            id = "cairo",
            name = "Cairo",
            latitude = 30.0444,
            longitude = 31.2357,
            region = "Egypt",
        ),
        City(
            id = "lagos",
            name = "Lagos",
            latitude = 6.5244,
            longitude = 3.3792,
            region = "Nigeria",
        ),
        City(
            id = "addis_ababa",
            name = "Addis Ababa",
            latitude = 9.1450,
            longitude = 40.4897,
            region = "Ethiopia",
        ),
        
        // Major Global Metropolitan Centers
        City(
            id = "london",
            name = "London",
            latitude = 51.5074,
            longitude = -0.1278,
            region = "United Kingdom",
        ),
        City(
            id = "paris",
            name = "Paris",
            latitude = 48.8566,
            longitude = 2.3522,
            region = "France",
        ),
        City(
            id = "berlin",
            name = "Berlin",
            latitude = 52.5200,
            longitude = 13.4050,
            region = "Germany",
        ),
        City(
            id = "rome",
            name = "Rome",
            latitude = 41.9028,
            longitude = 12.4964,
            region = "Italy",
        ),
        City(
            id = "madrid",
            name = "Madrid",
            latitude = 40.4168,
            longitude = -3.7038,
            region = "Spain",
        ),
        City(
            id = "new_york",
            name = "New York",
            latitude = 40.7128,
            longitude = -74.0060,
            region = "New York, USA",
        ),
        City(
            id = "los_angeles",
            name = "Los Angeles",
            latitude = 34.0522,
            longitude = -118.2437,
            region = "California, USA",
        ),
        City(
            id = "chicago",
            name = "Chicago",
            latitude = 41.8781,
            longitude = -87.6298,
            region = "Illinois, USA",
        ),
        City(
            id = "toronto",
            name = "Toronto",
            latitude = 43.6532,
            longitude = -79.3832,
            region = "Ontario, Canada",
        ),
        City(
            id = "vancouver",
            name = "Vancouver",
            latitude = 49.2827,
            longitude = -123.1207,
            region = "British Columbia, Canada",
        ),
        City(
            id = "sydney",
            name = "Sydney",
            latitude = -33.8688,
            longitude = 151.2093,
            region = "New South Wales, Australia",
        ),
        City(
            id = "melbourne",
            name = "Melbourne",
            latitude = -37.8136,
            longitude = 144.9631,
            region = "Victoria, Australia",
        ),
        City(
            id = "mumbai",
            name = "Mumbai",
            latitude = 19.0760,
            longitude = 72.8777,
            region = "Maharashtra, India",
        ),
        City(
            id = "delhi",
            name = "Delhi",
            latitude = 28.7041,
            longitude = 77.1025,
            region = "India",
        ),
        City(
            id = "singapore",
            name = "Singapore",
            latitude = 1.3521,
            longitude = 103.8198,
            region = "Singapore",
        ),
        City(
            id = "dubai",
            name = "Dubai",
            latitude = 25.2048,
            longitude = 55.2708,
            region = "UAE",
        ),
    )
}
