package iz.est.mkao.agroweather.presentation.radar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
// We're no longer using OSMDroid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherRadarScreen(
    navController: NavController = rememberNavController(),
    viewModel: WeatherRadarViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    val radarTextColor = if (isDarkTheme) Color.White else Color.Black

    var selectedLayer by remember { mutableStateOf(RadarLayer.PRECIPITATION) }
    var isAnimating by remember { mutableStateOf(false) }
    var animationFrame by remember { mutableStateOf(0) }

    // Animation effect
    LaunchedEffect(isAnimating) {
        if (isAnimating) {
            while (isAnimating) {
                delay(500)
                animationFrame = (animationFrame + 1) % 8
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        // Map View (full screen)
        WeatherRadarMapView(
            modifier = Modifier.fillMaxSize(),
            centerLocation = uiState.currentLocation,
            selectedLayer = selectedLayer,
            isAnimating = isAnimating,
            animationFrame = animationFrame,
            weatherStations = uiState.weatherStations,
        )

        // Floating Top Bar
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 48.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = radarTextColor,
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Precipitation Radar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = radarTextColor,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = if (uiState.isLoading) "Loading..." else "Live",
                        fontSize = 12.sp,
                        color = if (uiState.isLoading) {
                            radarTextColor.copy(alpha = 0.7f)
                        } else {
                            Color(0xFF4CAF50)
                        },
                        textAlign = TextAlign.Center,
                    )
                }

                // Refresh button
                IconButton(onClick = { viewModel.refreshRadarData() }) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = radarTextColor,
                    )
                }
            }
        }

        // Floating Bottom Controls
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.3f),
            ),
        ) {
            RadarControlsBottomBar(
                isAnimating = isAnimating,
                onAnimationToggle = { isAnimating = it },
                onLocationClick = { viewModel.goToCurrentLocation() },
            )
        }

        // Loading indicator
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center,
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.7f),
                    ),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator(color = Color.White)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading radar data...", color = Color.White)
                    }
                }
            }
        }

        // Error message
        uiState.error?.let { error ->
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.9f),
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = null,
                        tint = Color.White,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { viewModel.clearError() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White,
                        )
                    }
                }
            }
        }

        // Radar legend (small and transparent at bottom start)
        RadarLegend(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 140.dp),
            textColor = radarTextColor,
        )
    }
}

@Composable
fun WeatherRadarMapView(
    modifier: Modifier = Modifier,
    centerLocation: GeoPoint?,
    selectedLayer: RadarLayer,
    isAnimating: Boolean,
    animationFrame: Int,
    weatherStations: List<WeatherStation>,
) {
    val context = LocalContext.current
    val isDarkTheme = androidx.compose.foundation.isSystemInDarkTheme()
    
    // Create a simplified Canvas-based radar display
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Draw simplified radar circles using Canvas
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxRadius = size.width.coerceAtMost(size.height) / 2 * 0.9f
            
            // Draw grid lines
            val gridPaint = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1f
            )
            
            // Draw circular grid lines
            for (i in 1..3) {
                val radius = maxRadius * (i / 3f)
                drawCircle(
                    color = Color.Gray.copy(alpha = 0.3f),
                    radius = radius,
                    center = androidx.compose.ui.geometry.Offset(centerX, centerY),
                    style = gridPaint
                )
            }
            
            // Draw radar "sweeping" indicator
            if (isAnimating) {
                val sweepAngle = (animationFrame % 8) * 45f
                drawArc(
                    color = Color.Green.copy(alpha = 0.5f),
                    startAngle = 0f,
                    sweepAngle = sweepAngle,
                    useCenter = true,
                    topLeft = androidx.compose.ui.geometry.Offset(centerX - maxRadius, centerY - maxRadius),
                    size = androidx.compose.ui.geometry.Size(maxRadius * 2, maxRadius * 2)
                )
            }
            
            // Draw simulated precipitation based on the selected layer
            when (selectedLayer) {
                RadarLayer.PRECIPITATION -> {
                    // Simulate rainfall patterns with multiple circles
                    val rainIntensities = listOf(
                        Color(0x4081C784), // Light rain, more transparent
                        Color(0x60FFB74D), // Moderate rain
                        Color(0x80E57373), // Heavy rain
                        Color(0x409C27B0)  // Severe rain, outer edge
                    )
                    
                    // Calculate pattern offset based on animation frame
                    val offsetX = if (isAnimating) {
                        (animationFrame % 8) * 10f - 35f
                    } else {
                        0f
                    }
                    
                    // Draw precipitation patterns
                    for (i in rainIntensities.indices) {
                        val radius = maxRadius * (0.8f - (i * 0.15f))
                        val offsetY = -20f + (i * 10f)
                        
                        drawCircle(
                            color = rainIntensities[i],
                            radius = radius,
                            center = androidx.compose.ui.geometry.Offset(centerX + offsetX, centerY + offsetY)
                        )
                    }
                }
            }
            
            // Draw weather station indicators
            weatherStations.forEach { station ->
                // This is a very simplified positioning - in a real app we would
                // properly convert lat/long to x/y coordinates based on the center point
                
                // For demo, just distribute stations randomly on the radar
                val angle = station.name.hashCode() % 360
                val distance = (station.name.length % 3 + 1) / 4f
                
                val stationX = centerX + maxRadius * distance * kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat()
                val stationY = centerY + maxRadius * distance * kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat()
                
                // Draw station marker
                drawCircle(
                    color = Color.Red,
                    radius = 8f,
                    center = androidx.compose.ui.geometry.Offset(stationX, stationY)
                )
                
                // Draw station temp indicator
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = androidx.compose.ui.geometry.Offset(stationX, stationY)
                )
            }
        }
        
        // Draw center location indicator
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(Color.Blue, androidx.compose.foundation.shape.CircleShape)
                .align(Alignment.Center)
        )
        
        // Display location coordinates
        Text(
            text = centerLocation?.let { "Lat: ${it.latitude.format(4)}, Lon: ${it.longitude.format(4)}" }
                ?: "No location selected",
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                    RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

// Helper extension to format Double to specified decimal places
private fun Double.format(digits: Int) = "%.${digits}f".format(this)

@Composable
fun RadarControlsBottomBar(
    isAnimating: Boolean,
    onAnimationToggle: (Boolean) -> Unit,
    onLocationClick: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Animation toggle button
        Button(
            onClick = { onAnimationToggle(!isAnimating) },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAnimating) Color.Red.copy(alpha = 0.8f) else Color.Green.copy(alpha = 0.8f)
            )
        ) {
            Icon(
                imageVector = if (isAnimating) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isAnimating) "Pause" else "Animate",
                color = Color.White
            )
        }
        
        // Location button
        Button(
            onClick = onLocationClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Blue.copy(alpha = 0.8f)
            )
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "My Location",
                color = Color.White
            )
        }
    }
}

@Composable
fun RadarLegend(
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Box(
        modifier = modifier
            .background(
                Color.Transparent,
                RoundedCornerShape(8.dp),
            ),
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
        ) {
            Text(
                text = "Precipitation",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = textColor,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(4.dp))

            RadarLayer.PRECIPITATION.legendItems.take(3).forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 1.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(item.color, RoundedCornerShape(1.dp)),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.label,
                        fontSize = 8.sp,
                        color = textColor,
                    )
                }
            }
        }
    }
}

// Note: We no longer need the createDemoOverlay function since we're using Canvas directly
// in the WeatherRadarMapView composable for better compatibility

// Data classes and enums
enum class RadarLayer(
    val displayName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color,
    val legendItems: List<LegendItem>,
) {
    PRECIPITATION(
        "Precipitation",
        Icons.Default.Grain,
        Color(0xFF2196F3),
        listOf(
            LegendItem("Light", Color(0xFF81C784)),
            LegendItem("Moderate", Color(0xFFFFB74D)),
            LegendItem("Heavy", Color(0xFFE57373)),
            LegendItem("Severe", Color(0xFF9C27B0)),
        ),
    ),
}

data class LegendItem(
    val label: String,
    val color: Color,
)

data class WeatherStation(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val temperature: Double,
    val conditions: String,
)
