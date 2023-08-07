package risyan.app.trustysnails.basecomponent

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope

fun Context.showToast(msg : String){
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
}

@Composable
fun <Model> ResourceEffect(
    state : State<Event<ResourceState<Model>>?>,
    onSuccess: suspend CoroutineScope.(data : ResourceState.Success<Model>)-> Unit = {},
    onFail: suspend CoroutineScope.(data : ResourceState.Failure<Model>)-> Unit = {},
){
    state.value?.run{
        val dataObserve = this.nonFilteredContent()
        LaunchedEffect(dataObserve) {
            when(dataObserve){
                is ResourceState.Success -> {
                    onSuccess(dataObserve)
                }
                is ResourceState.Failure -> {
                    onFail(dataObserve)
                }
            }
        }
    }
}

@Composable
fun <Model> EventEffect(
    state : State<Event<ResourceState<Model>>?>,
    onSuccess: suspend CoroutineScope.(data : ResourceState.Success<Model>)-> Unit = {},
    onFail: suspend CoroutineScope.(data : ResourceState.Failure<Model>)-> Unit = {},
){
    state.value?.run{
        val dataObserve = this.contentIfNotHandled
        LaunchedEffect(dataObserve) {
            when(dataObserve){
                is ResourceState.Success -> {
                    onSuccess(dataObserve)
                }
                is ResourceState.Failure -> {
                    onFail(dataObserve)
                }
            }
        }
    }
}

@Composable
fun showToastCompose(msg: String){
    val context = LocalContext.current as ComponentActivity
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

fun Int.getTimeAddZero() : String{
    return if(this > 9) this.toString() else "0$this"
}