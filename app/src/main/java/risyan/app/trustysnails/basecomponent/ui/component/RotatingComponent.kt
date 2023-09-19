package risyan.app.trustysnails.basecomponent.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import kotlinx.coroutines.delay


@Composable
fun RotatingComponent(
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {

    var currentRotation by remember { mutableStateOf(160F) }
    LaunchedEffect(currentRotation){
        if(currentRotation <= 0f) {
            currentRotation = 0f
            return@LaunchedEffect
        }
        currentRotation -= 2f
    }

    Box(
        modifier = modifier
            .rotate(currentRotation),
        contentAlignment = Alignment.Center
    ) {
        content()
    }

    LaunchedEffect(key1 = true){
        currentRotation -= 2f
    }
}

@Composable
fun SlideFromBottomContainer(
    content: @Composable () -> Unit,
    initialOffset : Int = 100,
    modifier: Modifier = Modifier
){
    var startVisible by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = startVisible,
        enter = slideInVertically(initialOffsetY = { initialOffset },
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)),
        exit = slideOutVertically(targetOffsetY = { -initialOffset },
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)),
        modifier = modifier
    ){
        content()
    }

    LaunchedEffect(true){
        delay(100)
        startVisible = true
    }
}

@Composable
fun SlideFromEndContainer(
    content: @Composable () -> Unit,
    initialOffset : Int = 100,
    modifier: Modifier = Modifier
){
    var startVisible by remember { mutableStateOf(false) }

    AnimatedVisibility(visible = startVisible,
        enter = slideInHorizontally(initialOffsetX = { -initialOffset },
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)),
        exit = slideOutHorizontally(targetOffsetX = { initialOffset },
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)),
        modifier = modifier
    ){
        content()
    }

    LaunchedEffect(true){
        delay(100)
        startVisible = true
    }
}