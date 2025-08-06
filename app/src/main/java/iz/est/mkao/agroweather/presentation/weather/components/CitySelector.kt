package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.data.model.City

@Composable
fun CitySelector(
    selectedCity: City?,
    availableCities: List<City>,
    showCityPicker: Boolean,
    isLoadingCities: Boolean,
    onShowCityPicker: (Boolean) -> Unit,
    onCitySelected: (City) -> Unit,
    onSearchCities: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onShowCityPicker(true) },
            ) {
                Text(
                    text = selectedCity?.name ?: "Loading...",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                )

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select City",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp),
                )
            }

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(Color.White, RoundedCornerShape(50)),
                    )
                }
            }
        }

        IconButton(
            onClick = { /* Use current location */ },
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Current Location",
                tint = Color.White,
                modifier = Modifier.size(32.dp),
            )
        }
    }
}
