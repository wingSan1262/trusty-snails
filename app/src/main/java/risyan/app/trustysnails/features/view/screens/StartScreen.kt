package risyan.app.trustysnails.features.view.screens

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.google.android.gms.auth.api.identity.Identity
import risyan.app.trustysnails.R
import risyan.app.trustysnails.basecomponent.*
import risyan.app.trustysnails.basecomponent.ui.theme.BLUE_002989
import risyan.app.trustysnails.basecomponent.ui.theme.GRAY_757575
import risyan.app.trustysnails.data.remote.api.AuthStatus
import risyan.app.trustysnails.features.view.navigator.Screen
import risyan.app.trustysnails.features.viewmodel.AuthViewModel

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
fun NavGraphBuilder.OnboardingScreen(
    authViewModel: AuthViewModel,
    onAccountSettingFirst : ()->Unit,
    toBrowser: ()->Unit
){
    composable(route = Screen.START_SCREEN){

        val owner = LocalContext.current

        var isLoading by remember { mutableStateOf(false) }
        val loginState = authViewModel.loginData.observeAsState()

        val googleSignInLauncher = ComposeIntentSenderLauncher{
            it?.let { it1 ->
                val token = Identity.getSignInClient(owner as ComponentActivity)
                    .getSignInCredentialFromIntent(it1).googleIdToken
                authViewModel.loginByGoogle(token ?: ""); isLoading = true
            }
        }

        OnboardingContent(isLoading){
            (owner as ComponentActivity).doGoogleOneTapSignIn(googleSignInLauncher) }

        ResourceEffect(
            loginState,
            {
                isLoading = false
                if(it.body is AuthStatus.NewUserFromGoogle)
                    onAccountSettingFirst()
                else
                    toBrowser()
            }
        ){ data, owner ->
            isLoading = false
            owner.showToast(data.exception.message.toString())
        }
    }


}

@Composable
fun OnboardingContent(
    isLoading: Boolean,
    loginWithGoogle: () -> Unit,
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
        Spacer(modifier = Modifier.height(48.dp))
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = loginWithGoogle,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = BLUE_002989),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp),
            enabled = !isLoading
        ) {
            Row(
                Modifier.padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_google_image), // Replace with your Google icon resource
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Sign in with Google", color = Color.White, fontSize = 18.sp)
                if(isLoading){
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(
                        Modifier
                            .size(18.dp)
                            .padding(top = 1.dp),
                        color = Color.White)
                }

            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Push "Click here" to the bottom
    }
}

@Composable
@Preview(backgroundColor = 0xFFFFFF)
fun OnbordingContentPreview(){
    OnboardingContent(false){}
}