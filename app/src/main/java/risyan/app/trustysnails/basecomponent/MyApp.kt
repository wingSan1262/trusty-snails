package risyan.app.trustysnails.basecomponent

import android.app.Application
import androidx.room.Room
import risyan.app.trustysnails.data.remote.api.room.AppDatabase

class MyApp : Application() {
    companion object {
        lateinit var database: AppDatabase
    }

    override fun onCreate() {
        super.onCreate()

        // Initialize Room database
        database = Room.databaseBuilder(this, AppDatabase::class.java, "myapp-db")
            .build()
    }
}