package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.local.EtfcDao
import com.example.data.local.UserAccountEntity
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * UserDataManager:
 * Manages Firebase Firestore 'users' collection schema:
 * - userId: String
 * - email: String
 * - role: String ("admin" or "user")
 * - displayName: String
 * - handle: String
 * - phoneNumber: String
 * - countryCode: String
 * - flagEmoji: String
 * - createdTimestamp: Long
 * - lastLoginTimestamp: Long
 * - isBanned: Boolean
 * - isMutedUntilTimestamp: Long
 *
 * Provides methods to:
 * - Fetch current user's role on login from Firestore
 * - Observe user role updates in real-time
 * - Sync user profile to Firestore
 * - Update user role in Firestore ('users' collection & 'admin_invites')
 * - Invite other users to become admins by email
 */
class UserDataManager(
    private val context: Context,
    private val dao: EtfcDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private const val TAG = "UserDataManager"
        const val COLL_USERS = "users"
        const val COLL_ADMIN_INVITES = "admin_invites"
        const val SUPERADMIN_EMAIL = "dannynewg@gmail.com"
    }

    private var currentUserListener: ListenerRegistration? = null
    private var allUsersListener: ListenerRegistration? = null

    private val _currentUserRole = MutableStateFlow(UserRole.USER)
    val currentUserRole: StateFlow<UserRole> = _currentUserRole.asStateFlow()

    private val _firestoreUsers = MutableStateFlow<List<UserAccount>>(emptyList())
    val firestoreUsers: StateFlow<List<UserAccount>> = _firestoreUsers.asStateFlow()

    init {
        startUsersSnapshotListener()
    }

    /**
     * Realtime snapshot listener on the 'users' collection to keep local state and cache updated.
     */
    private fun startUsersSnapshotListener() {
        try {
            allUsersListener?.remove()
            allUsersListener = firestore.collection(COLL_USERS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Users collection listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    snapshot?.let { querySnap ->
                        coroutineScope.launch(Dispatchers.IO) {
                            val userList = mutableListOf<UserAccount>()
                            for (doc in querySnap.documents) {
                                try {
                                    val data = doc.data ?: continue
                                    val userId = data["userId"] as? String ?: doc.id
                                    val email = data["email"] as? String ?: ""
                                    val roleStr = (data["role"] as? String)?.lowercase() ?: "user"
                                    val isAdmin = email.equals(SUPERADMIN_EMAIL, ignoreCase = true) || roleStr == "admin"
                                    val role = if (isAdmin) UserRole.ADMIN else UserRole.USER
                                    val displayName = data["displayName"] as? String ?: "ETFC Fan"
                                    val handle = data["handle"] as? String ?: "@fan"
                                    val phoneNumber = data["phoneNumber"] as? String ?: ""
                                    val countryCode = data["countryCode"] as? String ?: "+251"
                                    val flagEmoji = data["flagEmoji"] as? String ?: "🇪🇹"
                                    val createdTs = (data["createdTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                                    val lastLoginTs = (data["lastLoginTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                                    val isBanned = data["isBanned"] as? Boolean ?: false
                                    val isMutedUntil = (data["isMutedUntilTimestamp"] as? Number)?.toLong() ?: 0L

                                    val user = UserAccount(
                                        userId = userId,
                                        email = email,
                                        handle = handle,
                                        displayName = displayName,
                                        phoneNumber = phoneNumber,
                                        countryCode = countryCode,
                                        flagEmoji = flagEmoji,
                                        role = role,
                                        createdTimestamp = createdTs,
                                        lastLoginTimestamp = lastLoginTs,
                                        isBanned = isBanned,
                                        isMutedUntilTimestamp = isMutedUntil
                                    )
                                    userList.add(user)

                                    // Sync down to Room database entity
                                    if (email.isNotBlank()) {
                                        val existingEntity = dao.getUserByEmail(email)
                                        val roleName = if (isAdmin) UserRole.ADMIN.name else UserRole.USER.name
                                        if (existingEntity != null) {
                                            dao.updateUserRole(existingEntity.userId, roleName)
                                        } else {
                                            val newEntity = UserAccountEntity(
                                                userId = userId,
                                                email = email,
                                                handle = handle,
                                                displayName = displayName,
                                                phoneNumber = phoneNumber,
                                                countryCode = countryCode,
                                                flagEmoji = flagEmoji,
                                                role = roleName,
                                                passwordHash = "",
                                                authToken = "token_$userId",
                                                createdTimestamp = createdTs,
                                                lastLoginTimestamp = lastLoginTs,
                                                isBanned = isBanned,
                                                isMutedUntilTimestamp = isMutedUntil
                                            )
                                            dao.insertUser(newEntity)
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error mapping user document from Firestore", e)
                                }
                            }
                            _firestoreUsers.value = userList
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start users snapshot listener", e)
        }
    }

    private suspend fun ensureFirebaseAuthSession() {
        try {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
            }
        } catch (e: Exception) {
            Log.d(TAG, "Anonymous auth note: ${e.message}")
        }
    }

    /**
     * Fetches the user's role on login from the Firestore 'users' collection.
     * If user does not exist in Firestore, creates/syncs their record.
     */
    suspend fun fetchUserRoleOnLogin(userId: String, email: String): UserRole = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        // Superadmin bypass
        if (cleanEmail.equals(SUPERADMIN_EMAIL, ignoreCase = true)) {
            _currentUserRole.value = UserRole.ADMIN
            syncUserRoleToFirestore(userId, cleanEmail, "admin")
            return@withContext UserRole.ADMIN
        }

        try {
            ensureFirebaseAuthSession()
            // First check Firestore users collection by userId
            var doc = firestore.collection(COLL_USERS).document(userId).get().await()
            if (!doc.exists()) {
                // Check by email query
                val queryByEmail = firestore.collection(COLL_USERS)
                    .whereEqualTo("email", cleanEmail)
                    .limit(1)
                    .get()
                    .await()
                if (!queryByEmail.isEmpty) {
                    doc = queryByEmail.documents[0]
                }
            }

            if (doc.exists()) {
                val roleStr = (doc.getString("role"))?.lowercase() ?: "user"
                val role = if (roleStr == "admin") UserRole.ADMIN else UserRole.USER
                _currentUserRole.value = role

                // Listen to live role changes on this user's document
                attachUserDocumentListener(doc.id)
                return@withContext role
            }

            // Check admin_invites collection
            val inviteDoc = firestore.collection(COLL_ADMIN_INVITES).document(cleanEmail).get().await()
            if (inviteDoc.exists() && inviteDoc.getString("status") == "ACTIVE") {
                _currentUserRole.value = UserRole.ADMIN
                syncUserRoleToFirestore(userId, cleanEmail, "admin")
                attachUserDocumentListener(userId)
                return@withContext UserRole.ADMIN
            }

            // Default role is USER
            _currentUserRole.value = UserRole.USER
            syncUserRoleToFirestore(userId, cleanEmail, "user")
            attachUserDocumentListener(userId)
            return@withContext UserRole.USER
        } catch (e: Exception) {
            Log.w(TAG, "Notice fetching user role from Firestore: ${e.message}")
            // Fallback to email check
            val role = if (cleanEmail.equals(SUPERADMIN_EMAIL, ignoreCase = true)) UserRole.ADMIN else UserRole.USER
            _currentUserRole.value = role
            return@withContext role
        }
    }

    /**
     * Listens to real-time changes on the logged-in user's Firestore document.
     */
    fun attachUserDocumentListener(userDocId: String) {
        currentUserListener?.remove()
        try {
            currentUserListener = firestore.collection(COLL_USERS).document(userDocId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null || !snapshot.exists()) return@addSnapshotListener
                    val email = snapshot.getString("email") ?: ""
                    val roleStr = (snapshot.getString("role"))?.lowercase() ?: "user"
                    val isAdmin = email.equals(SUPERADMIN_EMAIL, ignoreCase = true) || roleStr == "admin"
                    val newRole = if (isAdmin) UserRole.ADMIN else UserRole.USER
                    _currentUserRole.value = newRole
                }
        } catch (e: Exception) {
            Log.w(TAG, "Notice attaching user document listener: ${e.message}")
        }
    }

    /**
     * Syncs full user profile into Firestore 'users' collection.
     */
    suspend fun syncUserProfileToFirestore(user: UserAccount) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val roleString = if (user.isAdmin || user.role == UserRole.ADMIN || user.email.equals(SUPERADMIN_EMAIL, ignoreCase = true)) "admin" else "user"
            val userMap = hashMapOf(
                "userId" to user.userId,
                "email" to user.email.trim().lowercase(),
                "role" to roleString,
                "displayName" to user.displayName,
                "handle" to user.handle,
                "phoneNumber" to user.phoneNumber,
                "countryCode" to user.countryCode,
                "flagEmoji" to user.flagEmoji,
                "createdTimestamp" to user.createdTimestamp,
                "lastLoginTimestamp" to user.lastLoginTimestamp,
                "isBanned" to user.isBanned,
                "isMutedUntilTimestamp" to user.isMutedUntilTimestamp,
                "updatedAt" to System.currentTimeMillis()
            )

            firestore.collection(COLL_USERS).document(user.userId).set(userMap, SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced user ${user.userId} (${user.email}) to Firestore 'users' collection")
        } catch (e: FirebaseFirestoreException) {
            Log.w(TAG, "Firestore write permission note (local storage active): ${e.message}")
        } catch (e: Exception) {
            Log.w(TAG, "Notice syncing user profile to Firestore: ${e.message}")
        }
    }

    /**
     * Updates a user's role field in Firestore 'users' collection.
     */
    suspend fun syncUserRoleToFirestore(userId: String, email: String, role: String) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val map = hashMapOf(
                "userId" to userId,
                "email" to email.trim().lowercase(),
                "role" to role.lowercase(),
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection(COLL_USERS).document(userId).set(map, SetOptions.merge()).await()
        } catch (e: FirebaseFirestoreException) {
            Log.w(TAG, "Firestore update role permission note: ${e.message}")
        } catch (e: Exception) {
            Log.w(TAG, "Notice updating user role in Firestore: ${e.message}")
        }
    }

    /**
     * Allows an authorized admin (e.g. dannynewg@gmail.com) to invite another user to become an admin
     * by inputting their email address.
     * Updates both 'admin_invites' and 'users' collections in Firestore.
     */
    suspend fun inviteAdminUserByEmail(
        targetEmail: String,
        invitedByEmail: String = SUPERADMIN_EMAIL
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val cleanEmail = targetEmail.trim().lowercase()
            if (!cleanEmail.contains("@")) {
                return@withContext Result.failure(IllegalArgumentException("Invalid email address"))
            }

            val timestamp = System.currentTimeMillis()

            // 1. Write to admin_invites collection
            val inviteMap = hashMapOf(
                "email" to cleanEmail,
                "invitedBy" to invitedByEmail,
                "timestamp" to timestamp,
                "status" to "ACTIVE"
            )
            firestore.collection(COLL_ADMIN_INVITES).document(cleanEmail)
                .set(inviteMap, SetOptions.merge()).await()

            // 2. Update role in 'users' collection for any existing user with this email
            val userDocs = firestore.collection(COLL_USERS)
                .whereEqualTo("email", cleanEmail)
                .get()
                .await()

            if (!userDocs.isEmpty) {
                for (doc in userDocs.documents) {
                    firestore.collection(COLL_USERS).document(doc.id)
                        .update("role", "admin", "updatedAt", timestamp).await()
                }
            } else {
                // Pre-create placeholder user document with role 'admin'
                val docId = "usr_${cleanEmail.replace("@", "_").replace(".", "_")}"
                val placeholderMap = hashMapOf(
                    "userId" to docId,
                    "email" to cleanEmail,
                    "role" to "admin",
                    "displayName" to cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                    "handle" to "@${cleanEmail.substringBefore("@")}",
                    "createdTimestamp" to timestamp,
                    "lastLoginTimestamp" to timestamp,
                    "isBanned" to false,
                    "isMutedUntilTimestamp" to 0L
                )
                firestore.collection(COLL_USERS).document(docId)
                    .set(placeholderMap, SetOptions.merge()).await()
            }

            // 3. Update local Room database if record exists
            val localUser = dao.getUserByEmail(cleanEmail)
            if (localUser != null) {
                dao.updateUserRole(localUser.userId, UserRole.ADMIN.name)
            }

            Log.d(TAG, "Successfully invited $cleanEmail as Admin in Firestore")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to invite admin user in Firestore", e)
            Result.failure(e)
        }
    }

    /**
     * Revokes an admin's role, setting them back to 'user' role in Firestore.
     */
    suspend fun revokeAdminRoleByEmail(targetEmail: String): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanEmail = targetEmail.trim().lowercase()
        if (cleanEmail.equals(SUPERADMIN_EMAIL, ignoreCase = true)) {
            return@withContext Result.failure(IllegalStateException("Cannot revoke superadmin owner"))
        }

        try {
            // Delete from admin_invites
            firestore.collection(COLL_ADMIN_INVITES).document(cleanEmail).delete().await()

            // Update role in 'users' collection to 'user'
            val userDocs = firestore.collection(COLL_USERS)
                .whereEqualTo("email", cleanEmail)
                .get()
                .await()

            for (doc in userDocs.documents) {
                firestore.collection(COLL_USERS).document(doc.id)
                    .update("role", "user", "updatedAt", System.currentTimeMillis()).await()
            }

            // Update local Room database
            val localUser = dao.getUserByEmail(cleanEmail)
            if (localUser != null) {
                dao.updateUserRole(localUser.userId, UserRole.USER.name)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to revoke admin role in Firestore", e)
            Result.failure(e)
        }
    }

    fun cleanUp() {
        currentUserListener?.remove()
        allUsersListener?.remove()
    }
}
