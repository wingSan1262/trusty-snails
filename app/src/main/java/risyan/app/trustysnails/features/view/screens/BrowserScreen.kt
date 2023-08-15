package risyan.app.trustysnails.features.view.screen

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.createWebViewWithDefaults
import risyan.app.trustysnails.basecomponent.ui.component.CommonEditText
import risyan.app.trustysnails.basecomponent.ui.component.SwipeDetectableLayout
import risyan.app.trustysnails.basecomponent.ui.component.UrlNavigatingEditText
import risyan.app.trustysnails.features.view.navigator.Screen
import risyan.app.trustysnails.features.viewmodel.UserViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun NavGraphBuilder.BrowserScreen(
    userViewModel: UserViewModel,
    navigateToSetting : ()->Unit,
){
    composable(route = Screen.BROWSER_SCREEN){
        BrowserScreenContent(userViewModel, navigateToSetting)
    }
}

@Composable
fun BrowserScreenContent(
    userViewModel: UserViewModel,
    navigateToSetting : ()->Unit,
) {
    val context = LocalContext.current

    val webView = remember {
        (context as ComponentActivity).createWebViewWithDefaults({
            userViewModel.setWebLoading(false)
        }){
            userViewModel.updateCurrentUrl(it)
        }
    }

    Column {
        TopBarUrlNavigations(
            userViewModel,
            webView, navigateToSetting)
        webContentView(webView)
    }

    BackHandler(true) {
        webView.goBack()
    }
}

@Composable
fun TopBarUrlNavigations(
    userViewModel: UserViewModel,
    webView: WebView,
    onSetting : ()->Unit,
) {
    TopAppBar(
        backgroundColor = Color(0xFF1946AE),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.height(56.dp)
    ) {

        val urlState = userViewModel.currentUrl.observeAsState()
        val isWebLoading = userViewModel.isWebLoading.observeAsState()

        LaunchedEffect(true){
            delay(500)
            webView.loadUrl(urlState.value ?: "https://www.google.com")
        }

        UrlNavigatingEditText(
            onNewLink = { enteredUrl -> webView.loadUrl(enteredUrl) },
            valueText = urlState.value ?: "",
            placeholder = "Put URL here",
            Modifier
                .width(0.dp)
                .weight(1f)
                .padding(horizontal = 4.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .background(
                    Color.White,
                    RoundedCornerShape(8.dp)
                ) // Set the background color and rounded corner shape
                .padding(8.dp)
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_setting), // Replace with your burger icon
            contentDescription = "Menu",
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    onSetting()
                },
            tint = Color.White
        )

        if (isWebLoading.value == true) {
            LinearProgressIndicator(
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .padding(bottom = 1.dp)
            )
        }
    }
}

@Composable
fun webContentView(
    webView: WebView
) {
    AndroidView(
        factory = { context ->
            webView
        },
        modifier = Modifier.fillMaxSize()
    )
}