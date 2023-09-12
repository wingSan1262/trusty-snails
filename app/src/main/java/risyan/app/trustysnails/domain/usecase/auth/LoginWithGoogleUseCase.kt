package risyan.app.trustysnails.domain.usecase.auth

import kotlinx.coroutines.launch
import risyan.app.trustysnails.basecomponent.BaseUseCase
import risyan.app.trustysnails.data.remote.api.firebase.AuthStatus
import risyan.app.trustysnails.data.remote.api.firebase.FbAuth
import risyan.app.trustysnails.data.remote.api.firebase.UserApi
import risyan.app.trustysnails.data.remote.model.UserSettingDto

class LoginWithGoogleUseCase(
    val fbApi : FbAuth,
    val userApi: UserApi,

    ): BaseUseCase<String, AuthStatus>() {
    override fun setup(parameter: String) {
        super.setup(parameter)
        execute {
            val result = fbApi.loginWithGoogle(parameter)
            launch {
                if(result is AuthStatus.NewUserFromGoogle){
                    userApi.setUserData(UserSettingDto())
                }
            }
            return@execute result
        }
    }
}