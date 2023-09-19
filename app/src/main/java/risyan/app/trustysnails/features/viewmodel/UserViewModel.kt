package risyan.app.trustysnails.features.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import risyan.app.trustysnails.domain.model.UserSettingModel
import risyan.app.trustysnails.domain.usecase.user.GetSettingUseCase
import risyan.app.trustysnails.domain.usecase.user.SetSettingUseCase

class UserViewModel(
    val getSettingUseCase: GetSettingUseCase,
    val setSettingUseCase: SetSettingUseCase,
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
}