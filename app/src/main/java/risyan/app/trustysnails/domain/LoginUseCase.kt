package risyan.app.trustysnails.domain

import risyan.app.trustysnails.basecomponent.BaseUseCase
import risyan.app.trustysnails.data.remote.api.AuthStatus
import risyan.app.trustysnails.data.remote.api.FbAuth
import risyan.app.trustysnails.data.remote.model.FbUserLoginData

class LoginUseCase(
    val fbApi : FbAuth
): BaseUseCase<FbUserLoginData, AuthStatus>() {
    override fun setup(parameter: FbUserLoginData) {
        super.setup(parameter)
        execute {
            fbApi.login(parameter)
        }
    }
}