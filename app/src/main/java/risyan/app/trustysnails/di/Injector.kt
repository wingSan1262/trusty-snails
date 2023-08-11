package risyan.app.trustysnails.di

import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import risyan.app.trustysnails.basecomponent.ViewModelFactory
import risyan.app.trustysnails.data.remote.api.FbAuth
import risyan.app.trustysnails.data.remote.api.FbAuthImpl
import risyan.app.trustysnails.data.remote.api.UserApi
import risyan.app.trustysnails.data.remote.api.UserApiImpl
import risyan.app.trustysnails.domain.usecase.auth.LoginUseCase
import risyan.app.trustysnails.domain.usecase.auth.LoginWithGoogleUseCase
import risyan.app.trustysnails.domain.usecase.auth.RegisterUseCase
import risyan.app.trustysnails.domain.usecase.auth.UnregisterUseCase
import risyan.app.trustysnails.domain.usecase.user.GetSettingUseCase
import risyan.app.trustysnails.domain.usecase.user.SetSettingUseCase
import risyan.app.trustysnails.features.MainActivity
import risyan.app.trustysnails.features.viewmodel.AuthViewModel
import risyan.app.trustysnails.features.viewmodel.UserViewModel

class Injector(
    owner : ComponentActivity
) {

    val fbAuth : FbAuth by lazy { FbAuthImpl(FirebaseAuth.getInstance()) }
    val loginUseCase by lazy { LoginUseCase(fbAuth)}
    val loginWithGoogleUseCase by lazy { LoginWithGoogleUseCase(fbAuth)}
    val registerUseCase by lazy { RegisterUseCase(fbAuth)}
    val unRegisterUseCase by lazy { UnregisterUseCase(fbAuth)}

    val userApi : UserApi by lazy { UserApiImpl(
        FirebaseAuth.getInstance()
    ) }
    val getSettingUseCase by lazy { GetSettingUseCase(userApi) }
    val setSettingUseCase by lazy { SetSettingUseCase(userApi) }

    val vmFactory by lazy {
        ViewModelFactory(
            loginUseCase, loginWithGoogleUseCase, registerUseCase, unRegisterUseCase,
            getSettingUseCase, setSettingUseCase
        )
    }


    fun inject(owner : MainActivity){
        owner.authViewModel = ViewModelProvider(owner, vmFactory).get(AuthViewModel::class.java)
        owner.userViewModel = ViewModelProvider(owner, vmFactory).get(UserViewModel::class.java)
    }
}