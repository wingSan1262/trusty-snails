package risyan.app.trustysnails.basecomponent

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.util.Log
import android.view.ViewGroup
import android.webkit.*
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
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import risyan.app.trustysnails.BuildConfig
import risyan.app.trustysnails.R
import risyan.app.trustysnails.data.remote.model.HistoryItem
import java.util.*
import java.util.regex.Pattern


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
        val dataObserve = this.bareContent()
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
    this.bareContent().run {
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
    onSuccess: (Intent?)->Unit = { },
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

@Composable
fun ImageUrl(
    url: String
){
    val painter = rememberImagePainter(
        data = url,
        builder = {
            placeholder(R.drawable.ic_default_browser) // Set your default image resource here
            error(R.drawable.ic_default_browser) // Set your error image resource here
        }
    )

    Image(
        painter = painter,
        contentDescription = "Image",
        modifier = Modifier.size(24.dp),
        contentScale = ContentScale.Fit
    )
}

fun getFileExtension(url: String): String {
    val lastSlashIndex = url.lastIndexOf('/')
    val lastDotIndex = url.lastIndexOf('.')
    if (lastDotIndex != -1 && lastDotIndex > lastSlashIndex) {
        return url.substring(lastDotIndex)
    }
    return ""
}

fun getFileName(url : String): String{
    val lastSlashIndex = url.lastIndexOf('/')
    val lastDotIndex = url.lastIndexOf('.')
    if (lastDotIndex != -1 && lastDotIndex > lastSlashIndex) {
        return url.substring(lastSlashIndex + 1, lastDotIndex)
    }
    return "downloadfile"
}

@SuppressLint("Range")
fun ComponentActivity.setupDebugDownloadManagerMonitor(){
    fun runChecking(){
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        val cursor = downloadManager.query(query)
        while (cursor.moveToNext()) {
            try {
                val downloadId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                when (status) {
                    DownloadManager.STATUS_PENDING -> {
                        Log.d("DownloadStatus", "Download $downloadId is pending")
                    }
                    DownloadManager.STATUS_RUNNING -> {
                        val totalSize = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        val downloadedBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val progress = if (totalSize > 0) (downloadedBytes * 100 / totalSize) else 0
                        Log.d("DownloadStatus", "Download $downloadId is running ($progress%)")
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        val localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
                        val fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                        Log.d("DownloadStatus", "Download $downloadId completed successfully. File: $fileName, Uri: $localUri")
                    }
                    DownloadManager.STATUS_FAILED -> {
                        val reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON))
                        val fileName = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TITLE))
                        Log.e("DownloadStatus", "Download $downloadId failed with reason code $reason. File: $fileName")
                    }
                }
            } catch (e: Exception){

            }
            // Handle the download status here
        }
        cursor.close()
    }
    lifecycleScope.launch {
        while(true){
            delay(2000)
            runChecking()
        }
    }
}

fun ComponentActivity.createWebViewWithDefaults(
    onPageLoadFinished: (HistoryItem) -> Unit = {},
    onPageStartLoad: (String) -> Unit = {},
    onOffline: () -> Unit = {},
    onDownloadLink: (fileLink: String) -> Unit = { },
    onFileChoose: (Intent, ValueCallback<Array<Uri>>?) -> Unit,
    onViewNewTab: (link: String) -> Unit
): WebView {

    if(BuildConfig.DEBUG){
        setupDebugDownloadManagerMonitor()
    }


    return WebView(this).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setupWebViewClient(
            onPageLoadFinished, onOffline, onDownloadLink, onViewNewTab, onPageStartLoad,
        )
        setupChromeClient(onFileChoose)
        setupWebViewBrowsingMode(); setupDownloadListener()
    }
}

fun WebView.setupChromeClient(
    onFileChoose : (Intent, ValueCallback<Array<Uri>>?)->Unit
) {
    webChromeClient = object : WebChromeClient(){
        override fun onPermissionRequest(request: PermissionRequest?) {
            val resources = request?.resources
            val permissions = mutableListOf<String>()

            if (resources?.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE) ?: true) {
                permissions.add(Manifest.permission.CAMERA)
            }

            if (resources?.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE) ?: true) {
                permissions.add(Manifest.permission.RECORD_AUDIO)
            }

            if (permissions.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    context as ComponentActivity,  // Replace with your Activity reference
                    permissions.toTypedArray(),
                    979
                )
            } else {
                request?.grant(resources)
            }
        }

        override fun onPermissionRequestCanceled(request: PermissionRequest?) {
            context.showToast("Permission failed ${request?.resources}")
        }

        override fun onShowFileChooser(
            webView: WebView?,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
        ): Boolean {
            if(fileChooserParams != null && filePathCallback != null){
                val intent = fileChooserParams.createIntent()
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.type = "*/*";
                onFileChoose(intent, filePathCallback)
                return true
            }
            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
        }
    }
}

fun String.isDownloadableFile(): Boolean {
    return !this.contains("html", true) ||
            !this.contains("css", true)
}

fun WebView.setupWebViewClient(
    onPageLoadFinished: (HistoryItem) -> Unit,
    onOffline: () -> Unit,
    onDownloadLink: (fileLink: String) -> Unit,
    onViewNewTab: (link: String) -> Unit,
    onPageStartLoad: (String) -> Unit
){

    this.webViewClient = object : WebViewClient() {
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            view?.title?.let {
                onPageLoadFinished(HistoryItem(title = it, url = url ?: ""))
            }
        }

        override fun onReceivedError(
            view: WebView?,
            errorCode: Int,
            description: String?,
            failingUrl: String?
        ) {
            super.onReceivedError(view, errorCode, description, failingUrl)
            if (errorCode == ERROR_HOST_LOOKUP || errorCode == ERROR_CONNECT || errorCode == ERROR_TIMEOUT)
                onOffline()
        }
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url = request?.url.toString()
            if(checkAndStartIfDeepLink(url)) return true
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            getProperUserAgent(
                url?.contains("google.com", true) ?: false ||
                        url?.contains("twitter.com", true) ?: false ||
                        url?.contains("instagram.com", true) ?: false
            )
            view?.setOnLongClickListener {
                val result = view.hitTestResult
                val urlView = result.extra ?: ""
                if(getFileExtension(urlView).isNotEmpty() &&
                    getFileExtension(urlView).isDownloadableFile()) {
                    onDownloadLink(urlView)
                    return@setOnLongClickListener true
                } else if(urlView.isNotEmpty()) {
                    onViewNewTab(urlView)
                }
                false
            }
            url?.let {
                onPageStartLoad(it)
            }
        }
    }
}

fun WebView.checkAndStartIfDeepLink(urlTarget : String): Boolean {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlTarget)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REQUIRE_NON_BROWSER
        }
        val packageManager = context.packageManager
        val activities = packageManager.queryIntentActivities(intent, 0)

        activities.forEach {
            if(it?.activityInfo?.targetActivity?.contains("chrome", true) == true)
                return false
        }
        context.startActivity(intent)
        return true
    } catch (e: ActivityNotFoundException) {
        return false
    }
    return true
}

fun WebView.setupWebViewBrowsingMode(){
    settings.javaScriptEnabled = true
    settings.domStorageEnabled = true
    settings.cacheMode = WebSettings.LOAD_DEFAULT
    getProperUserAgent(true)
    CookieManager.getInstance().setAcceptThirdPartyCookies(this, true);
    CookieManager.getInstance().setAcceptCookie(true);
}

fun WebView.getProperUserAgent(isGoogleSearch : Boolean) {
    val defaultUserAgent = System.getProperty("http.agent")
    val customAgent =
        ("Mozilla/5.0 (Linux; Android ${Build.VERSION.RELEASE}; " +
            "${Build.MODEL} Build/${System.getProperty("ro.build.version.incremental")}) " +
            "AppleWebKit/535.19 (KHTML, like Gecko) " +
            "Chrome/18.0.1025.133 " +
            "Mobile Safari/535.19")
    settings.userAgentString = if(isGoogleSearch) "" else customAgent
}

fun WebView.setupDownloadListener(){
    setDownloadListener { url, userAgent, contentDisposition, mimeType, contentLength ->
        var mimeTypeConvert = mimeType
        val fileName =
            if(mimeType.contains("octet", true))
                contentDisposition.getFileNameFromContentDisposition().also {
                    mimeTypeConvert = mimeTypeConvert.getFileExtensionFromFileName()
                }
            else
                URLUtil.guessFileName(url, contentDisposition, mimeTypeConvert)

        this.context.downloadByDownloadManager(url, fileName.toString(), "",
            mimeTypeConvert, userAgent,url.extractDomain())
    }
}



fun String.getFileExtensionFromFileName(): String {
    val lastDotIndex = lastIndexOf('.')
    return if (lastDotIndex != -1 && lastDotIndex < length - 1) {
        substring(lastDotIndex)
    } else {
        ""
    }
}

fun String.getFileNameFromContentDisposition(): String? {
    val pattern = Pattern.compile("filename=\"([^\"]+)\"")
    val matcher = pattern.matcher(this)
    if (matcher.find()) {
        return matcher.group(1)
    }
    return null
}

fun String.extractDomain(): String {
    return try {
        val startIndex = this.indexOf("://") + 3
        val endIndex = this.indexOf("/", startIndex)
        return if (endIndex != -1) {
            this.substring(startIndex, endIndex)
        } else {
            this.substring(startIndex)
        }
    } catch (e : Exception){
        "notvalid"
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

fun isTimeValid(): Boolean {
    val currentTime = Calendar.getInstance()
    val hour = currentTime.get(Calendar.HOUR_OF_DAY)
    val minute = currentTime.get(Calendar.MINUTE)

    return hour % 3 == 0 && minute >= 0 && minute <= 15
}

fun Context.downloadByDownloadManager(
    fileUrl: String,
    fileName: String,
    fileExtension: String,
    mimeType: String, userAgent: String,
    domain: String
){
    val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val request = DownloadManager.Request(Uri.parse(fileUrl)).apply {
        setTitle("Downloading $fileName$fileExtension")
        setDescription("Downloading $fileName$fileExtension from $domain")
        val cookies = CookieManager.getInstance().getCookie(fileUrl)
        addRequestHeader("cookie", cookies)
        allowScanningByMediaScanner();
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        if(mimeType.isNotEmpty()) setMimeType(mimeType)
        if(userAgent.isNotEmpty()) addRequestHeader("User-Agent", userAgent)
        setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "$fileName$fileExtension" // Use the extracted extension
        )
    }
    downloadManager.enqueue(request)
}

fun Context.downloadFile(fileUrl: String) {
    // Get the file extension from the URL
    val fileExtension = getFileExtension(fileUrl)
    val fileName = getFileName(fileUrl)
    val source = fileUrl.extractDomain()
    downloadByDownloadManager(fileUrl, fileName, fileExtension, "", "",source)
}

fun String.generateGoogleSearchUrl(): String {
    val baseUrl = "https://www.google.com/search"
    val encodedQuery = Uri.encode(this)
    return "$baseUrl?q=$encodedQuery"
}


