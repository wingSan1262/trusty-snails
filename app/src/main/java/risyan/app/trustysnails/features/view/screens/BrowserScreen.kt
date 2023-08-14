package risyan.app.trustysnails.features.view.screen

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.ui.component.CommonEditText
import risyan.app.trustysnails.features.view.navigator.BROWSER_SCREEN
import risyan.app.trustysnails.features.viewmodel.UserViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun NavGraphBuilder.BrowserScreen(
    userViewModel: UserViewModel
){
    composable(route = BROWSER_SCREEN){
        BrowserScreenContent()
    }
}

@Composable
@Preview
fun SettingPreview(){
    BrowserScreenContent()
}

@Composable
fun BrowserScreenContent() {
    var webView : WebView? = null
    Column {
        TopBar {
            webView?.loadUrl(it)
        }
        webContentView{
            webView = it
        }
    }
}

@Composable
fun TopBar(
    onUpdateUrl : (String)->Unit
) {
    TopAppBar(
        backgroundColor = Color(0xFF1946AE),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = Modifier.height(72.dp)
    ) {
        var url by remember { mutableStateOf("https://www.google.com") }
        var isLoading by remember { mutableStateOf(false) }
        val context = LocalContext.current

        CommonEditText(
            onValueChange = { enteredUrl ->
                url = enteredUrl
            },
            startingText = url,
            placeholder = "Put URL here",
            onDone = {
                isLoading = true
                onUpdateUrl(url)
            },
            Modifier
                .width(0.dp)
                .weight(1f)
                .padding(4.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .background(
                    Color.White,
                    RoundedCornerShape(8.dp)
                ) // Set the background color and rounded corner shape
                .padding(8.dp)
        )

        Icon(
            painter = painterResource(id = R.drawable.forbidden), // Replace with your forbidden icon
            contentDescription = "Forbidden",
            modifier = Modifier
                .size(32.dp)
                .padding(end = 8.dp),
            tint = Color.White
        )

        Icon(
            painter = painterResource(id = R.drawable.burger_icon), // Replace with your burger icon
            contentDescription = "Menu",
            modifier = Modifier.size(32.dp),
            tint = Color.White
        )

        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun webContentView(
    onWebView : (WebView)->Unit
) {
    // You can use AndroidView to integrate WebView
    var webView : WebView? = null
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webChromeClient = WebChromeClient()
                settings.javaScriptEnabled = true
            }.also {
                onWebView(it)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}