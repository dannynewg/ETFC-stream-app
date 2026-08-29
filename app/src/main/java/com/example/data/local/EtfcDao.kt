package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EtfcDao {

    // --- Offline Downloaded Matches ---
    @Query("SELECT * FROM downloaded_fights ORDER BY downloadTimestamp DESC")
    fun getAllDownloadedFights(): Flow<List<DownloadedFightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownloadedFight(fight: DownloadedFightEntity)

    @Query("UPDATE downloaded_fights SET downloadProgress = :progress, isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateDownloadProgress(id: String, progress: Float, isCompleted: Boolean)

    @Query("DELETE FROM downloaded_fights WHERE id = :id")
    suspend fun deleteDownloadedFight(id: String)

    @Query("SELECT * FROM downloaded_fights WHERE id = :id LIMIT 1")
    suspend fun getDownloadedFightById(id: String): DownloadedFightEntity?

    // --- PPV Passes & Tickets ---
    @Query("SELECT * FROM ppv_passes ORDER BY purchaseTimestamp DESC")
    fun getAllPpvPasses(): Flow<List<PpvPassEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPpvPass(pass: PpvPassEntity)

    @Query("SELECT * FROM ppv_passes WHERE status = 'ACTIVE' LIMIT 1")
    fun getActivePpvPass(): Flow<PpvPassEntity?>

    @Query("DELETE FROM ppv_passes WHERE passId = :passId")
    suspend fun deletePpvPass(passId: String)

    // --- Fan Predictions ---
    @Query("SELECT * FROM user_predictions")
    fun getAllPredictions(): Flow<List<UserPredictionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePrediction(prediction: UserPredictionEntity)

    // --- Favorite Fighters ---
    @Query("SELECT * FROM favorite_fighters")
    fun getAllFavoriteFighters(): Flow<List<FavoriteFighterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteFighter(favorite: FavoriteFighterEntity)

    @Query("DELETE FROM favorite_fighters WHERE fighterId = :fighterId")
    suspend fun removeFavoriteFighter(fighterId: String)

    // ==========================================
    // FIGHTER META-ENGINE DAO METHODS
    // ==========================================

    // Fighter Profiles
    @Query("SELECT * FROM fighter_profiles ORDER BY name ASC")
    fun getAllFighterProfiles(): Flow<List<FighterProfileEntity>>

    @Query("SELECT * FROM fighter_profiles WHERE fighterId = :fighterId LIMIT 1")
    fun getFighterProfileById(fighterId: String): Flow<FighterProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFighterProfile(profile: FighterProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFighterProfiles(profiles: List<FighterProfileEntity>)

    // Fighter Statistics
    @Query("SELECT * FROM fighter_statistics WHERE fighterId = :fighterId LIMIT 1")
    fun getFighterStatsById(fighterId: String): Flow<FighterStatsEntity?>

    @Query("SELECT * FROM fighter_statistics")
    fun getAllFighterStats(): Flow<List<FighterStatsEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFighterStats(stats: FighterStatsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFighterStats(statsList: List<FighterStatsEntity>)

    // Fight Record History (Win/Loss Log)
    @Query("SELECT * FROM fight_record_history WHERE fighterId = :fighterId ORDER BY orderIndex DESC")
    fun getFightRecordsForFighter(fighterId: String): Flow<List<FightRecordHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFightRecord(record: FightRecordHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFightRecords(records: List<FightRecordHistoryEntity>)

    // Weight Class History
    @Query("SELECT * FROM weight_class_history WHERE fighterId = :fighterId ORDER BY startDate DESC")
    fun getWeightClassHistoryForFighter(fighterId: String): Flow<List<WeightClassHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightClassHistory(history: WeightClassHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightClassHistories(histories: List<WeightClassHistoryEntity>)

    // ==========================================
    // USER ACCOUNTS & AUTHENTICATION (RBAC) DAO
    // ==========================================

    @Query("SELECT * FROM user_accounts ORDER BY createdTimestamp ASC")
    fun getAllUsers(): Flow<List<UserAccountEntity>>

    @Query("SELECT * FROM user_accounts WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE handle = :handle LIMIT 1")
    suspend fun getUserByHandle(handle: String): UserAccountEntity?

    @Query("SELECT * FROM user_accounts WHERE email = :identifier OR handle = :identifier LIMIT 1")
    suspend fun getUserByEmailOrHandle(identifier: String): UserAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccountEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsersIfNotExist(users: List<UserAccountEntity>)

    @Query("UPDATE user_accounts SET role = :newRole WHERE userId = :userId")
    suspend fun updateUserRole(userId: String, newRole: String)

    @Query("UPDATE user_accounts SET lastLoginTimestamp = :timestamp, authToken = :token WHERE userId = :userId")
    suspend fun updateLastLogin(userId: String, timestamp: Long, token: String)

    @Query("UPDATE user_accounts SET isBanned = :isBanned WHERE userId = :userId")
    suspend fun setUserBanned(userId: String, isBanned: Boolean)

    @Query("UPDATE user_accounts SET isMutedUntilTimestamp = :mutedUntil WHERE userId = :userId")
    suspend fun setUserMutedUntil(userId: String, mutedUntil: Long)

    // ==========================================
    // REAL-TIME LIVE CHAT & MODERATION DAO
    // ==========================================

    @Query("SELECT * FROM live_chat_messages WHERE boutId = :boutId ORDER BY timestamp ASC")
    fun getMessagesForBout(boutId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM live_chat_messages WHERE isPinned = 1 AND boutId = :boutId LIMIT 1")
    fun getPinnedMessageForBout(boutId: String): Flow<ChatMessageEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessages(messages: List<ChatMessageEntity>)

    @Query("UPDATE live_chat_messages SET isDeleted = 1, deletedByMod = :modHandle WHERE messageId = :messageId")
    suspend fun deleteChatMessage(messageId: String, modHandle: String)

    @Query("UPDATE live_chat_messages SET isPinned = :isPinned WHERE messageId = :messageId")
    suspend fun setMessagePinned(messageId: String, isPinned: Boolean)

    @Query("UPDATE live_chat_messages SET isPinned = 0 WHERE boutId = :boutId")
    suspend fun unpinAllMessagesForBout(boutId: String)

    @Query("UPDATE live_chat_messages SET flagCount = flagCount + 1 WHERE messageId = :messageId")
    suspend fun incrementFlagCount(messageId: String)

    @Query("DELETE FROM live_chat_messages WHERE userId = :userId")
    suspend fun purgeAllMessagesFromUser(userId: String)

    // Moderation Logs
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModerationLog(log: ModerationLogEntity)

    @Query("SELECT * FROM chat_moderation_logs ORDER BY timestamp DESC LIMIT 50")
    fun getRecentModerationLogs(): Flow<List<ModerationLogEntity>>
}

