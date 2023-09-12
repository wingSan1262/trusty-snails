package risyan.app.trustysnails.features.view.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import risyan.app.trustysnails.basecomponent.ui.component.HistoryItemView
import risyan.app.trustysnails.data.remote.model.HistoryItem
import risyan.app.trustysnails.features.view.component.CommonSearchBar
import risyan.app.trustysnails.features.view.navigator.Screen
import risyan.app.trustysnails.features.viewmodel.HistoryViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun NavGraphBuilder.HistoryScreen(
    historyViewModel: HistoryViewModel,
    headerContent: @Composable () -> Unit = {},
    back: (HistoryItem?) -> Unit = {}
){
    composable(route = Screen.HISTORY_SCREEN){
        HistoryScreenContent(historyViewModel, headerContent, back = back)
    }
}

@Composable
fun HistoryScreenContent(
    historyViewModel: HistoryViewModel,
    headerContent: @Composable () -> Unit = {},
    isTopBar : Boolean = true,
    back: (HistoryItem?) -> Unit = {}
){

    val historyData = historyViewModel.searchHistoryData.observeAsState()
    var isQuerying by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = isQuerying ){
        if(isQuerying){
            delay(500)
            isQuerying = false } }

    var isFirstQuery by remember { mutableStateOf(true) }
    LaunchedEffect(key1 = true){ historyViewModel.query = ""
        historyViewModel.searchHistory(true); delay(300); isFirstQuery = false}


    val coroutine = rememberCoroutineScope()

    Scaffold(
        topBar = {
            if(!isTopBar) return@Scaffold
            TopAppBar(
                backgroundColor = Color(0xFF1946AE)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null, tint = Color(0xFFEEEEEE),
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .clickable {
                            historyViewModel.setSearching(false)
                            back(null)
                        }
                )
                CommonSearchBar(onNewContent = {
                    if(isQuerying) return@CommonSearchBar
                    isQuerying = true
                    historyViewModel.query = it
                    historyViewModel.searchHistory(true)
                }, valueText = "", placeholder = "Search History")
            }
        }
    ){
        it
        LazyColumn{
            item {
                Column(Modifier.fillMaxWidth()) {
                    headerContent()
                }
            }
            itemsIndexed(
                historyData.value?.bareContent() ?: listOf(),
                key = {index, item -> item.url}
            ) { index, item ->
                HistoryItemView(item){
                    coroutine.launch {
                        back(it)
                        historyViewModel.updateCurrentTabInfo(it)
                        historyViewModel.setSearching(false)
                    }
                }
            }
            item {
                LaunchedEffect(true) {
                    if(isQuerying) return@LaunchedEffect
                    if(!isFirstQuery) {
                        historyViewModel.searchHistory()
                        isQuerying = true
                    }
                }
            }
        }
    }
}


@Preview
@Composable
fun HistoryScreenPreview() {
    SplashContent{}
}