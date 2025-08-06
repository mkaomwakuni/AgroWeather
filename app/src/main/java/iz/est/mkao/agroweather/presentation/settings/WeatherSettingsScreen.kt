package iz.est.mkao.agroweather.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import iz.est.mkao.agroweather.data.model.City
import iz.est.mkao.agroweather.data.repository.CityRepository

data class SettingsItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val hasChevron: Boolean = true,
    val onClick: () -> Unit = {},
)

data class SettingsSection(
    val title: String,
    val items: List<SettingsItem>,
)



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherSettingsScreen(
    navController: NavController = rememberNavController(),
    viewModel: WeatherSettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val settingsSections = listOf(
        SettingsSection(
            title = "App Preferences",
            items = listOf(
                SettingsItem(
                    title = "Temperature Unit",
                    subtitle = if (uiState.isCelsius) "Celsius (°C)" else "Fahrenheit (°F)",
                    icon = Icons.Default.Thermostat,
                    onClick = { viewModel.updateTemperatureUnit(!uiState.isCelsius) },
                ),
                SettingsItem(
                    title = "Location",
                    subtitle = if (uiState.useCurrentLocation) "Use current location" else uiState.defaultLocation,
                    icon = Icons.Default.LocationOn,
                    onClick = { viewModel.showLocationDialog() },
                ),
                SettingsItem(
                    title = "Language",
                    subtitle = uiState.availableLanguages.find { it.code == uiState.language }?.name ?: "English",
                    icon = Icons.Default.Language,
                    onClick = { viewModel.showLanguageDialog() },
                ),
                SettingsItem(
                    title = "Theme",
                    subtitle = if (uiState.isDarkTheme) "Dark mode" else "Light mode",
                    icon = Icons.Default.LightMode,
                    onClick = { viewModel.updateDarkTheme(!uiState.isDarkTheme) },
                ),
            ),
        ),
        SettingsSection(
            title = "Irrigation Notifications",
            items = listOf(
                SettingsItem(
                    title = "Morning/Evening Alerts",
                    subtitle = if (uiState.irrigationNotificationsEnabled) "Enabled" else "Disabled",
                    icon = Icons.Default.Notifications,
                    onClick = { viewModel.updateIrrigationNotifications(!uiState.irrigationNotificationsEnabled) },
                ),
                SettingsItem(
                    title = "Urgent Irrigation Alerts",
                    subtitle = if (uiState.urgentIrrigationAlertsEnabled) "Enabled" else "Disabled",
                    icon = Icons.Default.NotificationImportant,
                    onClick = { viewModel.updateUrgentIrrigationAlerts(!uiState.urgentIrrigationAlertsEnabled) },
                ),
                SettingsItem(
                    title = "Daily Summary",
                    subtitle = if (uiState.dailyIrrigationSummaryEnabled) "Enabled" else "Disabled",
                    icon = Icons.Default.Summarize,
                    onClick = { viewModel.updateDailyIrrigationSummary(!uiState.dailyIrrigationSummaryEnabled) },
                ),
            ),
        ),
        SettingsSection(
            title = "Support and About",
            items = listOf(
                SettingsItem(
                    title = "FAQs",
                    subtitle = "Frequently asked questions",
                    icon = Icons.Default.Help,
                    onClick = { viewModel.showFAQsDialog() },
                ),
                SettingsItem(
                    title = "Contact Support",
                    subtitle = "Contact support via email or chat",
                    icon = Icons.Default.Headset,
                ),
                SettingsItem(
                    title = "About the App",
                    subtitle = "View app version and developer info",
                    icon = Icons.Default.Info,
                    onClick = { viewModel.showAboutDialog() },
                ),
            ),
        ),
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(settingsSections) { section ->
                SettingsSectionComposable(section = section)
            }

            // Save button
            item {
                Button(
                    onClick = { viewModel.saveSettings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                ) {
                    Text("Save Settings")
                }
            }

            // Add bottom padding for content visibility
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Language selection dialog
        if (uiState.showLanguageDialog) {
            LanguageSelectionDialog(
                languages = uiState.availableLanguages,
                selectedLanguage = uiState.language,
                onLanguageSelected = { language ->
                    viewModel.updateLanguage(language)
                    viewModel.hideLanguageDialog()
                },
                onDismiss = { viewModel.hideLanguageDialog() },
            )
        }

        // Location selection dialog
        if (uiState.showLocationDialog) {
            LocationSelectionDialog(
                cities = uiState.availableCities,
                selectedLocation = uiState.defaultLocation,
                useCurrentLocation = uiState.useCurrentLocation,
                onLocationSelected = { city ->
                    viewModel.updateLocation(city.name, city.latitude, city.longitude)
                    viewModel.updateUseCurrentLocation(false)
                    viewModel.hideLocationDialog()
                },
                onCurrentLocationSelected = {
                    viewModel.updateUseCurrentLocation(true)
                    viewModel.hideLocationDialog()
                },
                onDismiss = { viewModel.hideLocationDialog() },
            )
        }

        // FAQs Dialog
        if (uiState.showFAQsDialog) {
            FAQsDialog(
                onDismiss = { viewModel.hideFAQsDialog() },
            )
        }

        // About Dialog
        if (uiState.showAboutDialog) {
            AboutDialog(
                onDismiss = { viewModel.hideAboutDialog() },
            )
        }

        // Save message
        uiState.saveMessage?.let { message ->
            LaunchedEffect(message) {
                kotlinx.coroutines.delay(2000)
                viewModel.clearSaveMessage()
            }
        }
    }
}

@Composable
fun SettingsSectionComposable(section: SettingsSection) {
    Column {
        Text(
            text = section.title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            section.items.forEach { item ->
                SettingsItemComposable(item = item)
            }
        }
    }
}

@Composable
fun SettingsItemComposable(item: SettingsItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(30.dp),
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text content
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp),
                )
                Text(
                    text = item.subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    lineHeight = 20.sp,
                )
            }

            // Chevron icon
            if (item.hasChevron) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    languages: List<Language>,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Select Language",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                ) {
                    items(languages) { language ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLanguageSelected(language.code) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = language.code == selectedLanguage,
                                onClick = { onLanguageSelected(language.code) },
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = language.name,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun LocationSelectionDialog(
    cities: List<City>,
    selectedLocation: String,
    useCurrentLocation: Boolean,
    onLocationSelected: (City) -> Unit,
    onCurrentLocationSelected: () -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Select Location",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                ) {
                    // Current location option
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onCurrentLocationSelected() }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = useCurrentLocation,
                                onClick = { onCurrentLocationSelected() },
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Use Current Location",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                )
                            }
                        }
                    }

                    // Divider
                    item {
                        Divider(
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }

                    // City list
                    items(cities) { city ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onLocationSelected(city) }
                                .padding(vertical = 12.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = !useCurrentLocation && city.name == selectedLocation,
                                onClick = { onLocationSelected(city) },
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = city.name,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun FAQsDialog(
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Frequently Asked Questions",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    items(faqList) { faq ->
                        FAQItem(faq)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun FAQItem(faq: FAQ) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun AboutDialog(
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = Icons.Default.Agriculture,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Farm Weather",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = "Version 1.0.0",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                Text(
                    text = "Smart agricultural weather forecasting app with AI-powered farming suggestions.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Developer: @Mkaocodes",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "© 2025",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { /* Open GitHub link */ }
                        .padding(bottom = 16.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "github.com/mkaomwakuni",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                TextButton(onClick = onDismiss) {
                    Text("Close")
                }
            }
        }
    }
}

data class FAQ(
    val question: String,
    val answer: String,
)

private val faqList = listOf(
    FAQ(
        "How accurate are the weather forecasts?",
        "Our weather data comes from reliable meteorological services and is updated regularly. Accuracy is generally very high for short-term forecasts (1-3 days) and good for medium-term forecasts (4-7 days).",
    ),
    FAQ(
        "How does the AI provide farming suggestions?",
        "Our AI analyzes current weather conditions, soil data, and agricultural best practices to provide personalized recommendations for your crops and livestock.",
    ),
    FAQ(
        "Can I use the app offline?",
        "The app requires an internet connection to fetch the latest weather data and AI suggestions. However, recently viewed data is cached for limited offline viewing.",
    ),
    FAQ(
        "How do I change my location?",
        "Go to Settings > Location and either use your current location or select from our list of supported cities and towns.",
    ),
    FAQ(
        "What crops and livestock are supported?",
        "The app provides suggestions for a wide range of crops including cereals, vegetables, fruits, and cash crops, as well as livestock including cattle, poultry, and goats.",
    ),
    FAQ(
        "How often is the weather data updated?",
        "Weather data is updated every 6 hours to ensure you have the most current information for your farming decisions.",
    ),
    FAQ(
        "Can I get notifications for weather alerts?",
        "Currently, the app provides real-time weather information. Push notifications for severe weather alerts are planned for future updates.",
    ),
    FAQ(
        "Is my data secure?",
        "Yes, we take data privacy seriously. Location data is only used to provide weather information and is not shared with third parties.",
    ),
)

@Preview(showBackground = true)
@Composable
fun WeatherSettingsScreenPreview() {
    MaterialTheme {
        WeatherSettingsScreen()
    }
}
