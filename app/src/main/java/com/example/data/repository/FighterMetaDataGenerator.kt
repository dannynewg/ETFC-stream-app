package com.example.data.repository

import com.example.data.local.FightRecordHistoryEntity
import com.example.data.local.FighterProfileEntity
import com.example.data.local.FighterStatsEntity
import com.example.data.local.WeightClassHistoryEntity
import com.example.data.models.Fighter

object FighterMetaDataGenerator {

    fun generateProfiles(fighters: List<Fighter>): List<FighterProfileEntity> {
        return fighters.map { f ->
            FighterProfileEntity(
                fighterId = f.id,
                name = f.name,
                nickname = f.nickname,
                countryName = f.countryName,
                flagEmoji = f.flagEmoji,
                division = f.division.label,
                stance = f.stance,
                reachInches = f.reachInches,
                heightCm = f.heightCm,
                isChampion = f.isChampion,
                rank = f.rank,
                bio = f.bio,
                signatureMove = f.signatureMove,
                primaryColorHex = f.primaryColorHex,
                hairStyle = f.hairStyle.name,
                discipline = f.discipline.label,
                lastUpdatedTimestamp = System.currentTimeMillis(),
                photoUrl = f.photoUrl
            )
        }
    }

    fun generateStats(fighters: List<Fighter>): List<FighterStatsEntity> {
        return fighters.map { f ->
            val total = f.wins + f.losses + f.draws
            val finishRate = if (f.wins > 0) ((f.kos + f.submissions).toFloat() / f.wins) * 100f else 0f
            val koRate = if (f.wins > 0) (f.kos.toFloat() / f.wins) * 100f else 0f

            FighterStatsEntity(
                fighterId = f.id,
                totalFights = total,
                wins = f.wins,
                losses = f.losses,
                draws = f.draws,
                noContests = 0,
                kos = f.kos,
                submissions = f.submissions,
                decisions = (f.wins - f.kos - f.submissions).coerceAtLeast(0),
                koRatePercent = koRate,
                finishRatePercent = finishRate,
                strikeAccuracyPercent = f.strikeAccuracyPercent,
                takedownDefensePercent = f.takedownDefensePercent,
                takedownAccuracyPercent = (55..82).random(),
                strikesLandedPerMin = 4.2f + (f.wins % 5) * 0.3f,
                strikesAbsorbedPerMin = 2.1f + (f.losses % 4) * 0.4f,
                takedownsPer15Min = 1.8f + (f.submissions % 4) * 0.5f,
                avgFightTimeMinutes = 8.5f + (f.wins % 6) * 1.2f,
                winStreak = if (f.losses == 0) f.wins else (f.wins % 4 + 1),
                performanceBonusCount = (f.kos / 2).coerceAtLeast(1)
            )
        }
    }

    fun generateFightRecordHistory(): List<FightRecordHistoryEntity> {
        return listOf(
            // --- Sedo Fight History ---
            FightRecordHistoryEntity(
                recordId = "rec_sedo_1",
                fighterId = "f_sedo",
                opponentName = "Robel Tesfaye",
                opponentCountry = "Ethiopia",
                result = "WIN",
                method = "KO/TKO (Overhand Right)",
                round = 1,
                timeInRound = "1:42",
                eventName = "ETFC 001: Genesis",
                boutDate = "2025-11-20",
                weightClass = "Middleweight",
                isTitleBout = false,
                orderIndex = 12
            ),
            FightRecordHistoryEntity(
                recordId = "rec_sedo_2",
                fighterId = "f_sedo",
                opponentName = "Abel Kebede",
                opponentCountry = "Ethiopia",
                result = "WIN",
                method = "KO/TKO (Ground & Pound)",
                round = 2,
                timeInRound = "3:10",
                eventName = "Adwa Warrior Series 4",
                boutDate = "2025-07-14",
                weightClass = "Middleweight",
                isTitleBout = false,
                orderIndex = 11
            ),
            FightRecordHistoryEntity(
                recordId = "rec_sedo_3",
                fighterId = "f_sedo",
                opponentName = "Kiprono Bett",
                opponentCountry = "Kenya",
                result = "WIN",
                method = "KO/TKO (Adwa Hammer Cross)",
                round = 1,
                timeInRound = "0:58",
                eventName = "East Africa MMA Championship",
                boutDate = "2025-03-02",
                weightClass = "Middleweight",
                isTitleBout = true,
                orderIndex = 10
            ),
            FightRecordHistoryEntity(
                recordId = "rec_sedo_4",
                fighterId = "f_sedo",
                opponentName = "Markos Alemu",
                opponentCountry = "Ethiopia",
                result = "LOSS",
                method = "Unanimous Decision",
                round = 3,
                timeInRound = "5:00",
                eventName = "Horn of Africa Combat 2",
                boutDate = "2024-09-18",
                weightClass = "Light Heavyweight",
                isTitleBout = false,
                orderIndex = 9
            ),
            FightRecordHistoryEntity(
                recordId = "rec_sedo_5",
                fighterId = "f_sedo",
                opponentName = "Samuel Birhanu",
                opponentCountry = "Ethiopia",
                result = "WIN",
                method = "Submission (Rear-Naked Choke)",
                round = 2,
                timeInRound = "4:15",
                eventName = "Sheger Fight League 3",
                boutDate = "2024-04-10",
                weightClass = "Middleweight",
                isTitleBout = false,
                orderIndex = 8
            ),

            // --- Jhonny Fight History ---
            FightRecordHistoryEntity(
                recordId = "rec_jhonny_1",
                fighterId = "f_jhonny",
                opponentName = "Tadesse Hailu",
                opponentCountry = "Ethiopia",
                result = "WIN",
                method = "KO/TKO (Left Hook)",
                round = 2,
                timeInRound = "2:30",
                eventName = "ETFC 001: Genesis",
                boutDate = "2025-11-20",
                weightClass = "Middleweight",
                isTitleBout = true,
                orderIndex = 15
            ),
            FightRecordHistoryEntity(
                recordId = "rec_jhonny_2",
                fighterId = "f_jhonny",
                opponentName = "Ezekiel Mutua",
                opponentCountry = "Kenya",
                result = "WIN",
                method = "Submission (Guillotine Choke)",
                round = 1,
                timeInRound = "3:44",
                eventName = "Safari Combat FC 8",
                boutDate = "2025-06-12",
                weightClass = "Middleweight",
                isTitleBout = true,
                orderIndex = 14
            ),
            FightRecordHistoryEntity(
                recordId = "rec_jhonny_3",
                fighterId = "f_jhonny",
                opponentName = "Elezar Gebre",
                opponentCountry = "Ethiopia",
                result = "WIN",
                method = "Unanimous Decision",
                round = 5,
                timeInRound = "5:00",
                eventName = "Adwa Championship 1",
                boutDate = "2024-12-05",
                weightClass = "Middleweight",
                isTitleBout = true,
                orderIndex = 13
            ),
            FightRecordHistoryEntity(
                recordId = "rec_jhonny_4",
                fighterId = "f_jhonny",
                opponentName = "Yared Bekele",
                opponentCountry = "Ethiopia",
                result = "WIN",
                method = "KO/TKO (Head Kick)",
                round = 1,
                timeInRound = "1:15",
                eventName = "National Combat Trials",
                boutDate = "2024-05-22",
                weightClass = "Welterweight",
                isTitleBout = false,
                orderIndex = 12
            ),

            // --- Elezar Fight History ---
            FightRecordHistoryEntity(
                recordId = "rec_elezar_1",
                fighterId = "f_elezar",
                opponentName = "Desalegn Fikru",
                opponentCountry = "Ethiopia",
                result = "WIN",
                method = "KO/TKO (Gladiator Overhand)",
                round = 1,
                timeInRound = "4:02",
                eventName = "ETFC 001: Genesis",
                boutDate = "2025-11-20",
                weightClass = "Welterweight",
                isTitleBout = false,
                orderIndex = 9
            ),
            FightRecordHistoryEntity(
                recordId = "rec_elezar_2",
                fighterId = "f_elezar",
                opponentName = "Simon Kiptoo",
                opponentCountry = "Kenya",
                result = "WIN",
                method = "Submission (Armbar)",
                round = 2,
                timeInRound = "3:20",
                eventName = "Nairobi Cage Night",
                boutDate = "2025-08-05",
                weightClass = "Welterweight",
                isTitleBout = false,
                orderIndex = 8
            ),

            // --- Zahara Fight History ---
            FightRecordHistoryEntity(
                recordId = "rec_zahara_1",
                fighterId = "f_zahara",
                opponentName = "Amina Hassan",
                opponentCountry = "Somalia",
                result = "WIN",
                method = "Submission (Triangle Choke)",
                round = 1,
                timeInRound = "2:18",
                eventName = "ETFC 001: Genesis",
                boutDate = "2025-11-20",
                weightClass = "Women's Flyweight",
                isTitleBout = true,
                orderIndex = 8
            ),
            FightRecordHistoryEntity(
                recordId = "rec_zahara_2",
                fighterId = "f_zahara",
                opponentName = "Mercy Wanjiku",
                opponentCountry = "Kenya",
                result = "WIN",
                method = "KO/TKO (Spinning Back Kick)",
                round = 2,
                timeInRound = "1:45",
                eventName = "Great Rift Combat 3",
                boutDate = "2025-05-18",
                weightClass = "Women's Flyweight",
                isTitleBout = true,
                orderIndex = 7
            )
        )
    }

    fun generateWeightClassHistory(): List<WeightClassHistoryEntity> {
        return listOf(
            // Sedo
            WeightClassHistoryEntity(
                historyId = "wch_sedo_1",
                fighterId = "f_sedo",
                weightClass = "Middleweight (185 lbs)",
                startDate = "2024-10-01",
                endDate = null,
                boutsInDivision = 8,
                recordInDivision = "7-1-0",
                wasChampion = false,
                titleDefenses = 0,
                notes = "Current #1 Contender for ETFC Middleweight World Championship title."
            ),
            WeightClassHistoryEntity(
                historyId = "wch_sedo_2",
                fighterId = "f_sedo",
                weightClass = "Light Heavyweight (205 lbs)",
                startDate = "2023-01-15",
                endDate = "2024-09-30",
                boutsInDivision = 5,
                recordInDivision = "5-0-0",
                wasChampion = true,
                titleDefenses = 2,
                notes = "Captured regional Light Heavyweight belt with 4 consecutive first-round KOs."
            ),

            // Jhonny
            WeightClassHistoryEntity(
                historyId = "wch_jhonny_1",
                fighterId = "f_jhonny",
                weightClass = "Middleweight (185 lbs)",
                startDate = "2024-06-01",
                endDate = null,
                boutsInDivision = 9,
                recordInDivision = "9-0-0",
                wasChampion = true,
                titleDefenses = 3,
                notes = "Undefeated Reigning ETFC Middleweight World Champion with 3 successful defenses."
            ),
            WeightClassHistoryEntity(
                historyId = "wch_jhonny_2",
                fighterId = "f_jhonny",
                weightClass = "Welterweight (170 lbs)",
                startDate = "2022-03-10",
                endDate = "2024-05-30",
                boutsInDivision = 6,
                recordInDivision = "6-0-0",
                wasChampion = true,
                titleDefenses = 2,
                notes = "Former Welterweight Champion prior to moving up to Middleweight."
            ),

            // Elezar
            WeightClassHistoryEntity(
                historyId = "wch_elezar_1",
                fighterId = "f_elezar",
                weightClass = "Welterweight (170 lbs)",
                startDate = "2023-08-01",
                endDate = null,
                boutsInDivision = 11,
                recordInDivision = "9-2-0",
                wasChampion = false,
                titleDefenses = 0,
                notes = "Ranked #2 in division. Renowned for highest striking output."
            ),

            // Zahara
            WeightClassHistoryEntity(
                historyId = "wch_zahara_1",
                fighterId = "f_zahara",
                weightClass = "Women's Flyweight (125 lbs)",
                startDate = "2023-11-01",
                endDate = null,
                boutsInDivision = 8,
                recordInDivision = "8-0-0",
                wasChampion = true,
                titleDefenses = 2,
                notes = "Undefeated champion with fastest submission record (1:12 triangle choke)."
            )
        )
    }
}
