package risyan.app.trustysnails.domain.usecase.user

import risyan.app.trustysnails.basecomponent.BaseUseCase
import risyan.app.trustysnails.data.remote.api.AuthStatus
import risyan.app.trustysnails.data.remote.api.FbAuth
import risyan.app.trustysnails.data.remote.api.UserApi
import risyan.app.trustysnails.data.remote.model.FbUserLoginData
import risyan.app.trustysnails.data.remote.model.UserSettingDto
import risyan.app.trustysnails.domain.model.UserSettingModel

class GetSettingUseCase(
    val userApi: UserApi
): BaseUseCase<Any?, UserSettingModel>() {
    override fun setup(parameter: Any?) {
        super.setup(parameter)
        execute {
            userApi.getUserData()
        }
    }
}

class SetSettingUseCase(
    val userApi: UserApi
): BaseUseCase<UserSettingModel, Boolean>() {
    override fun setup(parameter: UserSettingModel) {
        super.setup(parameter)
        execute {
            userApi.setUserData(parameter.toSettingRequest())
        }
    }
}
