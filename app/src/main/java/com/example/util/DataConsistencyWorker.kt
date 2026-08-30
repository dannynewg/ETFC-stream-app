package com.example.util

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.data.local.EtfcDatabase
import com.example.data.local.MatchEntity
import com.example.data.local.HeroSlideEntity
import com.example.data.local.PreviousVideoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DataConsistencyWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "DataConsistencyWorker"
        const val KEY_FIGHTER_ID = "key_fighter_id"
        const val ONE_TIME_WORK_NAME = "etfc_fighter_consistency_sync"
        const val PERIODIC_WORK_NAME = "etfc_periodic_data_consistency"

        /**
         * Enqueue a one-time consistency check worker to immediately update all
         * crosslinked matches, hero slides, and video archives after a fighter edit.
         */
        fun enqueue(context: Context, fighterId: String? = null) {
            val dataBuilder = if (fighterId != null) {
                workDataOf(KEY_FIGHTER_ID to fighterId)
            } else {
                workDataOf()
            }

            val request = OneTimeWorkRequestBuilder<DataConsistencyWorker>()
                .setInputData(dataBuilder)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                ONE_TIME_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
            Log.d(TAG, "Enqueued DataConsistencyWorker for fighter: $fighterId")
        }

        /**
         * Enqueue periodic background consistency checks.
         */
        fun schedulePeriodic(context: Context) {
            val periodicRequest = PeriodicWorkRequestBuilder<DataConsistencyWorker>(
                1, TimeUnit.HOURS
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicRequest
            )
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val targetFighterId = inputData.getString(KEY_FIGHTER_ID)
            Log.d(TAG, "Executing data consistency check. Target fighter ID: $targetFighterId")

            val dao = EtfcDatabase.getDatabase(applicationContext).etfcDao()
            val fighters = dao.getAllFightersDirect()
            val profiles = dao.getAllFighterProfilesDirect()

            val fighterMap = fighters.associateBy { it.id }
            val profileMap = profiles.associateBy { it.fighterId }

            // 1. Synchronize & Re-index all MatchEntities referencing updated fighters
            val allMatches = dao.getAllMatchesDirect()
            var matchesUpdated = 0

            for (match in allMatches) {
                var isModified = false
                var updatedMatch = match

                // Check Red Corner Fighter
                val redFighter = fighterMap[match.redFighterId] ?: fighterMap.values.find { it.name.equals(match.redFighterName, ignoreCase = true) }
                val redProfile = profileMap[match.redFighterId] ?: profileMap.values.find { it.name.equals(match.redFighterName, ignoreCase = true) }

                if (redFighter != null || redProfile != null) {
                    val newRedName = redProfile?.name ?: redFighter?.name ?: match.redFighterName
                    val newRedFlag = redProfile?.flagEmoji ?: redFighter?.flagEmoji ?: match.redFighterFlag
                    val newRedPhoto = redProfile?.photoUrl ?: redFighter?.photoUrl ?: match.posterUrl

                    if (newRedName != match.redFighterName || newRedFlag != match.redFighterFlag || (newRedPhoto != null && newRedPhoto != match.posterUrl)) {
                        updatedMatch = updatedMatch.copy(
                            redFighterName = newRedName,
                            redFighterFlag = newRedFlag,
                            posterUrl = newRedPhoto ?: match.posterUrl,
                            thumbnailUrl = newRedPhoto ?: match.thumbnailUrl,
                            lastUpdated = System.currentTimeMillis()
                        )
                        isModified = true
                    }
                }

                // Check Blue Corner Fighter
                val blueFighter = fighterMap[match.blueFighterId] ?: fighterMap.values.find { it.name.equals(match.blueFighterName, ignoreCase = true) }
                val blueProfile = profileMap[match.blueFighterId] ?: profileMap.values.find { it.name.equals(match.blueFighterName, ignoreCase = true) }

                if (blueFighter != null || blueProfile != null) {
                    val newBlueName = blueProfile?.name ?: blueFighter?.name ?: match.blueFighterName
                    val newBlueFlag = blueProfile?.flagEmoji ?: blueFighter?.flagEmoji ?: match.blueFighterFlag

                    if (newBlueName != match.blueFighterName || newBlueFlag != match.blueFighterFlag) {
                        updatedMatch = updatedMatch.copy(
                            blueFighterName = newBlueName,
                            blueFighterFlag = newBlueFlag,
                            lastUpdated = System.currentTimeMillis()
                        )
                        isModified = true
                    }
                }

                if (isModified) {
                    dao.updateMatch(updatedMatch)
                    matchesUpdated++
                }
            }

            // 2. Synchronize Hero Slides
            val allSlides = dao.getAllHeroSlidesDirect()
            for (slide in allSlides) {
                var isSlideModified = false
                var updatedSlide = slide

                val redF = slide.redFighterName?.let { rName ->
                    fighterMap.values.find { it.name.equals(rName, ignoreCase = true) }
                }
                val blueF = slide.blueFighterName?.let { bName ->
                    fighterMap.values.find { it.name.equals(bName, ignoreCase = true) }
                }

                if (redF != null) {
                    val newFlag = redF.flagEmoji
                    val newPhoto = redF.photoUrl
                    if (slide.redFlag != newFlag || (!newPhoto.isNullOrBlank() && slide.posterImageUrl != newPhoto)) {
                        updatedSlide = updatedSlide.copy(
                            redFlag = newFlag,
                            posterImageUrl = newPhoto ?: slide.posterImageUrl
                        )
                        isSlideModified = true
                    }
                }

                if (blueF != null) {
                    val newFlag = blueF.flagEmoji
                    if (slide.blueFlag != newFlag) {
                        updatedSlide = updatedSlide.copy(blueFlag = newFlag)
                        isSlideModified = true
                    }
                }

                if (isSlideModified) {
                    dao.updateHeroSlide(updatedSlide)
                }
            }

            // 3. Synchronize Previous Fight Videos Archive
            val allVideos = dao.getAllVideosDirect()
            for (video in allVideos) {
                var isVidModified = false
                var updatedVid = video

                val redF = fighterMap.values.find { it.name.equals(video.fighterRedName, ignoreCase = true) }
                if (redF != null && !redF.photoUrl.isNullOrBlank() && video.thumbnailUrl.isBlank()) {
                    updatedVid = updatedVid.copy(thumbnailUrl = redF.photoUrl)
                    isVidModified = true
                }

                if (isVidModified) {
                    dao.updatePreviousVideo(updatedVid)
                }
            }

            Log.d(TAG, "Consistency worker completed successfully. Matches re-indexed: $matchesUpdated")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in DataConsistencyWorker", e)
            Result.retry()
        }
    }
}
