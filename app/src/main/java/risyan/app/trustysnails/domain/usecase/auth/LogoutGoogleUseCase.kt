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

class LogoutGoogleUseCase(
    val fbApi : FbAuth
): BaseUseCase<Any?, Any?>() {
    override fun setup(parameter: Any?) {
        super.setup(parameter)
        execute {
            fbApi.logout()
        }
    }
}