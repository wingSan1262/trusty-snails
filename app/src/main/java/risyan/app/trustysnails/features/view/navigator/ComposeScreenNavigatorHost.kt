package risyan.app.trustysnails.features.view.navigator

import android.window.SplashScreen
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import com.example.tasklist.feature.navigator.TaskScreenNavigator
import com.example.tasklist.feature.screens.*

@Composable
fun TaskComposeNavigationHost(
    nav: NavHostController,
){
    val navigator = remember(nav){
        TaskScreenNavigator(nav)
    }

    NavHost(nav, startDestination = SPLASH_SCREEN){
        SplashScreen{}
        OnboardingScreen()
        RegisterScreen()
        RegisterScreen()
    }
}

val SPLASH_SCREEN by lazy { "SPLASH_SCREEN" }
val ONBOARDING_SCREEN by lazy { "ONBOARDING_SCREEN" }
val LOGIN_SCREEN by lazy { "LOGIN_SCREEN" }
val REGISTER_SCREEN by lazy { "REGISTER_SCREEN" }