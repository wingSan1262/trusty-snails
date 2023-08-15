package risyan.app.trustysnails.basecomponent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.os.Build.VERSION.SDK_INT
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
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
    gifResource: Int,
    size: Size,
    modifier: Modifier
) {
    val context = LocalContext.current
    val loader = ImageLoader(context).newBuilder()
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

    val animationSpec = spring<IntOffset>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = animationSpec
        ) + fadeIn(initialAlpha = 0.3f),
        modifier = modifier
    ) {
        Image(
            painter = rememberAsyncImagePainter(
                ImageRequest.Builder(context).data(data = gifResource).apply {
                    size(size)
                }.build(), imageLoader = loader
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

fun ComponentActivity.createWebViewWithDefaults(
    onPageLoadFinished: () -> Unit = {},
    onPageStartLoad: (url: String) -> Unit = {},
): WebView {
    val webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            onPageLoadFinished()
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            url?.let {
                onPageStartLoad(it)
            }
        }
    }

    return WebView(this).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        webChromeClient = WebChromeClient()
        this.webViewClient = webViewClient
        settings.javaScriptEnabled = true
    }
}

fun String.extractDomain(): String {
    val startIndex = this.indexOf("://") + 3
    val endIndex = this.indexOf("/", startIndex)
    return if (endIndex != -1) {
        this.substring(startIndex, endIndex)
    } else {
        this.substring(startIndex)
    }
}

fun String.checkLinkValidity(): Boolean {
    val tldPattern = """\.(?:[a-zA-Z]{2,}|[0-9]{1,3})$""".toRegex()
    val pattern = """^(?:[a-zA-Z0-9\-]+\.)+[a-zA-Z]{2,}(?:\/.*)?$""".toRegex()

    val domain = this.extractDomain()
        .removePrefix("http://")
        .removePrefix("https://")

    return pattern.matches(domain) && tldPattern.find(domain)?.value != null && domain.count { it == '.' } >= 2
}


fun String.recheckValidityAndTransform(): String {
    val trimmed = trim()

    if (trimmed.isEmpty()) {
        return ""
    }

    // Check if the input starts with "http://" or "https://", and add if not present
    val httpPrefix = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
        "https://"
    } else {
        ""
    }

    // Check if the input ends with ".com", and add if not present
    val finalValue = if (trimmed.count { it == '.' } == 0) {
        "www.$trimmed.com"
    } else {
        trimmed
    }

    return "$httpPrefix$finalValue"
}

@Composable
fun <T, K> MediatorStateResource(
    vararg liveData: LiveData<Event<ResourceState<T>>>,
    mapper: (List<ResourceState<T>>) -> ResourceState<K>
): State<ResourceState<K>> {

    val states = liveData.map { state ->
        state.observeAsState().value?.nonFilteredContent()
    }

    return derivedStateOf {
        mapper(states.requireNoNulls())
    }
}

fun <T, K> ResourceState<T>.mapTo(
    mapper: (T) -> K
): ResourceState<K>{
    return when(this){
        is ResourceState.Success -> ResourceState.Success(mapper(this.body))
        is ResourceState.Failure ->
            ResourceState.Failure(
                exception,
                body = if(this.body == null) body else mapper(body)
            )
    }

}
