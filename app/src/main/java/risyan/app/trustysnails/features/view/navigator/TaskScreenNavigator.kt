package com.example.tasklist.feature.navigator

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

    fun navigateToLogin() {
        nav.navigate(
            route = LOGIN_SCREEN
        )
    }

    fun navigateToRegister() {
        nav.navigate(
            route = REGISTER_SCREEN
        )
    }
}