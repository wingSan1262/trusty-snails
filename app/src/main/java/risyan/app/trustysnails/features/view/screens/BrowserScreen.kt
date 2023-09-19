package risyan.app.trustysnails.features.view.screen

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import kotlinx.coroutines.launch
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.*
import risyan.app.trustysnails.basecomponent.ui.component.RotatingComponent
import risyan.app.trustysnails.basecomponent.ui.component.SlideFromBottomContainer
import risyan.app.trustysnails.basecomponent.ui.component.UrlNavigatingEditText
import risyan.app.trustysnails.basecomponent.ui.theme.GRAY_757575
import risyan.app.trustysnails.basecomponent.ui.theme.GRAY_CDD5EA
import risyan.app.trustysnails.basecomponent.ui.theme.GRAY_EEEEEE
import risyan.app.trustysnails.data.remote.model.BrowsingMode
import risyan.app.trustysnails.domain.model.UserSettingModel
import risyan.app.trustysnails.domain.model.getCleanIsSafe
import risyan.app.trustysnails.domain.model.getOneByOneIsSafe
import risyan.app.trustysnails.features.model.TabModel
import risyan.app.trustysnails.features.view.component.BottomTabBar
import risyan.app.trustysnails.features.view.dialog.LinkContextMenu
import risyan.app.trustysnails.features.view.model.ContextMenuModel
import risyan.app.trustysnails.features.view.navigator.Screen
import risyan.app.trustysnails.features.view.screens.HistoryScreenContent
import risyan.app.trustysnails.features.viewmodel.HistoryViewModel
import risyan.app.trustysnails.features.viewmodel.UserViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun NavGraphBuilder.BrowserScreen(
    userViewModel: UserViewModel,
    historyViewModel: HistoryViewModel,
    navigateToSetting : ()->Unit,
    navigateToHistory : ()->Unit
){
    composable(route = Screen.BROWSER_SCREEN){
        BrowserScreenContent(userViewModel, historyViewModel, navigateToSetting, navigateToHistory)
    }
}

@Composable
fun BrowserScreenContent(
    userViewModel: UserViewModel,
    historyViewModel: HistoryViewModel,
    navigateToSetting : ()->Unit,
    navigateToHistory : ()->Unit
) {
    val context = LocalContext.current

    var isBlocked by remember { mutableStateOf(false) }
    var isOffline = userViewModel.isOffline.observeAsState()
    val getUserSettingData = userViewModel.getSettingData.observeAsState()
    var userSetting : UserSettingModel? by remember { mutableStateOf(null) }
    val contextMenuContent = historyViewModel.contextMenuUrl.observeAsState()
    val tabListState = historyViewModel.tabListData.observeAsState()

    val isShowSuggesting = historyViewModel.isSearching.observeAsState()


    var fileCallback : ValueCallback<Array<Uri>>? by remember { mutableStateOf(null) }
    val fileLauncher = ComposeActivityLauncher({
        fileCallback?.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(
            Activity.RESULT_OK, it))
        fileCallback = null
    })

    val webView = remember {
        (context as ComponentActivity).createWebViewWithDefaults({
                historyViewModel.setWebLoading(false)
                historyViewModel.updateCurrentTabInfo(it)
                historyViewModel.insertHistory(it)
            },{
                userViewModel.setOffline(false)
                isBlocked = if(userSetting?.browsingMode == BrowsingMode.CLEAN_MODE)
                    userSetting?.cleanFilterList?.getCleanIsSafe(it) ?: false
                else
                    userSetting?.oneByOneList?.getOneByOneIsSafe(it) ?: false
                historyViewModel.setWebLoading(true)
            },
            { userViewModel.setOffline(true) },
            { filelink ->
                historyViewModel.showContextMenu(ContextMenuModel(filelink))
            },{ i, fileCb ->
                fileCallback = fileCb; fileLauncher.launch(i)
            }
        ){ urlTab -> historyViewModel.showContextMenu(ContextMenuModel(webUrl = urlTab)) }
    }



    Scaffold(
        topBar = {
            TopBarUrlNavigations(
                userViewModel, historyViewModel,
                webView, navigateToSetting)
        },
        bottomBar = {
            BottomTabBar(
                tabListState.value?.bareContent() ?: listOf(),
                { historyViewModel.changeOrAddNewTab(it) },
                { historyViewModel.removeTab(it)},
                { historyViewModel.changeOrAddNewTab(TabModel()) }
            ) {navigateToHistory()}
        },
        content = {
            if(isShowSuggesting.value == true){
                HistoryScreenContent(historyViewModel = historyViewModel, {
                    Text(
                        "Search on google", fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp)
                            .clickable {
                                historyViewModel.setSearching(false)
                                webView.loadUrl(
                                    historyViewModel.searchQuery.generateGoogleSearchUrl()
                                )
                            })

                    Text(
                        "Open as web link", fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(start = 16.dp, top = 8.dp)
                            .clickable {
                                historyViewModel.setSearching(false)
                                webView.loadUrl(
                                    historyViewModel.searchQuery.recheckValidityAndTransform()
                                )
                            })

                }, isTopBar = false){
                    webView.loadUrl(it?.url.toString())
                }
                return@Scaffold
            }
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(bottom = it.calculateBottomPadding())
            ) {
                if(!isBlocked && isOffline.value == false)
                    webContentView(webView)
                else
                    PageNotAvailableMessage(
                        imageRes = if(userSetting?.browsingMode == BrowsingMode.CLEAN_MODE)
                            R.drawable.clean_mode else R.drawable.one_by_one,
                        if(isOffline.value == true) "You're offline currently" else
                            "The page you're looking is blocked by you"
                    )

                contextMenuContent.value?.run{
                    if(downloadUrl.isNotEmpty() || webUrl.isNotEmpty())
                        LinkContextMenu(
                            onDismiss = { historyViewModel.showContextMenu() },
                            downloadLink = this.downloadUrl ,
                            webLink = this.webUrl,
                            onDownload = {
                                if(mimeType.isEmpty())
                                    context.downloadFile(downloadUrl)
                            },
                            onOpenLinkNewTab = {
                                historyViewModel.changeOrAddNewTab(TabModel(url = it))
                            }
                        )
                }
            }
        }
    )

    BackHandler(true) {
        webView.goBack()
        webView.copyBackForwardList()
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
    historyViewModel: HistoryViewModel,
    webView: WebView,
    onSetting : ()->Unit,
) {
    TopAppBar(
        backgroundColor = Color(0xFF1946AE),
        contentPadding = PaddingValues(start = 16.dp, end = 16 .dp),
        modifier = Modifier.height(56.dp)
    ) {

        val tabState = historyViewModel.tabData.observeAsState() // todo check if need reload webview, set history
        val isWebLoading = historyViewModel.isWebLoading.observeAsState()
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(key1 = tabState.value){
            if(tabState.value?.bareContent()?.isNeedResetWebView == true)
                tabState.value?.bareContent()?.setWebViewHistory(webView)
        }

        LaunchedEffect(true){
            delay(300)
            webView.loadUrl(tabState.value?.bareContent()?.url ?: "https://www.google.com")
        }

        SlideFromBottomContainer(content = {
            Card(
                backgroundColor = Color.White,
                elevation = 24.dp,
                shape = RoundedCornerShape(8.dp)
            ){
                UrlNavigatingEditText(
                    onNewLink = { enteredUrl ->
                        historyViewModel.setSearching(false)
                        coroutineScope.launch {
                            if(userViewModel.isOffline.value == true){
                                userViewModel.getSetting()
                            }
                            delay(175)
                            webView.loadUrl(enteredUrl)
                        }
                    },
                    valueText = tabState.value?.bareContent()?.url ?: "",
                    placeholder = "Put URL here",
                    historyViewModel,
                    isLoading = isWebLoading.value ?: false,
                    Modifier
                        .background(
                            Color.White,
                            RoundedCornerShape(8.dp)
                        ) // Set the background color and rounded corner shape
                        .padding(8.dp)
                ){
                    historyViewModel.setSearching(true)
                    historyViewModel.searchQuery = it
                    historyViewModel.requestHistorySuggestion()
                }
            }
        }, modifier = Modifier
            .width(0.dp)
            .weight(1f)
            .padding(horizontal = 4.dp))

        RotatingComponent(content = {
            Icon(
                painter = painterResource(id = R.drawable.ic_setting), // Replace with your burger icon
                contentDescription = "Menu",
                modifier = Modifier
                    .clickable {
                        onSetting()
                    },
                tint = Color(0xFFEEEEEE)
            )
        }, Modifier
            .padding(start = 8.dp)
            .size(26.dp))

        Icon(
            painter = painterResource(id = R.drawable.ic_forward), // Replace with your burger icon
            contentDescription = "Forward",
            modifier = Modifier
                .padding(start = 12.dp)
                .size(24.dp)
                .clickable { webView.goForward() },
            tint = Color(0xFFEEEEEE)
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

