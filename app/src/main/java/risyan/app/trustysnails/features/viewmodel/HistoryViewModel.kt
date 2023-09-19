package risyan.app.trustysnails.features.viewmodel

import androidx.lifecycle.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import risyan.app.trustysnails.basecomponent.Event
import risyan.app.trustysnails.basecomponent.getBareContent
import risyan.app.trustysnails.data.remote.model.HistoryItem
import risyan.app.trustysnails.domain.model.HistoryQueryRequest
import risyan.app.trustysnails.domain.usecase.browsing.InsertHistoryUseCase
import risyan.app.trustysnails.domain.usecase.browsing.SearchHistoryUseCase
import risyan.app.trustysnails.features.model.TabModel
import risyan.app.trustysnails.features.model.changeAndAddTable
import risyan.app.trustysnails.features.model.changeTabInfo
import risyan.app.trustysnails.features.model.removeItem
import risyan.app.trustysnails.features.view.model.ContextMenuModel

class HistoryViewModel(
    val searchHistoryUseCase: SearchHistoryUseCase,
    val insertHistoryUseCase: InsertHistoryUseCase
): ViewModel() {

    val _isWebLoading = MutableLiveData(false)
    val isWebLoading : LiveData<Boolean> = _isWebLoading
    fun setWebLoading(isLoading : Boolean) { _isWebLoading.value = isLoading }

    val _contextMenuUrl = MutableLiveData(ContextMenuModel("", ""))
    val contextMenuUrl : LiveData<ContextMenuModel> = _contextMenuUrl
    fun showContextMenu(content: ContextMenuModel = ContextMenuModel()) {
        _contextMenuUrl.value = content }

    val _searchHistoryData = MediatorLiveData<Event<List<HistoryItem>>>().apply {
        addSource(searchHistoryUseCase.currentData){
            val currentValue = this.value?.bareContent() ?: null
            viewModelScope.launch {
                it.getBareContent()?.forEach {
                    val currentValue = this@apply.value?.bareContent() ?: null
                    delay(50)
                    value = Event(
                        ArrayList(currentValue ?: listOf()).apply {
                            add(it)
                        }
                    )
                }
            }
        }
    }
    val searchHistoryData : LiveData<Event<List<HistoryItem>>> = _searchHistoryData

    var currentPage = -1 ; var query = ""
    fun searchHistory(isReset : Boolean = false){
        if(isReset){
            currentPage = -1
            _searchHistoryData.value = Event(listOf())
        }
        currentPage ++
        searchHistoryUseCase.setup(HistoryQueryRequest(currentPage, this.query))
    }

    fun insertHistory(item : HistoryItem) = insertHistoryUseCase.setup(item)

    val _mutableTabListData = MutableLiveData(Event(listOf(TabModel(
        url = "google.com"))))
    val tabListData : LiveData<Event<List<TabModel>>> = _mutableTabListData

    fun changeOrAddNewTab(tab : TabModel){
        _mutableTabListData.value?.let {
            _mutableTabListData.value = Event(
                ArrayList(it.bareContent()).changeAndAddTable(tab)) } }
    fun removeTab(tab : TabModel){
        _mutableTabListData.value?.let {
            _mutableTabListData.value = Event(
                ArrayList(it.bareContent()).removeItem(tab).apply {
                    if(isEmpty()) add(TabModel())
                }
            ) } }

    fun updateCurrentTabInfo(
        historyItem: HistoryItem,
        delay : Long = 0
    ) {
        viewModelScope.launch {
            delay(delay)
            _mutableTabListData.value?.let {
                _mutableTabListData.value = Event(ArrayList(
                    it.bareContent()).changeTabInfo(historyItem)) }
        }
    }

    val tabData = MediatorLiveData<Event<TabModel>>().apply {
        addSource(tabListData){
            it.bareContent().find {
                it.isSelected }?.let {
                it.isNeedResetWebView = it.id != this.value?.bareContent()?.id
                value = Event(it)
            } ?: run {
                it.bareContent().last().let {
                    it.isSelected = true
                    it.isNeedResetWebView = true
                    value = Event(it)
                }
            }
        }
    }

    var searchJob : Job? = null
    var searchQuery = ""
    fun requestHistorySuggestion() {
        if(searchJob == null)
            searchJob = viewModelScope.launch{
                delay(350)
                query = searchQuery; currentPage = 0
                searchHistory(true)
                searchJob?.cancel()
                searchJob = null
            }
    }

    val _isSearching = MutableLiveData(false); val isSearching = _isSearching
    fun setSearching(boolean: Boolean) {
        searchQuery = ""; query = searchQuery;
        _isSearching.value = boolean
    }

}