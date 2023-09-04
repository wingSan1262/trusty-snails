package risyan.app.trustysnails.features.view.screen

import android.annotation.SuppressLint
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import coil.size.Size
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.*
import risyan.app.trustysnails.basecomponent.ui.component.UrlNavigatingEditText
import risyan.app.trustysnails.data.remote.model.BrowsingMode
import risyan.app.trustysnails.domain.model.UserSettingModel
import risyan.app.trustysnails.domain.model.getCleanIsSafe
import risyan.app.trustysnails.domain.model.getOneByOneIsSafe
import risyan.app.trustysnails.features.view.dialog.LinkContextMenu
import risyan.app.trustysnails.features.view.model.ContextMenuModel
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
    var isOffline = userViewModel.isOffline.observeAsState()
    val getUserSettingData = userViewModel.getSettingData.observeAsState()
    var userSetting : UserSettingModel? by remember { mutableStateOf(null) }
    val contextMenuContent = userViewModel.contextMenuUrl.observeAsState()

    val webView = remember {
        (context as ComponentActivity).createWebViewWithDefaults({
                userViewModel.setWebLoading(false)
            },{
                userViewModel.setOffline(false)
                isBlocked = if(userSetting?.browsingMode == BrowsingMode.CLEAN_MODE)
                    userSetting?.cleanFilterList?.getCleanIsSafe(it) ?: false
                else
                    userSetting?.oneByOneList?.getOneByOneIsSafe(it) ?: false
                userViewModel.setWebLoading(true)
                userViewModel.updateCurrentUrl(it)
            },
            { userViewModel.setOffline(true) },
            { filelink, mime ->
                // TODO temporary
                if(mime.isEmpty())
                    userViewModel.showContextMenu(ContextMenuModel(filelink, mime))
            }
        ){ filelink, mime ->

        }
    }


    Column {
        TopBarUrlNavigations(
            userViewModel,
            webView, navigateToSetting)
        if(!isBlocked && isOffline.value == false)
            webContentView(webView)
        else
            PageNotAvailableMessage(
                imageRes = if(userSetting?.browsingMode == BrowsingMode.CLEAN_MODE)
                R.drawable.clean_mode else R.drawable.one_by_one,
                if(isOffline.value == true) "You're offline currently" else
                    "The page you're looking is blocked by you"
            )
    }

    BackHandler(true) {
        webView.goBack()
    }

    contextMenuContent.value?.run{
        LinkContextMenu(
            onDismiss = { userViewModel.showContextMenu() },
            linkContent = if(mimeType.isNotEmpty()) mimeType else url,
            onDownload = {
                if(mimeType.isEmpty())
                    context.downloadFile(url)
            },
        )
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
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(true){
            delay(100)
            webView.loadUrl(urlState.value ?: "https://www.google.com")
        }

        UrlNavigatingEditText(
            onNewLink = { enteredUrl ->
                coroutineScope.launch {
                    if(userViewModel.isOffline.value == true){
                        userViewModel.getSetting()
                    }
                    delay(175)
                    webView.loadUrl(enteredUrl)
                }
            },
            valueText = urlState.value ?: "",
            placeholder = "Put URL here",
            isLoading = isWebLoading.value ?: false,
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
    }
}

@Composable
fun webContentView(
    webView: WebView,
) {
    val context = LocalContext.current
    AndroidView(
        factory = { context ->
            webView
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun PageNotAvailableMessage(
    imageRes : Int,
    msg : String
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
            text = msg,
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

