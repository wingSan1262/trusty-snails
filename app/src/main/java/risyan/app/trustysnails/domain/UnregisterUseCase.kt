package risyan.app.trustysnails.domain

import risyan.app.trustysnails.basecomponent.BaseUseCase
import risyan.app.trustysnails.data.remote.api.FbAuth
import risyan.app.trustysnails.data.remote.model.FbUserLoginData

class UnregisterUseCase(
    val fbApi : FbAuth
): BaseUseCase<FbUserLoginData, Unit>() {
    override fun setup(parameter: FbUserLoginData) {
        super.setup(parameter)
        execute {
            fbApi.deleteAccount(parameter)
        }
    }
}