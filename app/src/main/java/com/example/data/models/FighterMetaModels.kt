package com.example.data.models

data class FighterStats(
    val fighterId: String,
    val totalFights: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val noContests: Int = 0,
    val kos: Int,
    val submissions: Int,
    val decisions: Int,
    val koRatePercent: Float,
    val finishRatePercent: Float,
    val strikeAccuracyPercent: Int,
    val takedownDefensePercent: Int,
    val takedownAccuracyPercent: Int,
    val strikesLandedPerMin: Float,
    val strikesAbsorbedPerMin: Float,
    val takedownsPer15Min: Float,
    val avgFightTimeMinutes: Float,
    val winStreak: Int,
    val performanceBonusCount: Int
)

data class FightRecordHistoryItem(
    val recordId: String,
    val fighterId: String,
    val opponentName: String,
    val opponentCountry: String,
    val result: String, // "WIN", "LOSS", "DRAW", "NC"
    val method: String, // "KO/TKO", "Submission", "Unanimous Decision", "Split Decision"
    val round: Int,
    val timeInRound: String, // e.g. "2:45"
    val eventName: String, // e.g. "ETFC 001", "Adwa Combat Championship"
    val boutDate: String, // e.g. "2025-11-14"
    val weightClass: String,
    val isTitleBout: Boolean,
    val orderIndex: Int
)

data class WeightClassHistoryItem(
    val historyId: String,
    val fighterId: String,
    val weightClass: String,
    val startDate: String,
    val endDate: String?, // null if active current
    val boutsInDivision: Int,
    val recordInDivision: String, // e.g. "5-0-0"
    val wasChampion: Boolean,
    val titleDefenses: Int,
    val notes: String
)

data class FighterMetaEngineRecord(
    val fighter: Fighter,
    val stats: FighterStats,
    val boutHistory: List<FightRecordHistoryItem>,
    val weightClassHistory: List<WeightClassHistoryItem>
)
