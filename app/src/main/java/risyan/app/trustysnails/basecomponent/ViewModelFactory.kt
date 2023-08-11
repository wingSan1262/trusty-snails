package risyan.app.trustysnails.basecomponent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import risyan.app.trustysnails.domain.usecase.auth.LoginUseCase
import risyan.app.trustysnails.domain.usecase.auth.LoginWithGoogleUseCase
import risyan.app.trustysnails.domain.usecase.auth.RegisterUseCase
import risyan.app.trustysnails.domain.usecase.auth.UnregisterUseCase
import risyan.app.trustysnails.domain.usecase.user.GetSettingUseCase
import risyan.app.trustysnails.domain.usecase.user.SetSettingUseCase
import risyan.app.trustysnails.features.viewmodel.AuthViewModel
import risyan.app.trustysnails.features.viewmodel.UserViewModel

class ViewModelFactory(
    val loginUseCase : LoginUseCase,
    val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    val registerUseCase: RegisterUseCase,
    val unregisterUseCase: UnregisterUseCase,
    val getSettingUseCase: GetSettingUseCase,
    val setSettingUseCase: SetSettingUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(
                loginUseCase, loginWithGoogleUseCase,
                registerUseCase, unregisterUseCase) as T
        }
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(
                getSettingUseCase, setSettingUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}