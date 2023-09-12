@file:OptIn(ExperimentalComposeUiApi::class)

package risyan.app.trustysnails.basecomponent.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import risyan.app.trustysnails.basecomponent.recheckValidityAndTransform
import risyan.app.trustysnails.features.viewmodel.HistoryViewModel

@Composable
fun CommonEditText(
    onValueChange: (String) -> Unit = {},
    startingText: String,
    placeholder: String,
    onDone: (String) -> Unit = {},
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
        .background(
            Color.White,
            RoundedCornerShape(8.dp)
        ) // Set the background color and rounded corner shape
        .padding(8.dp),
) {
    var bounceText by remember { mutableStateOf(startingText) }
    val keyboardController = LocalSoftwareKeyboardController.current

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
                        onDone(bounceText)
                    }
                },
            keyboardActions = KeyboardActions(onDone = {
                onDone(bounceText)
                keyboardController?.hide()
            }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
    }
}

@Composable
fun UrlNavigatingEditText(
    onNewLink: (String) -> Unit,
    valueText: String,
    placeholder: String,
    historyViewModel: HistoryViewModel? = null,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp)
        .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
        .background(
            Color.White,
            RoundedCornerShape(8.dp)
        ) // Set the background color and rounded corner shape
        .padding(8.dp),
    isUseRefresh : Boolean = true,
    onContentChangeWhenEdit: (String) -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var edittingValue by remember { mutableStateOf(valueText) }
    val focusManager = LocalFocusManager.current

    var isEditting = historyViewModel?.isSearching?.observeAsState()
        ?: remember { mutableStateOf(false) }
    fun setIsEditting(boolean: Boolean){
        if(historyViewModel == null)
            (isEditting as? MutableState<Boolean>)?.value = boolean
        else
            historyViewModel.setSearching(boolean)
    }

    LaunchedEffect(key1 = isEditting.value){
        if(isEditting.value == false){
            edittingValue = valueText
            focusManager.clearFocus(true)
            keyboardController?.hide()
        }
    }



    Row(
        modifier = modifier
    ) {

        BasicTextField(
            value = if(isEditting.value == true) edittingValue else valueText,
            onValueChange = {
                edittingValue = it
                if(isEditting.value == true) onContentChangeWhenEdit(it)
            },
            decorationBox = { innerTextField ->
                if (isEditting.value == true && edittingValue.isEmpty()) {
                    Text(placeholder, color = Color.Gray)
                } else if (isEditting.value == false && valueText.isEmpty()){
                    Text(placeholder, color = Color.Gray)
                }
                innerTextField()
            },
            textStyle = TextStyle(color = Color.Black),
            singleLine = true,
            modifier = Modifier
                .width(0.dp)
                .weight(1f)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused)
                        setIsEditting(true)
                },
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus(true)
                setIsEditting(false)
                edittingValue = edittingValue.recheckValidityAndTransform()
                onNewLink(edittingValue)
                keyboardController?.hide()
            }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        if (isEditting.value == true) {
            IconButton(
                onClick = {
                    edittingValue = ""
                },
                modifier = Modifier.size(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = null, tint = Color.Gray)
            }
            return@Row
        }

        if(isLoading)
            CircularProgressIndicator(
                Modifier
                    .size(16.dp)
                    .padding(start = 4.dp, top = 2.dp),
                color = Color.Gray,
                strokeWidth = 3.dp
            )
        else if(isEditting.value == false && isUseRefresh)
            IconButton(
                onClick = { onNewLink(valueText) },
                modifier = Modifier
                    .size(16.dp)
                    .padding(start = 4.dp, top = 2.dp),
            ) { Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = Color.Gray) }
    }

}