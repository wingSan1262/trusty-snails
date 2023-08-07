package risyan.app.trustysnails.basecomponent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import risyan.app.trustysnails.domain.LoginUseCase
import risyan.app.trustysnails.domain.LoginWithGoogleUseCase
import risyan.app.trustysnails.domain.RegisterUseCase
import risyan.app.trustysnails.domain.UnregisterUseCase
import risyan.app.trustysnails.features.viewmodel.AuthViewModel

class ViewModelFactory(
    val loginUseCase : LoginUseCase,
    val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    val registerUseCase: RegisterUseCase,
    val unregisterUseCase: UnregisterUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(
                loginUseCase, loginWithGoogleUseCase,
                registerUseCase, unregisterUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}