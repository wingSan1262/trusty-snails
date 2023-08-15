package risyan.app.trustysnails.basecomponent.ui.component

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun SwipeDetectableLayout(
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    content: @Composable () -> Unit
) {
    var offset by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .pointerInput(Unit) {
                this.detectHorizontalDragGestures{ change, _ ->
                    offset += change.position.x - change.previousPosition.x
                    if (offset < -200) {
                        onSwipeRight()
                        offset = 0f
                    } else if(offset > 90){
                        onSwipeLeft()
                        offset = 0f
                    }
                }
            }
    ) {
        content()
    }
}
