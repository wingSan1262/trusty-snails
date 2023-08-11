package risyan.app.trustysnails.domain.usecase.auth

import risyan.app.trustysnails.basecomponent.BaseUseCase
import risyan.app.trustysnails.data.remote.api.AuthStatus
import risyan.app.trustysnails.data.remote.api.FbAuth
import risyan.app.trustysnails.data.remote.model.FbUserLoginData

class LoginWithGoogleUseCase(
    val fbApi : FbAuth
): BaseUseCase<String, AuthStatus>() {
    override fun setup(parameter: String) {
        super.setup(parameter)
        execute {
            fbApi.loginWithGoogle(parameter)
        }
    }
}