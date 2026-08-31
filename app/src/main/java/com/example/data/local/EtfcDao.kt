package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
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

    @Query("SELECT * FROM fighter_profiles WHERE fighterId = :fighterId LIMIT 1")
    suspend fun getFighterProfileDirect(fighterId: String): FighterProfileEntity?

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

    @Query("DELETE FROM user_accounts WHERE email NOT IN ('dannynewg@gmail.com') AND userId NOT LIKE 'usr_admin_%'")
    suspend fun purgeDemoUsers()

    @Query("DELETE FROM user_accounts WHERE userId = :userId")
    suspend fun deleteUser(userId: String)

    @Query("UPDATE user_accounts SET role = :newRole WHERE userId = :userId")
    suspend fun updateUserRole(userId: String, newRole: String)

    @Query("UPDATE user_accounts SET displayName = :displayName, handle = :handle, phoneNumber = :phoneNumber, countryCode = :countryCode, flagEmoji = :flagEmoji WHERE userId = :userId")
    suspend fun updateUserDetails(userId: String, displayName: String, handle: String, phoneNumber: String, countryCode: String, flagEmoji: String)

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

    // ==========================================
    // HERO SLIDES & MATCH POSTERS DAO
    // ==========================================

    @Query("SELECT * FROM hero_slides ORDER BY isPinned DESC, displayOrder ASC")
    fun getAllHeroSlides(): Flow<List<HeroSlideEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeroSlide(slide: HeroSlideEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeroSlides(slides: List<HeroSlideEntity>)

    @Query("DELETE FROM hero_slides WHERE id = :id")
    suspend fun deleteHeroSlide(id: String)

    @Query("DELETE FROM hero_slides")
    suspend fun clearAllHeroSlides()

    // ==========================================
    // YOUTUBE VIDEOS & VOD ARCHIVE DAO
    // ==========================================

    @Query("SELECT * FROM previous_fight_videos ORDER BY displayOrder ASC")
    fun getAllVideos(): Flow<List<PreviousVideoEntity>>

    @Query("SELECT * FROM previous_fight_videos ORDER BY displayOrder ASC")
    fun getAllPreviousVideos(): Flow<List<PreviousVideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: PreviousVideoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideos(videos: List<PreviousVideoEntity>)

    @Query("DELETE FROM previous_fight_videos WHERE id = :id")
    suspend fun deleteVideo(id: String)

    @Query("DELETE FROM previous_fight_videos")
    suspend fun clearAllVideos()

    // ==========================================
    // NEWS & ANNOUNCEMENTS DAO
    // ==========================================

    @Query("SELECT * FROM news_announcements ORDER BY isFeatured DESC, timestamp DESC")
    fun getAllNewsArticles(): Flow<List<NewsArticleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsArticle(article: NewsArticleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewsArticles(articles: List<NewsArticleEntity>)

    @Query("DELETE FROM news_announcements WHERE id = :id")
    suspend fun deleteNewsArticle(id: String)

    @Query("DELETE FROM news_announcements")
    suspend fun clearAllNewsArticles()

    // Fighter updates
    @Query("UPDATE fighter_profiles SET photoUrl = :photoUrl WHERE fighterId = :fighterId")
    suspend fun updateFighterPhoto(fighterId: String, photoUrl: String)

    @Query("UPDATE fighter_profiles SET bio = :bio, stance = :stance, signatureMove = :signatureMove, reachInches = :reachInches, heightCm = :heightCm WHERE fighterId = :fighterId")
    suspend fun updateFighterDetails(fighterId: String, bio: String, stance: String, signatureMove: String, reachInches: Int, heightCm: Int)

    @Update
    suspend fun updateFighterProfile(profile: FighterProfileEntity)

    @Delete
    suspend fun deleteFighterProfile(profile: FighterProfileEntity)

    @Query("DELETE FROM fighter_profiles WHERE fighterId = :fighterId")
    suspend fun deleteFighterProfileById(fighterId: String)

    @Update
    suspend fun updateHeroSlide(slide: HeroSlideEntity)

    @Delete
    suspend fun deleteHeroSlide(slide: HeroSlideEntity)

    @Update
    suspend fun updatePreviousVideo(video: PreviousVideoEntity)

    @Delete
    suspend fun deletePreviousVideo(video: PreviousVideoEntity)

    @Update
    suspend fun updateNewsArticle(article: NewsArticleEntity)

    @Delete
    suspend fun deleteNewsArticle(article: NewsArticleEntity)

    // ==========================================
    // OFFLINE SYNC ENGINE DAO (Fighter, Match, Announcement)
    // ==========================================

    // Core Fighters
    @Query("SELECT * FROM fighters ORDER BY rank ASC, wins DESC")
    fun getAllFighters(): Flow<List<FighterEntity>>

    @Query("SELECT * FROM fighters WHERE id = :id LIMIT 1")
    fun getFighterById(id: String): Flow<FighterEntity?>

    @Query("SELECT * FROM fighters WHERE id = :id LIMIT 1")
    suspend fun getFighterDirect(id: String): FighterEntity?

    @Query("SELECT * FROM fighters WHERE division = :division ORDER BY rank ASC")
    fun getFightersByDivision(division: String): Flow<List<FighterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFighter(fighter: FighterEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFighters(fighters: List<FighterEntity>)

    @Update
    suspend fun updateFighter(fighter: FighterEntity)

    @Delete
    suspend fun deleteFighterEntity(fighter: FighterEntity)

    @Query("DELETE FROM fighters WHERE id = :id")
    suspend fun deleteFighter(id: String)

    @Query("DELETE FROM fighters")
    suspend fun clearAllFighters()

    // Core Matches
    @Query("SELECT * FROM matches ORDER BY displayOrder ASC, lastUpdated DESC")
    fun getAllMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE isMainEvent = 1 OR isTitleFight = 1 OR isCoMain = 1 ORDER BY displayOrder ASC")
    fun getFeaturedMatches(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE id = :id LIMIT 1")
    fun getMatchById(id: String): Flow<MatchEntity?>

    @Query("SELECT * FROM matches WHERE status = :status ORDER BY displayOrder ASC")
    fun getMatchesByStatus(status: String): Flow<List<MatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatch(match: MatchEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMatches(matches: List<MatchEntity>)

    @Update
    suspend fun updateMatch(match: MatchEntity)

    @Delete
    suspend fun deleteMatchEntity(match: MatchEntity)

    @Query("DELETE FROM matches WHERE id = :id")
    suspend fun deleteMatch(id: String)

    @Query("DELETE FROM matches")
    suspend fun clearAllMatches()

    @Query("SELECT * FROM fighters")
    suspend fun getAllFightersDirect(): List<FighterEntity>

    @Query("SELECT * FROM fighter_profiles")
    suspend fun getAllFighterProfilesDirect(): List<FighterProfileEntity>

    @Query("SELECT * FROM matches")
    suspend fun getAllMatchesDirect(): List<MatchEntity>

    @Query("SELECT * FROM matches WHERE redFighterId = :fighterId OR blueFighterId = :fighterId OR redFighterName = :fighterName OR blueFighterName = :fighterName")
    suspend fun getMatchesForFighter(fighterId: String, fighterName: String): List<MatchEntity>

    @Query("SELECT * FROM hero_slides")
    suspend fun getAllHeroSlidesDirect(): List<HeroSlideEntity>

    @Query("SELECT * FROM previous_fight_videos")
    suspend fun getAllVideosDirect(): List<PreviousVideoEntity>

    @Query("SELECT * FROM news_announcements")
    suspend fun getAllNewsArticlesDirect(): List<NewsArticleEntity>

    // Core Announcements
    @Query("SELECT * FROM announcements ORDER BY isFeatured DESC, timestamp DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE isFeatured = 1 ORDER BY timestamp DESC")
    fun getFeaturedAnnouncements(): Flow<List<AnnouncementEntity>>

    @Query("SELECT * FROM announcements WHERE id = :id LIMIT 1")
    fun getAnnouncementById(id: String): Flow<AnnouncementEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: AnnouncementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncements(announcements: List<AnnouncementEntity>)

    @Update
    suspend fun updateAnnouncement(announcement: AnnouncementEntity)

    @Delete
    suspend fun deleteAnnouncementEntity(announcement: AnnouncementEntity)

    @Query("DELETE FROM announcements WHERE id = :id")
    suspend fun deleteAnnouncement(id: String)

    @Query("DELETE FROM announcements")
    suspend fun clearAllAnnouncements()
}

