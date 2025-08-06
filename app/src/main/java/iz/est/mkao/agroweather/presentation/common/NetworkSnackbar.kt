package iz.est.mkao.agroweather.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import iz.est.mkao.agroweather.R
import iz.est.mkao.agroweather.util.NetworkUtils

/**
 * Network connectivity snackbar
 */
@Composable
fun NetworkSnackbar(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true
) {
    val context = LocalContext.current
    val isNetworkAvailable = NetworkUtils.isNetworkAvailable(context)
    
    if (isVisible && !isNetworkAvailable) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.error,
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = stringResource(R.string.network_error),
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.network_error),
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Check network connectivity and return status message
 */
@Composable
fun getNetworkStatusMessage(): String? {
    val context = LocalContext.current
    return if (!NetworkUtils.isNetworkAvailable(context)) {
        stringResource(R.string.network_error)
    } else {
        null
    }
}
