package risyan.app.trustysnails.basecomponent.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun CommonEditText(
    onValueChange: (String) -> Unit,
    startingText: String,
    placeholder: String,
    onDone: () -> Unit = {},
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
        .background(Color.White, RoundedCornerShape(8.dp)) // Set the background color and rounded corner shape
        .padding(8.dp)
) {
    var bounceText by remember { mutableStateOf(startingText) }

    Box(
        modifier = modifier
    ) {
        BasicTextField(
            value = bounceText,
            onValueChange = {
                bounceText = it
                onValueChange(it)
            },
            decorationBox = { innerTextField ->
                if (bounceText.isEmpty()) {
                    Text(placeholder, color = Color.Gray)
                }
                innerTextField()
            },
            textStyle = TextStyle(color = Color.Black),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused) {
                        onDone()
                    }
                },
            keyboardActions = KeyboardActions(onDone = {
                onDone()
            }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
    }
}