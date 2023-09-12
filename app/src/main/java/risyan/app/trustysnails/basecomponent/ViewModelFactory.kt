package risyan.app.trustysnails.basecomponent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import risyan.app.trustysnails.domain.usecase.auth.LoginWithGoogleUseCase
import risyan.app.trustysnails.domain.usecase.auth.LogoutGoogleUseCase
import risyan.app.trustysnails.domain.usecase.browsing.InsertHistoryUseCase
import risyan.app.trustysnails.domain.usecase.browsing.SearchHistoryUseCase
import risyan.app.trustysnails.domain.usecase.user.GetSettingUseCase
import risyan.app.trustysnails.domain.usecase.user.SetSettingUseCase
import risyan.app.trustysnails.features.viewmodel.AuthViewModel
import risyan.app.trustysnails.features.viewmodel.HistoryViewModel
import risyan.app.trustysnails.features.viewmodel.UserViewModel

class ViewModelFactory(
    val loginWithGoogleUseCase: LoginWithGoogleUseCase,
    val logoutGoogleUseCase: LogoutGoogleUseCase,
    val getSettingUseCase: GetSettingUseCase,
    val setSettingUseCase: SetSettingUseCase,
    val searchHistoryUseCase: SearchHistoryUseCase,
    val insertHistoryUseCase: InsertHistoryUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(
                loginWithGoogleUseCase, logoutGoogleUseCase) as T
        }
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            return UserViewModel(
                getSettingUseCase, setSettingUseCase) as T
        }

        if (modelClass.isAssignableFrom(HistoryViewModel::class.java)) {
            return HistoryViewModel(
                searchHistoryUseCase, insertHistoryUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}