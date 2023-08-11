package risyan.app.trustysnails.features

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import risyan.app.trustysnails.basecomponent.BaseActivity
import risyan.app.trustysnails.basecomponent.ui.theme.TrustysnailsTheme
import risyan.app.trustysnails.di.Inject
import risyan.app.trustysnails.di.Injector
import risyan.app.trustysnails.features.viewmodel.AuthViewModel
import risyan.app.trustysnails.features.viewmodel.UserViewModel

class MainActivity : BaseActivity() {

    @Inject
    lateinit var authViewModel: AuthViewModel
    @Inject
    lateinit var userViewModel: UserViewModel

    override fun inject(injector: Injector) { injector.inject(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrustysnailsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TrustysnailsTheme {
        Greeting("Android")
    }
}