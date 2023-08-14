package risyan.app.trustysnails.basecomponent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Build.VERSION.SDK_INT
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.ComponentRegistry
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.CoroutineScope
import risyan.app.trustysnails.R

fun Context.showToast(msg : String){
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

@Composable
fun <Model> ResourceEffect(
    state : State<Event<ResourceState<Model>>?>,
    onSuccess: suspend CoroutineScope.(data : ResourceState.Success<Model>)-> Unit = {},
    onFail: suspend CoroutineScope.(data : ResourceState.Failure<Model>
                                    , owner: ComponentActivity)-> Unit = {d,own->},
){
    val owner = LocalContext.current
    state.value?.run{
        val dataObserve = this.nonFilteredContent()
        LaunchedEffect(dataObserve) {
            when(dataObserve){
                is ResourceState.Success -> {
                    onSuccess(dataObserve)
                }
                is ResourceState.Failure -> {
                    onFail(dataObserve, owner as ComponentActivity)
                }
            }
        }
    }
}

@Composable
fun <Model> EventEffect(
    state : State<Event<ResourceState<Model>>?>,
    onSuccess: suspend CoroutineScope.(data : ResourceState.Success<Model>)-> Unit = {},
    onFail: suspend CoroutineScope.(data : ResourceState.Failure<Model>, owner: ComponentActivity)-> Unit = {d,own->},
){
    val owner = LocalContext.current
    state.value?.run{
        val dataObserve = this.contentIfNotHandled
        LaunchedEffect(dataObserve) {
            when(dataObserve){
                is ResourceState.Success -> {
                    onSuccess(dataObserve)
                }
                is ResourceState.Failure -> {
                    onFail(dataObserve, owner as ComponentActivity)
                }
            }
        }
    }
}

@Composable
fun showToastCompose(msg: String){
    val context = LocalContext.current
    context.showToast(msg)
}


fun <Content> Event<ResourceState<Content>>.getBareContent() : Content? {
    this.nonFilteredContent().run {
        when(this){
            is ResourceState.Success -> {
                return this.body
            }
            is ResourceState.Failure -> {
                return this.body
            }
        }
    }
}

@Composable
fun ComposeActivityLauncher(
    onSuccess: (Intent?)->Unit ={},
    onFail: ()->Unit = {},
    onFinish: (Intent?)->Unit ={},
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSuccess(result.data)
        } else {
            onFail()
        }
        onFinish(result.data)
    }
}

@Composable
fun ComposeIntentSenderLauncher(
    onSuccess: (Intent?)->Unit ={},
    onFail: ()->Unit = {},
    onFinish: (Intent?)->Unit ={},
): ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSuccess(result.data)
        } else {
            onFail()
        }
        onFinish(result.data)
    }
}

fun Int.getTimeAddZero() : String{
    return if(this > 9) this.toString() else "0$this"
}

fun getGoogleSignInContent(): BeginSignInRequest {
    return BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                // Your server's client ID, not your Android client ID.
                .setServerClientId("259359098883-4rta1omc1ufnejalo3kdobo1o3hj5bbe.apps.googleusercontent.com")
                // Only show accounts previously used to sign in.
                .setFilterByAuthorizedAccounts(false)
                .build())
        .build()
}

fun ComponentActivity.doGoogleOneTapSignIn(
    launcher : ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>
) {
    Identity.getSignInClient(this)
        .beginSignIn(getGoogleSignInContent())
        .addOnSuccessListener(this) { result ->
            launcher.launch(
                IntentSenderRequest
                    .Builder(result.pendingIntent.intentSender)
                    .build())
        }
        .addOnFailureListener(this) { e ->
            e.localizedMessage?.let { showToast(it.toString()) }
        }
}



@Composable
fun GifDisplay(
    gifResource : Int,
    size : Size,
    modifier: Modifier
) {
    val context = LocalContext.current
    val loader = ImageLoader(context).newBuilder()
        .components(fun ComponentRegistry.Builder.() {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }).build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = gifResource).apply(block = {
                size(size)
            }).build(), imageLoader = loader
        ),
        contentDescription = null,
        modifier = modifier,
    )
}