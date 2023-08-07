package risyan.app.trustysnails.features.viewmodel

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import risyan.app.trustysnails.basecomponent.Event
import risyan.app.trustysnails.basecomponent.ResourceState
import risyan.app.trustysnails.data.remote.api.AuthStatus
import risyan.app.trustysnails.data.remote.model.FbUserLoginData
import risyan.app.trustysnails.domain.LoginUseCase
import risyan.app.trustysnails.domain.LoginWithGoogleUseCase
import risyan.app.trustysnails.domain.RegisterUseCase
import risyan.app.trustysnails.domain.UnregisterUseCase

class AuthViewModel(
    val loginUseCase : LoginUseCase,
    val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    val registerUseCase: RegisterUseCase,
    val unregisterUseCase: UnregisterUseCase
): ViewModel() {

    val loginData by lazy {
        MediatorLiveData<Event<ResourceState<AuthStatus>>>().apply {
            addSource(loginUseCase.currentData){ value = it }
            addSource(loginWithGoogleUseCase.currentData){ value = it }
        }
    }
    fun loginByGoogle(idtoken : String) = loginWithGoogleUseCase.setup(idtoken)
    fun login(req : FbUserLoginData) = loginUseCase.setup(req)

    val registerData = registerUseCase.currentData
    fun registerUser(req : FbUserLoginData) = registerUseCase.setup(req)

    val deleteAccountData = unregisterUseCase.currentData
    fun deleteAccount(req : FbUserLoginData) = unregisterUseCase.setup(req)
}