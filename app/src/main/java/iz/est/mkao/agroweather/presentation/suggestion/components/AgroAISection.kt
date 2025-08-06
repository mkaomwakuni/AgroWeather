package iz.est.mkao.agroweather.presentation.suggestion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun AgroAISection(
    questionText: String,
    onQuestionChange: (String) -> Unit,
    onSendQuestion: (String) -> Unit,
    onQuickQuestionClick: (String) -> Unit,
    weatherData: iz.est.mkao.farmweather.data.model.WeatherResponse?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        // Section header with AI branding
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "AI Assistant",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Agro AI Assistant",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    text = "Get expert farming advice instantly",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
        }

        // Smart question suggestions based on current weather
        val smartSuggestions = generateSmartSuggestions(weatherData)
        if (smartSuggestions.isNotEmpty()) {
            Text(
                text = "Quick Questions",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                modifier = Modifier.padding(bottom = 8.dp),
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp),
            ) {
                items(smartSuggestions) { suggestion ->
                    SuggestionChip(
                        text = suggestion.text,
                        icon = suggestion.icon,
                        onClick = { onQuickQuestionClick(suggestion.fullQuestion) },
                    )
                }
            }
        }

        // Main input field
        OutlinedTextField(
            value = questionText,
            onValueChange = onQuestionChange,
            placeholder = {
                Text(
                    text = "Ask about crop management, weather impacts, pest control...",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Chat",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = { onSendQuestion(questionText) },
                    enabled = questionText.isNotBlank(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send question",
                        tint = if (questionText.isNotBlank()) 
                            MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            minLines = 2,
        )

        // Popular farming topics
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Popular Topics",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(popularTopics) { topic ->
                TopicChip(
                    text = topic.text,
                    icon = topic.icon,
                    onClick = { onQuickQuestionClick(topic.question) },
                )
            }
        }
    }
}

@Composable
fun SuggestionChip(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
fun TopicChip(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

data class QuestionSuggestion(
    val text: String,
    val icon: ImageVector,
    val fullQuestion: String,
)

data class PopularTopic(
    val text: String,
    val icon: ImageVector,
    val question: String,
)

fun generateSmartSuggestions(weatherData: iz.est.mkao.farmweather.data.model.WeatherResponse?): List<QuestionSuggestion> {
    val suggestions = mutableListOf<QuestionSuggestion>()
    
    weatherData?.currentConditions?.let { conditions ->
        // Temperature-based suggestions
        when {
            conditions.temp > 30 -> {
                suggestions.add(
                    QuestionSuggestion(
                        text = "Heat Protection",
                        icon = Icons.Default.Thermostat,
                        fullQuestion = "How can I protect my crops from the current high temperature of ${conditions.temp.roundToInt()}°C?"
                    )
                )
            }
            conditions.temp < 10 -> {
                suggestions.add(
                    QuestionSuggestion(
                        text = "Cold Weather",
                        icon = Icons.Default.AcUnit,
                        fullQuestion = "What should I do to protect my crops from the cold weather (${conditions.temp.roundToInt()}°C)?"
                    )
                )
            }
        }
        
        // Rain-based suggestions
        if (conditions.precipprob > 50) {
            suggestions.add(
                QuestionSuggestion(
                    text = "Rain Expected",
                    icon = Icons.Default.Grain,
                    fullQuestion = "There's a ${conditions.precipprob.roundToInt()}% chance of rain today. Should I adjust my irrigation schedule?"
                )
            )
        }
        
        // UV/Sun suggestions
        if (conditions.uvindex > 7) {
            suggestions.add(
                QuestionSuggestion(
                    text = "High UV",
                    icon = Icons.Default.WbSunny,
                    fullQuestion = "UV index is ${conditions.uvindex.roundToInt()} today. How can I protect my crops from sun damage?"
                )
            )
        }
        
        // Wind suggestions
        if (conditions.windspeed > 20) {
            suggestions.add(
                QuestionSuggestion(
                    text = "Windy Day",
                    icon = Icons.Default.Air,
                    fullQuestion = "Wind speed is ${conditions.windspeed.roundToInt()} km/h. What precautions should I take for my crops?"
                )
            )
        }
    }
    
    return suggestions.take(3)
}

val popularTopics = listOf(
    PopularTopic(
        text = "Fertilizer Types",
        icon = Icons.Default.Grass,
        question = "What types of fertilizers should I use for my crops based on current soil conditions and weather?"
    ),
    PopularTopic(
        text = "Nutrient Management",
        icon = Icons.Default.Science,
        question = "How can I optimize nutrient management for maximum crop yield and soil health?"
    ),
    PopularTopic(
        text = "Organic Fertilizers",
        icon = Icons.Default.Eco,
        question = "What are the best organic fertilizer options and application methods for sustainable farming?"
    ),
    PopularTopic(
        text = "Soil Testing",
        icon = Icons.Default.Analytics,
        question = "How often should I test my soil and what nutrients should I monitor for optimal crop growth?"
    ),
    PopularTopic(
        text = "Composting",
        icon = Icons.Default.Recycling,
        question = "How can I create and use compost effectively to improve soil fertility and reduce costs?"
    ),
    PopularTopic(
        text = "Crop Rotation",
        icon = Icons.Default.Sync,
        question = "What crop rotation practices will help maintain soil health and prevent nutrient depletion?"
    ),
    PopularTopic(
        text = "Cover Crops",
        icon = Icons.Default.Nature,
        question = "Which cover crops should I plant to improve soil structure and add natural nutrients?"
    ),
    PopularTopic(
        text = "Foliar Feeding",
        icon = Icons.Default.Shower,
        question = "When and how should I apply foliar fertilizers for quick nutrient uptake?"
    ),
)
