package com.example.tasklist.feature.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.ui.theme.GRAY_CDD5EA
import risyan.app.trustysnails.basecomponent.ui.theme.LIGHT_BLUE_0033AA
import risyan.app.trustysnails.features.view.navigator.SPLASH_SCREEN

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun NavGraphBuilder.SplashScreen(
    navigateToNextScreen: () -> Unit
){
    composable(route = SPLASH_SCREEN){
        SplashContent(navigateToNextScreen)
    }
}

@Composable
fun SplashContent(
    navigateToNextScreen: () -> Unit
){
    LaunchedEffect(true) {
        delay(3000)
        navigateToNextScreen()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LIGHT_BLUE_0033AA), // Set the background color here
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){

        Image(
            painter = painterResource(id = R.drawable.image_splash), // Replace with your logo image
            contentDescription = null,
            modifier = Modifier.size(200.dp)
        )

        Text(
            text = "Your App Name",
            color = GRAY_CDD5EA,
            fontSize = 30.sp,
            modifier = Modifier.padding(top = 16.dp, bottom = 40.dp)
        )
    }
}


@Preview
@Composable
fun SplashScreenPreview() {
    SplashContent{}
}