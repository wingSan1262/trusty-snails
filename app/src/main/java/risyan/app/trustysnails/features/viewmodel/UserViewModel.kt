package risyan.app.trustysnails.features.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import risyan.app.trustysnails.basecomponent.Event
import risyan.app.trustysnails.basecomponent.ResourceState
import risyan.app.trustysnails.data.remote.api.AuthStatus
import risyan.app.trustysnails.data.remote.model.FbUserLoginData
import risyan.app.trustysnails.domain.model.UserSettingModel
import risyan.app.trustysnails.domain.usecase.auth.LoginUseCase
import risyan.app.trustysnails.domain.usecase.auth.LoginWithGoogleUseCase
import risyan.app.trustysnails.domain.usecase.auth.RegisterUseCase
import risyan.app.trustysnails.domain.usecase.auth.UnregisterUseCase
import risyan.app.trustysnails.domain.usecase.user.GetSettingUseCase
import risyan.app.trustysnails.domain.usecase.user.SetSettingUseCase

class UserViewModel(
    val getSettingUseCase: GetSettingUseCase,
    val setSettingUseCase: SetSettingUseCase
): ViewModel() {

    val getSettingData = getSettingUseCase.currentData
    fun getSetting() = getSettingUseCase.setup(null)

    val setSettingData = setSettingUseCase.currentData
    fun setSetting(req : UserSettingModel) = setSettingUseCase.setup(req)
}