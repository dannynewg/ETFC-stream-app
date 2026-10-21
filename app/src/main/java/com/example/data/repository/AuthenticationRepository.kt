package com.example.data.repository

import android.util.Log
import com.example.data.models.AuthResult
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.example.data.remote.UserDataManager
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * AuthenticationRepository:
 * Encapsulates all Firebase Authentication operations (signIn, signUp, signOut, password reset)
 * and root-level authentication state lifecycle management.
 */
class AuthenticationRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val userDataManager: UserDataManager? = null,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    companion object {
        private const val TAG = "AuthRepository"
    }

    private val _currentFirebaseUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val authStateFlow: StateFlow<FirebaseUser?> = _currentFirebaseUser.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authErrorMessage = MutableStateFlow<String?>(null)
    val authErrorMessage: StateFlow<String?> = _authErrorMessage.asStateFlow()

    init {
        // Root Firebase Auth State Listener
        try {
            firebaseAuth.addAuthStateListener { auth ->
                val user = auth.currentUser
                _currentFirebaseUser.value = user
                Log.d(TAG, "FirebaseAuth state changed: uid=${user?.uid}, email=${user?.email}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register AuthStateListener", e)
        }
    }

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    val isUserLoggedIn: Boolean
        get() = firebaseAuth.currentUser != null

    /**
     * Sign In with Email and Password
     */
    suspend fun signIn(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return@withContext AuthResult.Error("Please enter a valid email address.")
        }
        if (password.isBlank()) {
            return@withContext AuthResult.Error("Please enter your password.")
        }

        _isAuthLoading.value = true
        _authErrorMessage.value = null

        try {
            val result = firebaseAuth.signInWithEmailAndPassword(cleanEmail, password).await()
            val user = result.user ?: return@withContext AuthResult.Error("Authentication succeeded but no user session returned.")

            // Sync with UserDataManager if present
            val role = userDataManager?.fetchUserRoleOnLogin(user.uid, cleanEmail) ?: UserRole.USER

            val account = UserAccount(
                userId = "usr_${user.uid.take(8)}",
                email = cleanEmail,
                handle = "@${cleanEmail.substringBefore("@").replace(".", "_")}",
                displayName = user.displayName ?: cleanEmail.substringBefore("@"),
                phoneNumber = user.phoneNumber ?: "",
                role = role,
                authToken = "ETFC.TOKEN.${user.uid.take(8)}"
            )

            _currentFirebaseUser.value = user
            _isAuthLoading.value = false
            return@withContext AuthResult.Success(account, "Welcome back, ${account.displayName}!")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            _isAuthLoading.value = false
            val msg = "Invalid email or password. Please verify your credentials."
            _authErrorMessage.value = msg
            return@withContext AuthResult.Error(msg)
        } catch (e: FirebaseAuthInvalidUserException) {
            _isAuthLoading.value = false
            val msg = "No account found with this email. Please sign up."
            _authErrorMessage.value = msg
            return@withContext AuthResult.Error(msg)
        } catch (e: Exception) {
            _isAuthLoading.value = false
            val msg = e.localizedMessage ?: "Sign in failed. Please check your internet connection."
            _authErrorMessage.value = msg
            Log.e(TAG, "Sign in failed", e)
            return@withContext AuthResult.Error(msg)
        }
    }

    /**
     * Sign Up with Email, Password, and Display Name
     */
    suspend fun signUp(
        email: String,
        password: String,
        displayName: String = "",
        handle: String = "",
        phoneNumber: String = ""
    ): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.isBlank() || !cleanEmail.contains("@")) {
            return@withContext AuthResult.Error("Please enter a valid email address.")
        }
        if (password.length < 6) {
            return@withContext AuthResult.Error("Password must be at least 6 characters.")
        }

        _isAuthLoading.value = true
        _authErrorMessage.value = null

        val resolvedName = displayName.trim().ifEmpty { cleanEmail.substringBefore("@") }
        val resolvedHandle = if (handle.isNotBlank()) {
            if (handle.startsWith("@")) handle.trim() else "@${handle.trim()}"
        } else {
            "@${cleanEmail.substringBefore("@").replace(".", "_")}"
        }

        try {
            val result = firebaseAuth.createUserWithEmailAndPassword(cleanEmail, password).await()
            val user = result.user ?: return@withContext AuthResult.Error("Registration succeeded but no user session created.")

            // Update user profile display name in Firebase Auth
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(resolvedName)
                .build()
            user.updateProfile(profileUpdates).await()

            // Initialize user role via UserDataManager
            val role = userDataManager?.fetchUserRoleOnLogin(user.uid, cleanEmail) ?: UserRole.USER

            val account = UserAccount(
                userId = "usr_${user.uid.take(8)}",
                email = cleanEmail,
                handle = resolvedHandle,
                displayName = resolvedName,
                phoneNumber = phoneNumber.ifEmpty { user.phoneNumber ?: "" },
                role = role,
                authToken = "ETFC.TOKEN.${user.uid.take(8)}"
            )

            // Push profile to Firestore users collection
            userDataManager?.syncUserProfile(account)

            _currentFirebaseUser.value = user
            _isAuthLoading.value = false
            return@withContext AuthResult.Success(account, "Account created successfully! Welcome to ETFC Arena, ${account.displayName}.")
        } catch (e: FirebaseAuthUserCollisionException) {
            _isAuthLoading.value = false
            val msg = "An account with this email address already exists. Please sign in."
            _authErrorMessage.value = msg
            return@withContext AuthResult.Error(msg)
        } catch (e: FirebaseAuthWeakPasswordException) {
            _isAuthLoading.value = false
            val msg = "Password is too weak: ${e.reason ?: "Please use at least 6 characters."}"
            _authErrorMessage.value = msg
            return@withContext AuthResult.Error(msg)
        } catch (e: Exception) {
            _isAuthLoading.value = false
            val msg = e.localizedMessage ?: "Registration failed. Please try again."
            _authErrorMessage.value = msg
            Log.e(TAG, "Sign up failed", e)
            return@withContext AuthResult.Error(msg)
        }
    }

    /**
     * Sign In with Firebase Auth Credential (Google, etc.)
     */
    suspend fun signInWithCredential(credential: AuthCredential): AuthResult = withContext(Dispatchers.IO) {
        _isAuthLoading.value = true
        _authErrorMessage.value = null

        try {
            val result = firebaseAuth.signInWithCredential(credential).await()
            val user = result.user ?: return@withContext AuthResult.Error("Credential sign in returned no user session.")
            val email = user.email?.lowercase() ?: ""
            val displayName = user.displayName ?: email.substringBefore("@").ifEmpty { "ETFC Fan" }

            val role = userDataManager?.fetchUserRoleOnLogin(user.uid, email) ?: UserRole.USER

            val account = UserAccount(
                userId = "usr_${user.uid.take(8)}",
                email = email,
                handle = "@${email.substringBefore("@").replace(".", "_").ifEmpty { "fan" }}",
                displayName = displayName,
                phoneNumber = user.phoneNumber ?: "",
                role = role,
                authToken = "ETFC.TOKEN.${user.uid.take(8)}"
            )

            userDataManager?.syncUserProfile(account)

            _currentFirebaseUser.value = user
            _isAuthLoading.value = false
            return@withContext AuthResult.Success(account, "Signed in successfully with Google!")
        } catch (e: Exception) {
            _isAuthLoading.value = false
            val msg = e.localizedMessage ?: "Google sign-in failed. Please try again."
            _authErrorMessage.value = msg
            Log.e(TAG, "Credential sign in failed", e)
            return@withContext AuthResult.Error(msg)
        }
    }

    /**
     * Send Password Reset Email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            firebaseAuth.sendPasswordResetEmail(email.trim().lowercase()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Password reset failed for $email", e)
            Result.failure(e)
        }
    }

    /**
     * Sign Out
     */
    fun signOut() {
        try {
            firebaseAuth.signOut()
            _currentFirebaseUser.value = null
            _authErrorMessage.value = null
            _isAuthLoading.value = false
            Log.d(TAG, "User signed out successfully from Firebase Auth")
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out from Firebase Auth", e)
        }
    }

    fun clearError() {
        _authErrorMessage.value = null
    }
}
