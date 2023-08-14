package risyan.app.trustysnails.features.view.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import risyan.app.trustysnails.basecomponent.ResourceEffect
import risyan.app.trustysnails.basecomponent.showToast
import risyan.app.trustysnails.data.remote.model.BrowsingMode
import risyan.app.trustysnails.features.view.screen.BrowserScreen
import risyan.app.trustysnails.features.view.screens.OnboardingScreen
import risyan.app.trustysnails.features.view.screens.SettingScreen
import risyan.app.trustysnails.features.view.screens.SplashScreen
import risyan.app.trustysnails.features.viewmodel.AuthViewModel
import risyan.app.trustysnails.features.viewmodel.UserViewModel

@Composable
fun TaskComposeNavigationHost(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    nav: NavHostController,
){
    val navigator = remember(nav){
        TaskScreenNavigator(nav)
    }

    val userSettingState = userViewModel.getSettingData.observeAsState()

    NavHost(nav, startDestination = SPLASH_SCREEN){
        SplashScreen{
            userViewModel.getSetting()
        }
        OnboardingScreen( authViewModel,{
            navigator.navigateToSetting(true)
        }){
            userViewModel.getSetting()
        }
        SettingScreen(userViewModel){

        }
        BrowserScreen(userViewModel)

    }

    // TODO bad !! tight coupling . . . not reactive . . .
    ResourceEffect(
        userSettingState,{

            if(FirebaseAuth.getInstance().currentUser == null)
                navigator.navigateToOnboarding(true)

            if(it.body.browsingMode == BrowsingMode.CLEAN_MODE){
                if(it.body.cleanFilterList.isEmpty()){
                    navigator.navigateToSetting(true)
                    return@ResourceEffect
                }
                navigator.navigateToBrowserScreen(true)
            }

            if(it.body.browsingMode == BrowsingMode.ONE_BY_ONE){
                if(it.body.oneByOneList.isEmpty()){
                    navigator.navigateToSetting(true)
                    return@ResourceEffect
                }
                navigator.navigateToBrowserScreen(true)
            }
        }
    ){ data, owner ->
        if(FirebaseAuth.getInstance().currentUser == null)
            navigator.navigateToOnboarding(true)
        else
            navigator.navigateToSetting(true)
    }
}

val SPLASH_SCREEN by lazy { "SPLASH_SCREEN" }
val ONBOARDING_SCREEN by lazy { "START_SCREEN" }
val SETTING_SCREEN by lazy { "SETTING_SCREEN" }
val BROWSER_SCREEN by lazy { "BROWSER_SCREEN" }