package risyan.app.trustysnails.data.remote.api

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import risyan.app.trustysnails.data.remote.model.FbUserLoginData

interface FbAuth {
    suspend fun login(req : FbUserLoginData) : AuthStatus
    suspend fun loginWithGoogle(idToken: String) : AuthStatus
    suspend fun register(req : FbUserLoginData) : AuthStatus
    suspend fun deleteAccount(req : FbUserLoginData)
}

class FbAuthImpl(
    val firebaseAuth: FirebaseAuth
) : FbAuth {
    override suspend fun login(req: FbUserLoginData): AuthStatus {
        val response = firebaseAuth.signInWithEmailAndPassword(
            req.email, req.password
        ).await()
        return when (response.user?.isEmailVerified) {
            true ->
                AuthStatus.LogInSuccess(req)
            false ->
                AuthStatus.NeedEmailVerification(req)
            else -> throw IllegalArgumentException("isEmailVerification null")
        }
    }

    override suspend fun loginWithGoogle(idToken: String): AuthStatus {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val response = firebaseAuth.signInWithCredential(credential).await()
        return when (response.user?.isEmailVerified) {
            true ->
                AuthStatus.LogInSuccess(FbUserLoginData(response?.user?.email.toString(), ""))
            false ->
                AuthStatus.NeedEmailVerification(FbUserLoginData(response?.user?.email.toString(), ""))
            else -> throw IllegalArgumentException("isEmailVerification null")
        }
    }

    override suspend fun register(req: FbUserLoginData): AuthStatus {
        val response = firebaseAuth.createUserWithEmailAndPassword(
            req.email, req.password
        ).await()
        return when (response.user?.isEmailVerified) {
            true ->
                AuthStatus.RegisterSuccess(req)
            false ->
                AuthStatus.NeedEmailVerification(req)
            else -> throw IllegalArgumentException("isEmailVerification null")
        }
    }

    override suspend fun deleteAccount(req: FbUserLoginData) {
        firebaseAuth.currentUser!!.delete().await()
    }

}

sealed class AuthStatus {
    data class LogInSuccess(val userData : FbUserLoginData) : AuthStatus()
    data class NeedEmailVerification(val userData : FbUserLoginData) : AuthStatus()
    data class NeedRegister(val userData : FbUserLoginData) : AuthStatus()
    data class RegisterSuccess(val userData : FbUserLoginData) : AuthStatus()
    data class WrongLoginData(val userData : FbUserLoginData) : AuthStatus()
}