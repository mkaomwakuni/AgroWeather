package iz.est.mkao.agroweather.presentation.common

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition

/**
 * A reusable Lottie animation component for loading states
 * 
 * @param animationRes The resource ID of the Lottie animation file
 * @param modifier Modifier to be applied to the animation
 * @param size The size of the animation in dp
 * @param iterations Number of iterations to play the animation, default is infinite
 */
@Composable
fun LottieLoadingAnimation(
    animationRes: Int,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    iterations: Int = LottieConstants.IterateForever
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(animationRes)
    )
    
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations
    )
    
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier.size(size)
    )
}
