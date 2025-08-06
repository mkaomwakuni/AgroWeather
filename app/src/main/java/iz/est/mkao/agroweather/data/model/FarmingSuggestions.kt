package iz.est.mkao.agroweather.data.model

/**
 * Comprehensive farming suggestions organized by categories
 * Used in PromptScreen for AI assistance options
 */
object FarmingSuggestions {

    /**
     * Weather-based AI suggestions for smart farming decisions
     */
    val weatherBasedSuggestions = listOf(
        "How will today's weather affect my crops?",
        "What crops are best for current soil conditions?",
        "Should I irrigate today based on weather forecast?",
        "Best planting time with current weather trends?",
        "Weather-based pest control recommendations",
        "Will tomorrow's rain affect my harvest schedule?",
        "How can I protect crops from upcoming frost?",
        "Best fertilization timing with current humidity levels?",
        "Should I delay pesticide application due to wind conditions?",
        "How does soil temperature affect seed germination today?",
    )

    /**
     * Farm product and crop management suggestions
     */
    val farmProductSuggestions = listOf(
        "Organic Vegetables",
        "Dairy Management",
        "Harvesting Techniques",
        "Fruits and Berries",
        "Grain Crops",
        "Herbs and Spices",
        "Irrigation Systems",
        "Livestock Care",
        "Organic Farming Methods",
        "Beekeeping",
        "Composting",
        "Greenhouse Management",
        "Crop Rotation Planning",
        "Soil Health Improvement",
        "Seed Selection",
        "Post-Harvest Storage",
    )

    /**
     * On-farm activities and operations suggestions
     */
    val onFarmActivitySuggestions = listOf(
        "Farm Tour Planning",
        "Farmers Market Setup",
        "Agricultural Workshops",
        "Crop Farming Techniques",
        "Equipment Maintenance",
        "Animal Husbandry",
        "Tractor Operations",
        "Planting Schedules",
        "Farm Equipment Selection",
        "Water Management",
        "Orchard Management",
        "Field Preparation",
        "Pest Monitoring",
        "Yield Optimization",
        "Farm Safety Protocols",
        "Sustainable Practices",
    )

    /**
     * Quick AI prompts for immediate assistance
     */
    val quickAiPrompts = listOf(
        "What should I prioritize on my farm today?",
        "Help me create a weekly farming schedule",
        "Analyze my crop health based on current conditions",
        "Suggest improvements for my farming operations",
        "What are the best practices for my climate zone?",
        "How can I increase my farm productivity sustainably?",
        "What equipment should I invest in next?",
        "Help me plan for the upcoming growing season",
    )

    /**
     * Get weather-appropriate suggestions based on conditions
     */
    fun getWeatherSpecificSuggestions(temperature: Double?, humidity: Double?, precipitation: Double?): List<String> {
        val suggestions = mutableListOf<String>()

        temperature?.let { temp ->
            when {
                temp < 5 -> suggestions.add("How to protect crops from frost damage?")
                temp > 35 -> suggestions.add("Heat stress management for livestock and crops")
                temp in 20.0..25.0 -> suggestions.add("Optimal planting activities for current temperature")
                else -> suggestions.add("General temperature management for crops")
            }
        }

        humidity?.let { hum ->
            when {
                hum > 80 -> suggestions.add("Prevent fungal diseases in high humidity")
                hum < 30 -> suggestions.add("Irrigation strategies for dry conditions")
                else -> suggestions.add("Optimal humidity management practices")
            }
        }

        precipitation?.let { precip ->
            when {
                precip > 10 -> suggestions.add("Field drainage and waterlogging prevention")
                precip == 0.0 -> suggestions.add("Drought management techniques")
                else -> suggestions.add("Water management best practices")
            }
        }

        return suggestions.ifEmpty { weatherBasedSuggestions.take(3) }
    }
}
