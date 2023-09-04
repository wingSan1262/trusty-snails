package risyan.app.trustysnails.features.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import risyan.app.trustysnails.domain.model.UserSettingModel
import risyan.app.trustysnails.domain.usecase.user.GetSettingUseCase
import risyan.app.trustysnails.domain.usecase.user.SetSettingUseCase
import risyan.app.trustysnails.features.view.model.ContextMenuModel

class UserViewModel(
    val getSettingUseCase: GetSettingUseCase,
    val setSettingUseCase: SetSettingUseCase
): ViewModel() {

    val _isOffline  = MutableLiveData(false)
    val isOffline : LiveData<Boolean> = _isOffline
    fun setOffline(isOffline : Boolean){
        _isOffline.value = isOffline}

    val getSettingData = getSettingUseCase.currentData
    fun getSetting(){
        getSettingUseCase.setup(null)
    }

    val setSettingData = setSettingUseCase.currentData
    fun setSetting(req : UserSettingModel){
        setSettingUseCase.setup(req.apply {
            validityTransform()
        })
    }

    val _currentUrl = MutableLiveData("https://www.google.com")
    val currentUrl : LiveData<String> = _currentUrl
    fun updateCurrentUrl(req : String) { _currentUrl.value = req }

    val _isWebLoading = MutableLiveData(false)
    val isWebLoading : LiveData<Boolean> = _isWebLoading
    fun setWebLoading(isLoading : Boolean) { _isWebLoading.value = isLoading }

    val _contextMenuUrl = MutableLiveData(ContextMenuModel("", ""))
    val contextMenuUrl : LiveData<ContextMenuModel> = _contextMenuUrl
    fun showContextMenu(content: ContextMenuModel = ContextMenuModel()) {
        _contextMenuUrl.value = content }

}