package risyan.app.trustysnails.domain.usecase.auth

import risyan.app.trustysnails.basecomponent.BaseUseCase
import risyan.app.trustysnails.data.remote.api.firebase.FbAuth

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