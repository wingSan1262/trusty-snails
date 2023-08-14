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

    val _currentUrl = MutableLiveData(Pair("https://www.google.com", true))
    val currentUrl : LiveData<Pair<String, Boolean>> = _currentUrl

    fun updateCurrentUrl(req : Pair<String, Boolean>) {
        _currentUrl.value = req
    }

}