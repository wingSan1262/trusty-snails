package risyan.app.trustysnails.domain.usecase.auth

import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import risyan.app.trustysnails.basecomponent.BaseUseCase
import risyan.app.trustysnails.data.remote.api.AuthStatus
import risyan.app.trustysnails.data.remote.api.FbAuth
import risyan.app.trustysnails.data.remote.api.UserApi
import risyan.app.trustysnails.data.remote.model.FbUserLoginData
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