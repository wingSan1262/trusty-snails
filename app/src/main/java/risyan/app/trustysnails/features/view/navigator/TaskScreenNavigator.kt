package risyan.app.trustysnails.features.view.navigator

import androidx.navigation.NavHostController
import risyan.app.trustysnails.features.view.navigator.*

class TaskScreenNavigator(
    val nav : NavHostController
){

    fun navigateToOnboarding (isPop : Boolean = false) {
        nav.navigate(
            route = ONBOARDING_SCREEN
        ){ if(isPop) popUpTo(ONBOARDING_SCREEN) }
    }

    fun navigateToSetting(isPop : Boolean = false) {
        nav.navigate(
            route = SETTING_SCREEN
        ){ if(isPop) popUpTo(SETTING_SCREEN) }
    }

    fun navigateToBrowserScreen(isPop : Boolean = false) {
        nav.navigate(
            route = BROWSER_SCREEN
        ){ if(isPop) popUpTo(BROWSER_SCREEN) }
    }
}