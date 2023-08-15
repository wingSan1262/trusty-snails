package risyan.app.trustysnails.features.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import risyan.app.trustysnails.domain.model.UserSettingModel
import risyan.app.trustysnails.domain.usecase.user.GetSettingUseCase
import risyan.app.trustysnails.domain.usecase.user.SetSettingUseCase

class UserViewModel(
    val getSettingUseCase: GetSettingUseCase,
    val setSettingUseCase: SetSettingUseCase
): ViewModel() {

    val getSettingData = getSettingUseCase.currentData
    fun getSetting(){
        getSettingUseCase.setup(null)
    }

    val setSettingData = setSettingUseCase.currentData
    fun setSetting(req : UserSettingModel){
        setSettingUseCase.setup(req)
    }

    val _currentUrl = MutableLiveData("https://www.google.com")
    val currentUrl : LiveData<String> = _currentUrl
    fun updateCurrentUrl(req : String) { _currentUrl.value = req }

    val _isWebLoading = MutableLiveData(false)
    val isWebLoading : LiveData<Boolean> = _isWebLoading
    fun setWebLoading(isLoading : Boolean) { _isWebLoading.value = isLoading }

}