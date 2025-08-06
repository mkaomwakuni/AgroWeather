package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import iz.est.mkao.agroweather.data.model.City

@Composable
fun CityPickerDialog(
    availableCities: List<City>,
    isLoadingCities: Boolean,
    onCitySelected: (City) -> Unit,
    onDismiss: () -> Unit,
    onSearchCities: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = "Select City",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                // Search field
                TextField(
                    value = searchQuery,
                    onValueChange = { query: String ->
                        searchQuery = query
                        onSearchCities(query)
                    },
                    placeholder = { Text("Search cities...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                )

                if (isLoadingCities) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(availableCities) { city ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCitySelected(city) },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFF8F9FA),
                                ),
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                ) {
                                    Text(
                                        text = city.name,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF111827),
                                    )
                                    if (city.region.isNotEmpty()) {
                                        Text(
                                            text = city.region,
                                            fontSize = 14.sp,
                                            color = Color(0xFF6B7280),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
