package risyan.app.trustysnails.basecomponent

import android.os.Bundle
import androidx.activity.ComponentActivity
import risyan.app.trustysnails.di.Injector

abstract class BaseActivity : ComponentActivity() {

    val injector : Injector by lazy {
        Injector(this)
    }

    abstract fun inject(injector: Injector)

    override fun onCreate(savedInstanceState: Bundle?) {
        inject(injector)
        super.onCreate(savedInstanceState)
    }
}