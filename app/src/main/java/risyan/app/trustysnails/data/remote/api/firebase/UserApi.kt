package risyan.app.trustysnails.data.remote.api.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import risyan.app.trustysnails.data.remote.model.UserSettingDto
import risyan.app.trustysnails.domain.model.UserSettingModel

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