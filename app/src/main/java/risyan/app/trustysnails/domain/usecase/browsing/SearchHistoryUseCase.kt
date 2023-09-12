package risyan.app.trustysnails.domain.usecase.browsing

import risyan.app.trustysnails.basecomponent.BaseUseCase
import risyan.app.trustysnails.data.remote.api.room.HistoryItemDao
import risyan.app.trustysnails.data.remote.model.HistoryItem
import risyan.app.trustysnails.domain.model.HistoryQueryRequest

class SearchHistoryUseCase(
    private val historyItemDao: HistoryItemDao
) : BaseUseCase<HistoryQueryRequest, List<HistoryItem>>() {

    override fun setup(parameter: HistoryQueryRequest) {
        super.setup(parameter)
        execute {
            historyItemDao.searchHistoryItems(parameter.query,
                15, parameter.currentPage*15)
        }
    }
}

class InsertHistoryUseCase(
    private val historyItemDao: HistoryItemDao
) : BaseUseCase<HistoryItem, Any?>() {

    override fun setup(parameter: HistoryItem) {
        super.setup(parameter)
        execute {
            historyItemDao.insert(parameter)
        }
    }
}