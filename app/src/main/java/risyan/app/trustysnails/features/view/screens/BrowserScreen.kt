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
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.size.Size
import kotlinx.coroutines.delay
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.GifDisplay
import risyan.app.trustysnails.basecomponent.ResourceEffect
import risyan.app.trustysnails.basecomponent.createWebViewWithDefaults
import risyan.app.trustysnails.basecomponent.showToast
import risyan.app.trustysnails.basecomponent.ui.component.CommonEditText
import risyan.app.trustysnails.basecomponent.ui.component.SwipeDetectableLayout
import risyan.app.trustysnails.basecomponent.ui.component.UrlNavigatingEditText
import risyan.app.trustysnails.data.remote.model.BrowsingMode
import risyan.app.trustysnails.domain.model.UserSettingModel
import risyan.app.trustysnails.domain.model.getCleanIsSafe
import risyan.app.trustysnails.domain.model.getOneByOneIsSafe
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

    var isBlocked by remember { mutableStateOf(false) }

    val getUserSettingData = userViewModel.getSettingData.observeAsState()
    var userSetting : UserSettingModel? by remember { mutableStateOf(null) }

    val webView = remember {
        (context as ComponentActivity).createWebViewWithDefaults({
            userViewModel.setWebLoading(false)
        }){
            isBlocked = if(userSetting?.browsingMode == BrowsingMode.CLEAN_MODE)
                userSetting?.cleanFilterList?.getCleanIsSafe(it) ?: false
            else
                userSetting?.oneByOneList?.getOneByOneIsSafe(it) ?: false
            userViewModel.updateCurrentUrl(it)
        }
    }


    Column {
        TopBarUrlNavigations(
            userViewModel,
            webView, navigateToSetting)
        if(!isBlocked)
            webContentView(webView)
        else
            BlockedPageMessage(imageRes = if(userSetting?.browsingMode == BrowsingMode.CLEAN_MODE)
                R.drawable.clean_mode else R.drawable.one_by_one)
    }

    BackHandler(true) {
        webView.goBack()
    }

    ResourceEffect(getUserSettingData,{
        userSetting = it.body;
    }){data, owner ->
        userSetting = UserSettingModel()
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

@Composable
fun BlockedPageMessage(
    imageRes : Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        GifDisplay(
            gifResource = imageRes,
            size = Size(240, 240),
            modifier = Modifier.size(240.dp)
        )

        Text(
            text = "The page you're looking is blocked by you",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 300,
                        easing = FastOutSlowInEasing
                    )
                )
        )
    }
}