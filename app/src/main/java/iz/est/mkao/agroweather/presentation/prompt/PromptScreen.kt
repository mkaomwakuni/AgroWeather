package iz.est.mkao.agroweather.presentation.prompt

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import iz.est.mkao.agroweather.data.model.FarmingSuggestions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgricultureActivitiesScreen(
    navController: NavController = rememberNavController(),
    cityName: String = "",
    latitude: Double = 38.9697,
    longitude: Double = -77.385,
) {
    val selectedSuggestions = remember { mutableStateListOf<String>() }
    val locationDisplay = cityName.ifEmpty { "Current Location" }
    val maxSelections = 10
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // State for collapsible sections
    var weatherSectionExpanded by remember { mutableStateOf(true) }
    var farmProductsExpanded by remember { mutableStateOf(true) }
    var onFarmExpanded by remember { mutableStateOf(true) }

    // Weather-based suggestions
    val weatherSuggestions = FarmingSuggestions.weatherBasedSuggestions

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    LinearProgressIndicator(
                        progress = selectedSuggestions.size.toFloat() / maxSelections,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Navigate directly to chat screen
                            navController.navigate(
                                "chat?suggestion=Hello AI assistant, I need help with my farming activities&cityName=$cityName&latitude=$latitude&longitude=$longitude",
                            )
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Open AI Chat",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        floatingActionButton = {
            if (selectedSuggestions.isNotEmpty()) {
                FloatingActionButton(
                    onClick = {
                        // Navigate to chat with all selected suggestions
                        val combinedSuggestions = selectedSuggestions.joinToString(", ")
                        navController.navigate(
                            "chat?suggestion=${java.net.URLEncoder.encode("Provide advice for: $combinedSuggestions with current weather conditions", "UTF-8")}&cityName=${java.net.URLEncoder.encode(cityName, "UTF-8")}&latitude=$latitude&longitude=$longitude",
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Continue with selections",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Selection Count with Location info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "My selection ${selectedSuggestions.size}/$maxSelections",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Location",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                    Text(
                        text = locationDisplay,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Selected items display
            if (selectedSuggestions.isNotEmpty()) {
                Text(
                    text = "Selected: ${selectedSuggestions.joinToString(", ")}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 8.dp),
                )
            }

            // Weather-based suggestions section
            Spacer(modifier = Modifier.height(24.dp))

            CollapsibleSection(
                title = "Weather-Based Suggestions",
                icon = Icons.Default.WbSunny,
                iconColor = MaterialTheme.colorScheme.tertiary,
                isExpanded = weatherSectionExpanded,
                onToggle = { weatherSectionExpanded = !weatherSectionExpanded },
                borderColor = MaterialTheme.colorScheme.tertiaryContainer,
            ) {
                // Weather suggestions
                weatherSuggestions.chunked(2).forEach { rowSuggestions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        rowSuggestions.forEach { suggestion ->
                            ActivityChip(
                                text = suggestion,
                                selected = suggestion in selectedSuggestions,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        if (suggestion in selectedSuggestions) {
                                            selectedSuggestions -= suggestion
                                        } else if (selectedSuggestions.size < maxSelections) {
                                            selectedSuggestions += suggestion
                                        }
                                    },
                            )
                        }

                        if (rowSuggestions.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(
                    text = "See more",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .clickable {
                            // Navigate to chat with weather suggestion
                            navController.navigate(
                                "chat?suggestion=${java.net.URLEncoder.encode("Show me more weather-based farming suggestions", "UTF-8")}&cityName=${java.net.URLEncoder.encode(cityName, "UTF-8")}&latitude=$latitude&longitude=$longitude",
                            )
                        },
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            CollapsibleSection(
                title = "Farm Products",
                icon = Icons.Default.Settings,
                iconColor = MaterialTheme.colorScheme.primary,
                isExpanded = farmProductsExpanded,
                onToggle = { farmProductsExpanded = !farmProductsExpanded },
                borderColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
                // First row of options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ActivityChip(
                        text = "Organic Vegetables",
                        selected = "Organic Vegetables" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Organic Vegetables" in selectedSuggestions) {
                                    selectedSuggestions -= "Organic Vegetables"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Organic Vegetables"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Dairy",
                        selected = "Dairy" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Dairy" in selectedSuggestions) {
                                    selectedSuggestions -= "Dairy"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Dairy"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Harvesting",
                        selected = "Harvesting" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Harvesting" in selectedSuggestions) {
                                    selectedSuggestions -= "Harvesting"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Harvesting"
                                }
                            },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Second row of options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ActivityChip(
                        text = "Fruits and Berries",
                        selected = "Fruits and Berries" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable {
                                if ("Fruits and Berries" in selectedSuggestions) {
                                    selectedSuggestions -= "Fruits and Berries"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Fruits and Berries"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Grain Crops",
                        selected = "Grain Crops" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Grain Crops" in selectedSuggestions) {
                                    selectedSuggestions -= "Grain Crops"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Grain Crops"
                                }
                            },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ActivityChip(
                        text = "Herbs",
                        selected = "Herbs" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Herbs" in selectedSuggestions) {
                                    selectedSuggestions -= "Herbs"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Herbs"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Irrigation",
                        selected = "Irrigation" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Irrigation" in selectedSuggestions) {
                                    selectedSuggestions -= "Irrigation"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Irrigation"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Livestock Care",
                        selected = "Livestock Care" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable {
                                if ("Livestock Care" in selectedSuggestions) {
                                    selectedSuggestions -= "Livestock Care"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Livestock Care"
                                }
                            },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ActivityChip(
                        text = "Organic Farming",
                        selected = "Organic Farming" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable {
                                if ("Organic Farming" in selectedSuggestions) {
                                    selectedSuggestions -= "Organic Farming"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Organic Farming"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Beekeeping",
                        selected = "Beekeeping" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Beekeeping" in selectedSuggestions) {
                                    selectedSuggestions -= "Beekeeping"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Beekeeping"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Composting",
                        selected = "Composting" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable {
                                if ("Composting" in selectedSuggestions) {
                                    selectedSuggestions -= "Composting"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Composting"
                                }
                            },
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            CollapsibleSection(
                title = "On the Farm",
                icon = Icons.Default.LocationOn,
                iconColor = MaterialTheme.colorScheme.secondary,
                isExpanded = onFarmExpanded,
                onToggle = { onFarmExpanded = !onFarmExpanded },
                borderColor = MaterialTheme.colorScheme.secondaryContainer,
            ) {
                // First row of farm options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ActivityChip(
                        text = "Farm Tour",
                        selected = "Farm Tour" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Farm Tour" in selectedSuggestions) {
                                    selectedSuggestions -= "Farm Tour"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Farm Tour"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Farmers Market",
                        selected = "Farmers Market" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable {
                                if ("Farmers Market" in selectedSuggestions) {
                                    selectedSuggestions -= "Farmers Market"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Farmers Market"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Workshops",
                        selected = "Workshops" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Workshops" in selectedSuggestions) {
                                    selectedSuggestions -= "Workshops"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Workshops"
                                }
                            },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Second row of farm options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ActivityChip(
                        text = "Crop Farming",
                        selected = "Crop Farming" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable {
                                if ("Crop Farming" in selectedSuggestions) {
                                    selectedSuggestions -= "Crop Farming"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Crop Farming"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Hay Rides",
                        selected = "Hay Rides" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Hay Rides" in selectedSuggestions) {
                                    selectedSuggestions -= "Hay Rides"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Hay Rides"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Petting Zoo",
                        selected = "Petting Zoo" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1.3f)
                            .clickable {
                                if ("Petting Zoo" in selectedSuggestions) {
                                    selectedSuggestions -= "Petting Zoo"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Petting Zoo"
                                }
                            },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Third row of farm options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ActivityChip(
                        text = "Tractor Rides",
                        selected = "Tractor Rides" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1.2f)
                            .clickable {
                                if ("Tractor Rides" in selectedSuggestions) {
                                    selectedSuggestions -= "Tractor Rides"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Tractor Rides"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Planting",
                        selected = "Planting" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Planting" in selectedSuggestions) {
                                    selectedSuggestions -= "Planting"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Planting"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Farm Equipment",
                        selected = "Farm Equipment" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1.5f)
                            .clickable {
                                if ("Farm Equipment" in selectedSuggestions) {
                                    selectedSuggestions -= "Farm Equipment"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Farm Equipment"
                                }
                            },
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Fourth row of farm options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ActivityChip(
                        text = "Irrigation",
                        selected = "Irrigation" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Irrigation" in selectedSuggestions) {
                                    selectedSuggestions -= "Irrigation"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Irrigation"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Orchard",
                        selected = "Orchard" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Orchard" in selectedSuggestions) {
                                    selectedSuggestions -= "Orchard"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Orchard"
                                }
                            },
                    )
                    ActivityChip(
                        text = "Corn Maze",
                        selected = "Corn Maze" in selectedSuggestions,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                if ("Corn Maze" in selectedSuggestions) {
                                    selectedSuggestions -= "Corn Maze"
                                } else if (selectedSuggestions.size < maxSelections) {
                                    selectedSuggestions += "Corn Maze"
                                }
                            },
                    )
                }
            }
        }
    }
}

@Composable
fun CollapsibleSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    borderColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val rotationDegrees by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(dampingRatio = 0.8f),
        label = "arrow_rotation",
    )

    val cardColor by animateColorAsState(
        targetValue = if (isExpanded) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300),
        label = "card_color",
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isExpanded) 8.dp else 2.dp,
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp),
            ),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle() },
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = iconColor,
                )
                Text(
                    text = " $title",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationDegrees),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(dampingRatio = 0.8f),
                ) + fadeIn(),
                exit = shrinkVertically(
                    animationSpec = spring(dampingRatio = 0.8f),
                ) + fadeOut(),
            ) {
                Column(
                    modifier = Modifier.padding(top = 16.dp),
                ) {
                    content()
                }
            }
        }
    }
}

@Composable
fun ActivityChip(
    text: String,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(backgroundColor)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor,
            maxLines = 1,
        )
    }
}

