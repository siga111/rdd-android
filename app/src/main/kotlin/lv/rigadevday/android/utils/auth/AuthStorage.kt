package lv.rigadevday.android.utils.auth

import com.google.firebase.auth.FirebaseAuth
import java.lang.Exception
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthStorage @Inject constructor() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    val hasLogin: Boolean
        get() = auth.currentUser != null

    val uId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("all bad")

    val email: String
        get() = auth.currentUser?.email ?: throw IllegalStateException("all bad")

    fun signOut() {
        auth.signOut()
    }

}
