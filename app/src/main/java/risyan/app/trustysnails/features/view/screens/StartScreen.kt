package com.example.tasklist.feature.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.ui.theme.BLUE_002989
import risyan.app.trustysnails.basecomponent.ui.theme.GRAY_757575
import risyan.app.trustysnails.features.view.navigator.ONBOARDING_SCREEN

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun NavGraphBuilder.OnboardingScreen(
){
    composable(route = ONBOARDING_SCREEN){

    }
}

@Composable
fun OnboardingContent(
    loginManual : ()->Unit,
    loginWithGoogle : ()->Unit,
    register : ()->Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header Title
        Text(
            text = "Trusty Snails",
            fontWeight = FontWeight.W400,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 80.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Image
        Image(
            painter = painterResource(id = R.drawable.image_splash), // Replace with your image resource
            contentDescription = "illustration",
            modifier = Modifier
                .size(128.dp),
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Masuk untuk mulai pencarian luring bebas konten berbahaya. Kamu dapat memfilter situs manapun yang berbahaya",
            fontSize = 14.sp,
            color = GRAY_757575,
            modifier = Modifier.padding(horizontal = 60.dp),
            textAlign = TextAlign.Center
        )
//
        Spacer(modifier = Modifier.height(48.dp))
//
//        // Rounded Rectangle Button
        Button(
            onClick = loginManual,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = BLUE_002989),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            Text(text = "Masuk", color = Color.White, fontSize = 18.sp)
        }
//
        Spacer(modifier = Modifier.height(16.dp))
//
//        // Sign in with Google Button
        Button(
            onClick = loginWithGoogle,
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_image), // Replace with your Google icon resource
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Sign in with Google", color = GRAY_757575, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Push "Click here" to the bottom

        // "Don't have an account? Click here!" text
        ClickableText(
            text = buildAnnotatedString {
                append("Don't have an account? ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append("Click here")
                }
                append("!")
            },
            onClick = { (register()) },
            modifier = Modifier.padding(bottom = 40.dp)
        )
    }
}

@Composable
@Preview(backgroundColor = 0xFFFFFF)
fun OnbordingContentPreview(){
    OnboardingContent(loginManual = { /*TODO*/ }, {}) {

    }
}