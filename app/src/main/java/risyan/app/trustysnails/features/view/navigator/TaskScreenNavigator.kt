package risyan.app.trustysnails.features.view.navigator

import androidx.navigation.NavHostController

class TaskScreenNavigator(
    val nav : NavHostController
){

    fun navigateToOnboarding (isPop : Boolean = false) {
        nav.navigate(
            route = Screen.START_SCREEN
        ){ if(isPop) popUpTo(Screen.START_SCREEN) }
    }

    fun navigateToSetting(isPop : Boolean = false) {
        nav.navigate(
            route = Screen.SETTING_SCREEN
        ){ if(isPop) popUpTo(Screen.SETTING_SCREEN) }
    }

    fun navigateToBrowserScreen(isPop : Boolean = false) {
        nav.navigate(
            route = Screen.BROWSER_SCREEN
        ){ if(isPop) popUpTo(Screen.BROWSER_SCREEN) }
    }
}