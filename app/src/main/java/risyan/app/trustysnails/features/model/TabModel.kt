package risyan.app.trustysnails.features.model

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.webkit.WebView
import risyan.app.trustysnails.data.remote.model.HistoryItem
import java.util.UUID

data class TabModel(
    val id: String = UUID.randomUUID().toString(), // Unique tab ID
    var url: String = "www.google.com", // Current URL
    var title: String = "google", // Page title
    val history: ArrayList<HistoryItem> = arrayListOf(), // Browsing history
    var isSelected: Boolean = true,
    var isNeedResetWebView : Boolean = false
){
    fun setWebViewHistory(webView: WebView){
        webView.clearHistory()
        history.forEach {
            webView.loadUrl(it.url)
        }
        webView.loadUrl(url)
    }

    fun pushUrlHistory(item: HistoryItem){ history.add(item) }
}

fun ArrayList<TabModel>.removeItem(tabModel: TabModel): ArrayList<TabModel> {
    this.remove(tabModel)
    return this
}

fun ArrayList<TabModel>.changeAndAddTable(tabModel: TabModel): List<TabModel> {
    var isFound = false
    forEach {
        it.isSelected = false
        if(it.id == tabModel.id){
            isFound = true
        }
    }

    tabModel.isSelected = true
    if(!isFound) add(tabModel)
    return this
}

fun ArrayList<TabModel>.changeTabInfo(
    historyItem: HistoryItem
): List<TabModel> {
    forEach {
        if(it.isSelected){
            it.history.add(historyItem)
            it.title = historyItem.title;
            it.url = historyItem.url
        }
    }
    return this
}