package risyan.app.trustysnails.features.view.navigator

import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import risyan.app.trustysnails.basecomponent.ResourceEffect
import risyan.app.trustysnails.data.remote.model.BrowsingMode
import risyan.app.trustysnails.features.view.screen.BrowserScreen
import risyan.app.trustysnails.features.view.screens.HistoryScreen
import risyan.app.trustysnails.features.view.screens.OnboardingScreen
import risyan.app.trustysnails.features.view.screens.SettingScreen
import risyan.app.trustysnails.features.view.screens.SplashScreen
import risyan.app.trustysnails.features.viewmodel.AuthViewModel
import risyan.app.trustysnails.features.viewmodel.HistoryViewModel
import risyan.app.trustysnails.features.viewmodel.UserViewModel

@Composable
fun TaskComposeNavigationHost(
    authViewModel: AuthViewModel,
    userViewModel: UserViewModel,
    historyViewModel: HistoryViewModel,
    nav: NavHostController,
){
    val navigator = remember(nav){
        TaskScreenNavigator(nav)
    }

    val userSettingState = userViewModel.getSettingData.observeAsState()

    NavHost(nav, startDestination = Screen.SPLASH_SCREEN){
        SplashScreen{
            userViewModel.getSetting()
        }
        OnboardingScreen( authViewModel,{
            navigator.navigateToSetting(true)
        }){
            userViewModel.getSetting()
        }
        SettingScreen(userViewModel){
            navigator.navigateToBrowserScreen(true)
        }
        BrowserScreen(userViewModel, historyViewModel,
            {
                if(userViewModel.isOffline.value == false)
                    navigator.navigateToSetting(false)
            }
        ){ navigator.navigateToHistory(false) }
        HistoryScreen(historyViewModel){
            navigator.navigateToBrowserScreen(true)
        }
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

        if(data.exception.message.toString()
                .contains("client is offline", true)){
            userViewModel.setOffline(true)
            navigator.navigateToBrowserScreen(true)
            return@ResourceEffect
        }

        if(FirebaseAuth.getInstance().currentUser == null)
            navigator.navigateToOnboarding(true)
        else
            navigator.navigateToSetting(true)
    }
}

class Screen {
    companion object{
        val SPLASH_SCREEN by lazy { "SPLASH_SCREEN" }
        val START_SCREEN by lazy { "START_SCREEN" }
        val SETTING_SCREEN by lazy { "SETTING_SCREEN" }
        val BROWSER_SCREEN by lazy { "BROWSER_SCREEN" }
        val HISTORY_SCREEN by lazy { "HISTORY_SCREEN" }
    }
}