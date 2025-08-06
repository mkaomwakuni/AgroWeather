package iz.est.mkao.agroweather.presentation.weather.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import iz.est.mkao.agroweather.R
import iz.est.mkao.agroweather.presentation.common.LottieLoadingAnimation

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        LottieLoadingAnimation(
            animationRes = R.raw.weather_loading,
            size = 150.dp
        )
    }
}
