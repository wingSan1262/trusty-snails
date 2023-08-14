package risyan.app.trustysnails.data.remote.api

import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.utilities.Utilities
import com.google.firebase.installations.Utils
import kotlinx.coroutines.tasks.await
import risyan.app.trustysnails.basecomponent.doGoogleOneTapSignIn
import risyan.app.trustysnails.data.remote.model.FbUserLoginData
import risyan.app.trustysnails.data.remote.model.UserSettingDto
import risyan.app.trustysnails.domain.model.UserSettingModel
import java.net.URLEncoder

interface UserApi {
    suspend fun setUserData(req : UserSettingDto) : Boolean
    suspend fun getUserData() : UserSettingModel
}

class UserApiImpl(
    val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance(),
    private val usersReference: DatabaseReference = database.getReference("users")
) : UserApi {

    override suspend fun setUserData(req: UserSettingDto): Boolean {
        firebaseAuth.currentUser?.uid?.let { id ->
            val userReference = usersReference.child("email").child(id)
            userReference.setValue(req).await()
            return true
        } ?: kotlin.run {
            return false
        }
    }

    override suspend fun getUserData(): UserSettingModel {
        firebaseAuth.currentUser?.uid?.let { id ->
            val dataSnapshot = usersReference.child("email").child(id).get().await()
            val userData = dataSnapshot.getValue(UserSettingDto::class.java)
            return userData!!.toUserSettingModel()
        } ?: kotlin.run {
            throw IllegalArgumentException("id user not found")
        }
    }
}