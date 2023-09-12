package risyan.app.trustysnails.features.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import risyan.app.trustysnails.basecomponent.Event
import risyan.app.trustysnails.basecomponent.ResourceState
import risyan.app.trustysnails.data.remote.api.firebase.AuthStatus
import risyan.app.trustysnails.domain.usecase.auth.LoginWithGoogleUseCase
import risyan.app.trustysnails.domain.usecase.auth.LogoutGoogleUseCase

class AuthViewModel(
    val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    val logoutGoogleUseCase: LogoutGoogleUseCase
): ViewModel() {

    val loginData by lazy {
        MediatorLiveData<Event<ResourceState<AuthStatus>>>().apply {
            addSource(loginWithGoogleUseCase.currentData){
                value = it }
        }
    }
    fun loginByGoogle(googleIntent : String) = loginWithGoogleUseCase.setup(googleIntent)

    val logoutGoogleData = logoutGoogleUseCase.currentData
    fun logout() = logoutGoogleUseCase.setup(null)
}