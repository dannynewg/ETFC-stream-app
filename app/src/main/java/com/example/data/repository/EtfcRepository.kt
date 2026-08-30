package com.example.data.repository

import com.example.data.local.DownloadedFightEntity
import com.example.data.local.EtfcDao
import com.example.data.local.FavoriteFighterEntity
import com.example.data.local.FighterProfileEntity
import com.example.data.local.FighterStatsEntity
import com.example.data.local.HeroSlideEntity
import com.example.data.local.NewsArticleEntity
import com.example.data.local.PpvPassEntity
import com.example.data.local.PreviousVideoEntity
import com.example.data.local.UserPredictionEntity
import com.example.data.local.UserAccountEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.ModerationLogEntity
import com.example.data.models.AuthResult
import com.example.data.models.Bout
import com.example.data.models.BoutStatus
import com.example.data.models.BoutType
import com.example.data.models.CdnNodeInfo
import com.example.data.models.ChatMessage
import com.example.data.models.ContentTier
import com.example.data.models.FanChatMessage
import com.example.data.models.Fighter
import com.example.data.models.FighterHairStyle
import com.example.data.models.HeroSlide
import com.example.data.models.HeroSlideType
import com.example.data.models.MatchCategory
import com.example.data.models.ModActionType
import com.example.data.models.NewsArticle
import com.example.data.models.NewsCategory
import com.example.data.models.PaymentProvider
import com.example.data.models.PpvTier
import com.example.data.models.PreviousFightVideo
import com.example.data.models.StreamQuality
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.example.data.models.VideoCategory
import com.example.data.models.WeightClass
import com.example.data.security.SecurityUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

class EtfcRepository(private val dao: EtfcDao) {

    // --- Local DB Flows ---
    val downloadedFights: Flow<List<DownloadedFightEntity>> = dao.getAllDownloadedFights()
    val ppvPasses: Flow<List<PpvPassEntity>> = dao.getAllPpvPasses()
    val activePass: Flow<PpvPassEntity?> = dao.getActivePpvPass()
    val userPredictions: Flow<List<UserPredictionEntity>> = dao.getAllPredictions()
    val favoriteFighters: Flow<List<FavoriteFighterEntity>> = dao.getAllFavoriteFighters()

    // Dynamic Content Flows (Populated from SQLite Room and Synced with GitHub Admin)
    val dynamicHeroSlides: Flow<List<HeroSlide>> = dao.getAllHeroSlides().map { entities ->
        if (entities.isEmpty()) heroSlides else entities.map { it.toModel() }
    }

    val dynamicPreviousVideos: Flow<List<PreviousFightVideo>> = dao.getAllPreviousVideos().map { entities ->
        if (entities.isEmpty()) previousVideos else entities.map { it.toModel() }
    }

    val dynamicNewsArticles: Flow<List<NewsArticle>> = dao.getAllNewsArticles().map { entities ->
        if (entities.isEmpty()) newsArticles else entities.map { it.toModel() }
    }

    val dynamicFighters: Flow<List<Fighter>> = dao.getAllFighterProfiles().map { profiles ->
        if (profiles.isEmpty()) fighters else {
            profiles.map { profile ->
                val base = fighters.firstOrNull { it.id == profile.fighterId }
                if (base != null) {
                    base.copy(
                        name = profile.name,
                        nickname = profile.nickname,
                        countryName = profile.countryName,
                        flagEmoji = profile.flagEmoji,
                        division = WeightClass.entries.firstOrNull { it.label == profile.division } ?: base.division,
                        stance = profile.stance,
                        reachInches = profile.reachInches,
                        heightCm = profile.heightCm,
                        isChampion = profile.isChampion,
                        rank = profile.rank,
                        bio = profile.bio,
                        signatureMove = profile.signatureMove,
                        primaryColorHex = profile.primaryColorHex,
                        photoUrl = profile.photoUrl
                    )
                } else {
                    Fighter(
                        id = profile.fighterId,
                        name = profile.name,
                        nickname = profile.nickname,
                        countryName = profile.countryName,
                        flagEmoji = profile.flagEmoji,
                        division = WeightClass.entries.firstOrNull { it.label == profile.division } ?: WeightClass.MIDDLEWEIGHT,
                        wins = 5,
                        losses = 0,
                        draws = 0,
                        kos = 3,
                        submissions = 1,
                        strikeAccuracyPercent = 65,
                        takedownDefensePercent = 80,
                        reachInches = profile.reachInches,
                        heightCm = profile.heightCm,
                        stance = profile.stance,
                        isChampion = profile.isChampion,
                        rank = profile.rank,
                        bio = profile.bio,
                        signatureMove = profile.signatureMove,
                        primaryColorHex = profile.primaryColorHex,
                        photoUrl = profile.photoUrl
                    )
                }
            }
        }
    }

    // --- Fighter Meta-Engine Roster (ETFC 002: Adwa Fight Night) ---
    val fighters: List<Fighter> = listOf(
        // 1. Sedo (Middleweight MMA - Red Corner Main Event)
        Fighter(
            id = "f_sedo",
            name = "Sedo",
            nickname = "The Demolisher",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.MIDDLEWEIGHT,
            wins = 12,
            losses = 1,
            draws = 0,
            kos = 9,
            submissions = 2,
            strikeAccuracyPercent = 72,
            takedownDefensePercent = 88,
            reachInches = 76,
            heightCm = 184,
            stance = "Orthodox",
            isChampion = false,
            rank = 1,
            bio = "Explosive middleweight knockout specialist known for ruthless forward pressure and heavy right hands.",
            signatureMove = "Adwa Hammer Cross",
            primaryColorHex = 0xFFEF4444,
            hairStyle = FighterHairStyle.BALD_BEARD,
            discipline = MatchCategory.MMA
        ),
        // 2. Jhonny (Middleweight MMA - Blue Corner Champion Main Event)
        Fighter(
            id = "f_jhonny",
            name = "Jhonny",
            nickname = "The Champion",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.MIDDLEWEIGHT,
            wins = 15,
            losses = 0,
            draws = 0,
            kos = 11,
            submissions = 3,
            strikeAccuracyPercent = 75,
            takedownDefensePercent = 94,
            reachInches = 78,
            heightCm = 187,
            stance = "Southpaw",
            isChampion = true,
            rank = null,
            bio = "Reigning ETFC Middleweight Champion. Unbeaten powerhouse with championship belt pedigree and all-round mastery.",
            signatureMove = "Belt Winner Left Hook",
            primaryColorHex = 0xFFFAB818,
            hairStyle = FighterHairStyle.CURLY_BEARD,
            discipline = MatchCategory.MMA
        ),
        // 3. Elezar (MMA Bout)
        Fighter(
            id = "f_elezar",
            name = "Elezar",
            nickname = "The Gladiator",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.WELTERWEIGHT,
            wins = 9,
            losses = 2,
            draws = 0,
            kos = 6,
            submissions = 2,
            strikeAccuracyPercent = 67,
            takedownDefensePercent = 83,
            reachInches = 73,
            heightCm = 180,
            stance = "Orthodox",
            isChampion = false,
            rank = 2,
            bio = "Dynamic welterweight warrior with crisp boxing fundamentals and fast level changes.",
            signatureMove = "Gladiator Overhand",
            primaryColorHex = 0xFFEF4444,
            hairStyle = FighterHairStyle.FADE_BEARD,
            discipline = MatchCategory.MMA
        ),
        // 4. Kaleab (MMA Bout)
        Fighter(
            id = "f_kaleab",
            name = "Kaleab",
            nickname = "The Panther",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.WELTERWEIGHT,
            wins = 8,
            losses = 1,
            draws = 0,
            kos = 5,
            submissions = 3,
            strikeAccuracyPercent = 64,
            takedownDefensePercent = 86,
            reachInches = 74,
            heightCm = 182,
            stance = "Orthodox",
            isChampion = false,
            rank = 3,
            bio = "Tenacious grappler and counter-striker with relentless takedown persistence.",
            signatureMove = "Panther Double Leg",
            primaryColorHex = 0xFF38BDF8,
            hairStyle = FighterHairStyle.CURLY_SHORT,
            discipline = MatchCategory.MMA
        ),
        // 5. Edris (MMA Bout)
        Fighter(
            id = "f_edris",
            name = "Edris",
            nickname = "The Barbarian",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.LIGHTWEIGHT,
            wins = 11,
            losses = 2,
            draws = 0,
            kos = 8,
            submissions = 2,
            strikeAccuracyPercent = 69,
            takedownDefensePercent = 81,
            reachInches = 71,
            heightCm = 177,
            stance = "Orthodox",
            isChampion = false,
            rank = 1,
            bio = "Feared brawler with full beard power, crushing clinch knees, and aggressive ground-and-pound.",
            signatureMove = "Barbarian Clinch Barrage",
            primaryColorHex = 0xFFEF4444,
            hairStyle = FighterHairStyle.FADE_BEARD,
            discipline = MatchCategory.MMA
        ),
        // 6. Boyka (MMA Bout)
        Fighter(
            id = "f_boyka",
            name = "Boyka",
            nickname = "The Undisputed",
            countryName = "South Sudan",
            flagEmoji = "🇸🇸",
            division = WeightClass.LIGHTWEIGHT,
            wins = 13,
            losses = 1,
            draws = 0,
            kos = 10,
            submissions = 2,
            strikeAccuracyPercent = 73,
            takedownDefensePercent = 90,
            reachInches = 75,
            heightCm = 183,
            stance = "Switch",
            isChampion = false,
            rank = 2,
            bio = "Spectacular acrobat and lethal spinning kick artist with imposing height and reach.",
            signatureMove = "Tornado Guyver Kick",
            primaryColorHex = 0xFF38BDF8,
            hairStyle = FighterHairStyle.SHAVED_CLEAN,
            discipline = MatchCategory.MMA
        ),
        // 7. Robel (MMA Bout)
        Fighter(
            id = "f_robel",
            name = "Robel",
            nickname = "The Eagle",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.FEATHERWEIGHT,
            wins = 10,
            losses = 0,
            draws = 0,
            kos = 7,
            submissions = 2,
            strikeAccuracyPercent = 70,
            takedownDefensePercent = 89,
            reachInches = 70,
            heightCm = 175,
            stance = "Orthodox",
            isChampion = true,
            rank = null,
            bio = "Undefeated featherweight phenom with razor-sharp striking combinations and elite agility.",
            signatureMove = "Highland Eagle Flying Knee",
            primaryColorHex = 0xFFEF4444,
            hairStyle = FighterHairStyle.BRAIDS_TIED,
            discipline = MatchCategory.MMA
        ),
        // 8. Nikatehilina (MMA Bout)
        Fighter(
            id = "f_nikatehilina",
            name = "Nikatehilina",
            nickname = "The Warrior",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.FEATHERWEIGHT,
            wins = 8,
            losses = 2,
            draws = 0,
            kos = 5,
            submissions = 2,
            strikeAccuracyPercent = 63,
            takedownDefensePercent = 79,
            reachInches = 69,
            heightCm = 173,
            stance = "Southpaw",
            isChampion = false,
            rank = 2,
            bio = "Fearless tactical striker who tests opponents' cardio in deep championship waters.",
            signatureMove = "Southpaw Cross Counter",
            primaryColorHex = 0xFF38BDF8,
            hairStyle = FighterHairStyle.CURLY_SHORT,
            discipline = MatchCategory.MMA
        ),
        // 9. Biniam (Boxing Match)
        Fighter(
            id = "f_biniam",
            name = "Biniam",
            nickname = "The Precision",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.WELTERWEIGHT,
            wins = 14,
            losses = 1,
            draws = 0,
            kos = 9,
            submissions = 0,
            strikeAccuracyPercent = 78,
            takedownDefensePercent = 0,
            reachInches = 72,
            heightCm = 179,
            stance = "Orthodox",
            isChampion = false,
            rank = 1,
            bio = "Elite amateur boxing gold medalist featuring pinpoint jab accuracy and head movement.",
            signatureMove = "Laser Straight Right",
            primaryColorHex = 0xFFEF4444,
            hairStyle = FighterHairStyle.FADE_BEARD,
            discipline = MatchCategory.BOXING
        ),
        // 10. Esubalew (Boxing Match)
        Fighter(
            id = "f_esubalew",
            name = "Esubalew",
            nickname = "Iron Fist",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.WELTERWEIGHT,
            wins = 12,
            losses = 2,
            draws = 1,
            kos = 10,
            submissions = 0,
            strikeAccuracyPercent = 71,
            takedownDefensePercent = 0,
            reachInches = 71,
            heightCm = 178,
            stance = "Orthodox",
            isChampion = false,
            rank = 2,
            bio = "Heavy-handed boxer with tremendous body shot power and devastating inside combinations.",
            signatureMove = "Liver Rip Hook",
            primaryColorHex = 0xFF38BDF8,
            hairStyle = FighterHairStyle.CURLY_SHORT,
            discipline = MatchCategory.BOXING
        ),
        // 11. Haymanot (Boxing Match)
        Fighter(
            id = "f_haymanot",
            name = "Haymanot",
            nickname = "Lightning",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.LIGHTWEIGHT,
            wins = 10,
            losses = 1,
            draws = 0,
            kos = 6,
            submissions = 0,
            strikeAccuracyPercent = 74,
            takedownDefensePercent = 0,
            reachInches = 69,
            heightCm = 174,
            stance = "Orthodox",
            isChampion = false,
            rank = 1,
            bio = "Blistering hand speed and footwork that makes him elusive and dangerous in ring exchanges.",
            signatureMove = "Triple Jab-Cross Flurry",
            primaryColorHex = 0xFFEF4444,
            hairStyle = FighterHairStyle.FADE_BEARD,
            discipline = MatchCategory.BOXING
        ),
        // 12. Abreham (Boxing Match)
        Fighter(
            id = "f_abreham",
            name = "Abreham",
            nickname = "The Shadow",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.LIGHTWEIGHT,
            wins = 9,
            losses = 2,
            draws = 0,
            kos = 5,
            submissions = 0,
            strikeAccuracyPercent = 68,
            takedownDefensePercent = 0,
            reachInches = 70,
            heightCm = 176,
            stance = "Southpaw",
            isChampion = false,
            rank = 2,
            bio = "Slick southpaw counter-puncher who baits opponents before landing vicious check hooks.",
            signatureMove = "Stealth Check Hook",
            primaryColorHex = 0xFF38BDF8,
            hairStyle = FighterHairStyle.FADE_BEARD,
            discipline = MatchCategory.BOXING
        ),
        // 13. Desalegn (Boxing Match)
        Fighter(
            id = "f_desalegn",
            name = "Desalegn",
            nickname = "The Hammer",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.MIDDLEWEIGHT,
            wins = 13,
            losses = 3,
            draws = 0,
            kos = 11,
            submissions = 0,
            strikeAccuracyPercent = 73,
            takedownDefensePercent = 0,
            reachInches = 75,
            heightCm = 183,
            stance = "Orthodox",
            isChampion = false,
            rank = 1,
            bio = "Powerhouse pugilist who breaks down guards with heavy hooks and relentless ring control.",
            signatureMove = "Overhand Sledgehammer",
            primaryColorHex = 0xFFEF4444,
            hairStyle = FighterHairStyle.FADE_BEARD,
            discipline = MatchCategory.BOXING
        ),
        // 14. Surafel (Boxing Match)
        Fighter(
            id = "f_surafel",
            name = "Surafel",
            nickname = "The Cobra",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.MIDDLEWEIGHT,
            wins = 11,
            losses = 1,
            draws = 1,
            kos = 7,
            submissions = 0,
            strikeAccuracyPercent = 70,
            takedownDefensePercent = 0,
            reachInches = 76,
            heightCm = 185,
            stance = "Orthodox",
            isChampion = false,
            rank = 2,
            bio = "Veteran boxing technician with a stinging jab and masterclass defensive shoulder roll.",
            signatureMove = "Cobra Strike Uppercut",
            primaryColorHex = 0xFF38BDF8,
            hairStyle = FighterHairStyle.SHAVED_CLEAN,
            discipline = MatchCategory.BOXING
        ),
        // 15. Rebik (Muay Thai Match)
        Fighter(
            id = "f_rebik",
            name = "Rebik",
            nickname = "The Dragon",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.FEATHERWEIGHT,
            wins = 15,
            losses = 2,
            draws = 0,
            kos = 12,
            submissions = 0,
            strikeAccuracyPercent = 80,
            takedownDefensePercent = 85,
            reachInches = 70,
            heightCm = 174,
            stance = "Orthodox",
            isChampion = false,
            rank = 1,
            bio = "Fierce Muay Thai Nak Muay with bone-crushing low kicks and lethal elbow exits.",
            signatureMove = "Dragon Low Kick Chop",
            primaryColorHex = 0xFFEF4444,
            hairStyle = FighterHairStyle.FADE_BEARD,
            discipline = MatchCategory.MUAY_THAI
        ),
        // 16. Stephen (Muay Thai Match)
        Fighter(
            id = "f_stephen",
            name = "Stephen",
            nickname = "The Storm",
            countryName = "Kenya",
            flagEmoji = "🇰🇪",
            division = WeightClass.FEATHERWEIGHT,
            wins = 12,
            losses = 3,
            draws = 0,
            kos = 9,
            submissions = 0,
            strikeAccuracyPercent = 74,
            takedownDefensePercent = 82,
            reachInches = 73,
            heightCm = 181,
            stance = "Orthodox",
            isChampion = false,
            rank = 2,
            bio = "High-energy Kenyan Muay Thai sensation known for flying knees and spinning back elbows.",
            signatureMove = "Storm Flying Knee",
            primaryColorHex = 0xFF38BDF8,
            hairStyle = FighterHairStyle.HIGH_TOP,
            discipline = MatchCategory.MUAY_THAI
        ),
        // 17. Habtamu (Muay Thai Match)
        Fighter(
            id = "f_habtamu",
            name = "Habtamu",
            nickname = "The Lion",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.BANTAMWEIGHT,
            wins = 10,
            losses = 1,
            draws = 0,
            kos = 7,
            submissions = 0,
            strikeAccuracyPercent = 76,
            takedownDefensePercent = 80,
            reachInches = 68,
            heightCm = 171,
            stance = "Orthodox",
            isChampion = false,
            rank = 1,
            bio = "Relentless forward pressing Nak Muay who specializes in lethal body teeps and knee dumps.",
            signatureMove = "Spear Knee to Ribs",
            primaryColorHex = 0xFFEF4444,
            hairStyle = FighterHairStyle.CURLY_SHORT,
            discipline = MatchCategory.MUAY_THAI
        ),
        // 18. Frezer (Muay Thai Match)
        Fighter(
            id = "f_frezer",
            name = "Frezer",
            nickname = "The Sniper",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.BANTAMWEIGHT,
            wins = 9,
            losses = 2,
            draws = 0,
            kos = 6,
            submissions = 0,
            strikeAccuracyPercent = 72,
            takedownDefensePercent = 81,
            reachInches = 67,
            heightCm = 170,
            stance = "Orthodox",
            isChampion = false,
            rank = 2,
            bio = "Sharp technical striker who excels in counter-elbows and sweeps from the Muay Thai clinch.",
            signatureMove = "Clinch Step Elbow",
            primaryColorHex = 0xFF38BDF8,
            hairStyle = FighterHairStyle.SHAVED_CLEAN,
            discipline = MatchCategory.MUAY_THAI
        ),
        // 19. Zahara (Muay Thai Match - Female)
        Fighter(
            id = "f_zahara",
            name = "Zahara",
            nickname = "The Queen",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.WOMENS_FLYWEIGHT,
            wins = 11,
            losses = 0,
            draws = 0,
            kos = 8,
            submissions = 0,
            strikeAccuracyPercent = 82,
            takedownDefensePercent = 90,
            reachInches = 66,
            heightCm = 168,
            stance = "Orthodox",
            isChampion = true,
            rank = null,
            bio = "Undefeated Women's Muay Thai powerhouse fighting in signature sports hijab. Flawless roundhouse kicks.",
            signatureMove = "Highland Head Kick KO",
            primaryColorHex = 0xFFEF4444,
            hairStyle = FighterHairStyle.HIJAB_HOOD,
            discipline = MatchCategory.MUAY_THAI
        ),
        // 20. Yabsira (Muay Thai Match - Female)
        Fighter(
            id = "f_yabsira",
            name = "Yabsira",
            nickname = "The Valkyrie",
            countryName = "Ethiopia",
            flagEmoji = "🇪🇹",
            division = WeightClass.WOMENS_FLYWEIGHT,
            wins = 9,
            losses = 1,
            draws = 0,
            kos = 6,
            submissions = 0,
            strikeAccuracyPercent = 77,
            takedownDefensePercent = 84,
            reachInches = 65,
            heightCm = 166,
            stance = "Southpaw",
            isChampion = false,
            rank = 1,
            bio = "Aggressive southpaw striker with blistering punch-kick combinations and fierce ring presence.",
            signatureMove = "Southpaw Switch Kick",
            primaryColorHex = 0xFF38BDF8,
            hairStyle = FighterHairStyle.BRAIDS_TIED,
            discipline = MatchCategory.MUAY_THAI
        )
    )

    // --- Official ETFC 002: Adwa Fight Night Match Card ---
    val currentEventBouts: List<Bout> = listOf(
        // 1. MAIN EVENT: Sedo vs Jhonny (Middle Weight MMA Bout)
        Bout(
            id = "bout_etfc002_main",
            eventName = "ETFC 002: Adwa Fight Night",
            eventVenue = "Adwa 00 Museum — August 27",
            boutType = BoutType.MAIN_EVENT,
            weightClass = WeightClass.MIDDLEWEIGHT,
            matchCategory = MatchCategory.MMA,
            matchTitle = "MIDDLE WEIGHT MMA BOUT",
            fighterRed = fighters[0], // Sedo
            fighterBlue = fighters[1], // Jhonny
            isTitleFight = true,
            scheduledRounds = 5,
            status = BoutStatus.LIVE,
            liveRound = 3,
            liveClockSeconds = 174,
            redSignificantStrikes = 46,
            blueSignificantStrikes = 53,
            redTotalStrikes = 68,
            blueTotalStrikes = 76,
            redTakedowns = 1,
            blueTakedowns = 2,
            redControlTimeSec = 118,
            blueControlTimeSec = 142
        ),
        // 2. Elezar vs Kaleab (MMA Bout)
        Bout(
            id = "bout_etfc002_elezar_kaleab",
            eventName = "ETFC 002: Adwa Fight Night",
            eventVenue = "Adwa 00 Museum — August 27",
            boutType = BoutType.CO_MAIN,
            weightClass = WeightClass.WELTERWEIGHT,
            matchCategory = MatchCategory.MMA,
            matchTitle = "MMA BOUT",
            fighterRed = fighters[2], // Elezar
            fighterBlue = fighters[3], // Kaleab
            isTitleFight = false,
            scheduledRounds = 3,
            status = BoutStatus.UPCOMING
        ),
        // 3. Edris vs Boyka (MMA Bout)
        Bout(
            id = "bout_etfc002_edris_boyka",
            eventName = "ETFC 002: Adwa Fight Night",
            eventVenue = "Adwa 00 Museum — August 27",
            boutType = BoutType.MAIN_CARD,
            weightClass = WeightClass.LIGHTWEIGHT,
            matchCategory = MatchCategory.MMA,
            matchTitle = "MMA BOUT",
            fighterRed = fighters[4], // Edris
            fighterBlue = fighters[5], // Boyka
            isTitleFight = false,
            scheduledRounds = 3,
            status = BoutStatus.UPCOMING
        ),
        // 4. Robel vs Nikatehilina (MMA Bout)
        Bout(
            id = "bout_etfc002_robel_nikatehilina",
            eventName = "ETFC 002: Adwa Fight Night",
            eventVenue = "Adwa 00 Museum — August 27",
            boutType = BoutType.MAIN_CARD,
            weightClass = WeightClass.FEATHERWEIGHT,
            matchCategory = MatchCategory.MMA,
            matchTitle = "MMA BOUT",
            fighterRed = fighters[6], // Robel
            fighterBlue = fighters[7], // Nikatehilina
            isTitleFight = false,
            scheduledRounds = 3,
            status = BoutStatus.UPCOMING
        ),
        // 5. Biniam vs Esubalew (Boxing Match)
        Bout(
            id = "bout_etfc002_biniam_esubalew",
            eventName = "ETFC 002: Adwa Fight Night",
            eventVenue = "Adwa 00 Museum — August 27",
            boutType = BoutType.MAIN_CARD,
            weightClass = WeightClass.WELTERWEIGHT,
            matchCategory = MatchCategory.BOXING,
            matchTitle = "BOXING MATCH",
            fighterRed = fighters[8], // Biniam
            fighterBlue = fighters[9], // Esubalew
            isTitleFight = false,
            scheduledRounds = 4,
            status = BoutStatus.UPCOMING
        ),
        // 6. Haymanot vs Abreham (Boxing Match)
        Bout(
            id = "bout_etfc002_haymanot_abreham",
            eventName = "ETFC 002: Adwa Fight Night",
            eventVenue = "Adwa 00 Museum — August 27",
            boutType = BoutType.MAIN_CARD,
            weightClass = WeightClass.LIGHTWEIGHT,
            matchCategory = MatchCategory.BOXING,
            matchTitle = "BOXING MATCH",
            fighterRed = fighters[10], // Haymanot
            fighterBlue = fighters[11], // Abreham
            isTitleFight = false,
            scheduledRounds = 4,
            status = BoutStatus.UPCOMING
        ),
        // 7. Desalegn vs Surafel (Boxing Match)
        Bout(
            id = "bout_etfc002_desalegn_surafel",
            eventName = "ETFC 002: Adwa Fight Night",
            eventVenue = "Adwa 00 Museum — August 27",
            boutType = BoutType.MAIN_CARD,
            weightClass = WeightClass.MIDDLEWEIGHT,
            matchCategory = MatchCategory.BOXING,
            matchTitle = "BOXING MATCH",
            fighterRed = fighters[12], // Desalegn
            fighterBlue = fighters[13], // Surafel
            isTitleFight = false,
            scheduledRounds = 4,
            status = BoutStatus.UPCOMING
        ),
        // 8. Rebik vs Stephen (Muay Thai Match)
        Bout(
            id = "bout_etfc002_rebik_stephen",
            eventName = "ETFC 002: Adwa Fight Night",
            eventVenue = "Adwa 00 Museum — August 27",
            boutType = BoutType.EARLY_PRELIMS,
            weightClass = WeightClass.FEATHERWEIGHT,
            matchCategory = MatchCategory.MUAY_THAI,
            matchTitle = "MUAY THAI MATCH",
            fighterRed = fighters[14], // Rebik
            fighterBlue = fighters[15], // Stephen
            isTitleFight = false,
            scheduledRounds = 3,
            status = BoutStatus.UPCOMING
        ),
        // 9. Habtamu vs Frezer (Muay Thai Match)
        Bout(
            id = "bout_etfc002_habtamu_frezer",
            eventName = "ETFC 002: Adwa Fight Night",
            eventVenue = "Adwa 00 Museum — August 27",
            boutType = BoutType.EARLY_PRELIMS,
            weightClass = WeightClass.BANTAMWEIGHT,
            matchCategory = MatchCategory.MUAY_THAI,
            matchTitle = "MUAY THAI MATCH",
            fighterRed = fighters[16], // Habtamu
            fighterBlue = fighters[17], // Frezer
            isTitleFight = false,
            scheduledRounds = 3,
            status = BoutStatus.UPCOMING
        ),
        // 10. Zahara vs Yabsira (Muay Thai Match - Female)
        Bout(
            id = "bout_etfc002_zahara_yabsira",
            eventName = "ETFC 002: Adwa Fight Night",
            eventVenue = "Adwa 00 Museum — August 27",
            boutType = BoutType.EARLY_PRELIMS,
            weightClass = WeightClass.WOMENS_FLYWEIGHT,
            matchCategory = MatchCategory.MUAY_THAI,
            matchTitle = "MUAY THAI MATCH",
            fighterRed = fighters[18], // Zahara
            fighterBlue = fighters[19], // Yabsira
            isTitleFight = false,
            scheduledRounds = 3,
            status = BoutStatus.UPCOMING
        )
    )

    // --- Pay-Per-View Packages ---
    val ppvTiers: List<PpvTier> = listOf(
        PpvTier(
            id = "tier_main_event",
            title = "ETFC 002 Live PPV Pass",
            subtitle = "Full Access to Adwa Fight Night (All 10 Bouts)",
            priceEtb = 250,
            priceKes = 450,
            badge = "BEST SELLER",
            isPopular = true,
            features = listOf(
                "Full 1080p 60fps Ultra-ABR Live Arena Stream",
                "Instant unlock with Telebirr & M-Pesa",
                "Main Event: Sedo vs Jhonny (Middleweight Championship)",
                "All MMA, Boxing & Muay Thai Match cards",
                "Multi-Angle Corner Cam & Overhead Sky Cam",
                "Ultra-lean 240p Data Saver mode (150 MB/hr)",
                "48-hour Full VOD Replay & Offline Download access"
            )
        ),
        PpvTier(
            id = "tier_vip_ringside",
            title = "VIP Octagon Pass + Season Access",
            subtitle = "Adwa Fight Night + Next 3 ETFC World Championships",
            priceEtb = 600,
            priceKes = 1100,
            badge = "SEASON PASS",
            isPopular = false,
            features = listOf(
                "Everything in ETFC 002 Live Pass",
                "Next 3 ETFC Major Fight Cards Included",
                "Unlimited Full HD Offline Vault Downloads",
                "Exclusive Corner Audio & Cornerman Live Feed",
                "Adwa 00 Museum Backstage Fighter Interviews",
                "Priority Low-Latency Edge Node Allocation"
            )
        ),
        PpvTier(
            id = "tier_prelims_pass",
            title = "Early Prelims & Radio Stream",
            subtitle = "Live Radio commentary + 480p Muay Thai/Boxing Bouts",
            priceEtb = 90,
            priceKes = 180,
            badge = "BUDGET SAVER",
            isPopular = false,
            features = listOf(
                "Live Muay Thai & Boxing early matches",
                "Low-bandwidth live radio commentary (64 kbps)",
                "Live punch-by-punch statistics tracker",
                "Post-fight highlights & knockout replays"
            )
        )
    )

    // --- CDN Edge Nodes ---
    val cdnNodes: List<CdnNodeInfo> = listOf(
        CdnNodeInfo("addis-edge-01", "Addis Ababa", "Ethiopia", 14, "addis-rtmp.etfc.stream"),
        CdnNodeInfo("nairobi-edge-02", "Nairobi", "Kenya", 22, "nairobi-cdn.etfc.stream"),
        CdnNodeInfo("kampala-edge-01", "Kampala", "Uganda", 32, "kampala-hls.etfc.stream"),
        CdnNodeInfo("kigali-edge-03", "Kigali", "Rwanda", 38, "kigali-edge.etfc.stream")
    )

    // --- Fan Live Chat Initial Seed ---
    val initialChatMessages: List<FanChatMessage> = listOf(
        FanChatMessage("c1", "Yohannes M.", "🇪🇹", "Sedo is looking aggressive in round 3! That right hand landed flush!", "10s ago"),
        FanChatMessage("c2", "Brian Ochieng", "🇰🇪", "Jhonny's counter left hook is dangerous! Belt is on the line! 🥊", "22s ago"),
        FanChatMessage("c3", "Bethlehem T.", "🇪🇹", "Adwa 00 Museum is completely sold out! The energy is electric! 🔥🔥", "38s ago", isSuperFan = true, cheerEmoji = "🔥"),
        FanChatMessage("c4", "Samuel K.", "🇰🇪", "Can't wait for Stephen vs Rebik in Muay Thai! East Africa fighting spirit!", "1m ago"),
        FanChatMessage("c5", "Alazar G.", "🇪🇹", "ETFC 002 is history in the making! Sedo vs Jhonny for the gold 🏆", "2m ago", isSuperFan = true, cheerEmoji = "🏆")
    )

    // --- Payment Processing (M-Pesa Daraja STK Push & Telebirr API Simulation) ---
    suspend fun processPpvPayment(
        tier: PpvTier,
        provider: PaymentProvider,
        phoneNumber: String
    ): Result<PpvPassEntity> {
        delay(1800)

        val txPrefix = if (provider == PaymentProvider.MPESA) "MP" else "TB"
        val randomDigits = (100000..999999).random()
        val txRef = "$txPrefix${System.currentTimeMillis().toString().takeLast(4)}$randomDigits"

        val passEntity = PpvPassEntity(
            passId = UUID.randomUUID().toString(),
            tierId = tier.id,
            title = tier.title,
            eventName = "ETFC 002: Adwa Fight Night",
            priceCurrency = if (provider == PaymentProvider.MPESA) "KES" else "ETB",
            amountPaid = if (provider == PaymentProvider.MPESA) tier.priceKes.toDouble() else tier.priceEtb.toDouble(),
            paymentProvider = provider.displayName,
            transactionRef = txRef,
            phoneNumber = phoneNumber,
            status = "ACTIVE",
            purchaseTimestamp = System.currentTimeMillis(),
            validUntilTimestamp = System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000), // 7 days
            signatureToken = "ETFC-SECURE-TOKEN-${UUID.randomUUID().toString().take(12).uppercase()}"
        )

        dao.insertPpvPass(passEntity)
        return Result.success(passEntity)
    }

    // --- Download Match Simulation for Bandwidth Optimization ---
    suspend fun startMatchDownload(bout: Bout, quality: StreamQuality) {
        val sizeMultiplier = when (quality) {
            StreamQuality.SAVER_240P -> 48_000_000L // 48MB
            StreamQuality.SMOOTH_480P -> 160_000_000L // 160MB
            StreamQuality.HD_720P -> 450_000_000L // 450MB
            StreamQuality.HD_1080P -> 920_000_000L // 920MB
            else -> 60_000_000L
        }

        val downloadId = "dl_${bout.id}_${quality.name.lowercase()}"
        val initialEntity = DownloadedFightEntity(
            id = downloadId,
            boutId = bout.id,
            title = "${bout.fighterRed.name} vs ${bout.fighterBlue.name}",
            eventName = bout.eventName,
            redFighterName = bout.fighterRed.name,
            blueFighterName = bout.fighterBlue.name,
            weightClass = bout.weightClass.label,
            durationMinutes = 25,
            quality = quality.resolutionLabel,
            fileSizeBytes = sizeMultiplier,
            downloadProgress = 0.15f,
            isCompleted = false,
            downloadTimestamp = System.currentTimeMillis(),
            localFilePath = "/storage/etfc/encrypted_vault/${downloadId}.etfc_enc",
            encryptedToken = "ENC-VOD-${UUID.randomUUID().toString().take(8)}"
        )

        dao.insertDownloadedFight(initialEntity)
        
        // Progressively simulate fast edge CDN download
        delay(800)
        dao.updateDownloadProgress(downloadId, 0.55f, false)
        delay(900)
        dao.updateDownloadProgress(downloadId, 1.0f, true)
    }

    suspend fun deleteDownloadedFight(id: String) {
        dao.deleteDownloadedFight(id)
    }

    suspend fun savePrediction(boutId: String, eventName: String, fighter: Fighter, method: String, round: Int) {
        val entity = UserPredictionEntity(
            boutId = boutId,
            eventName = eventName,
            fighterPickId = fighter.id,
            fighterPickName = fighter.name,
            winMethod = method,
            roundPick = round,
            timestamp = System.currentTimeMillis()
        )
        dao.savePrediction(entity)
    }

    suspend fun toggleFavoriteFighter(fighter: Fighter, isFavorite: Boolean) {
        if (isFavorite) {
            dao.removeFavoriteFighter(fighter.id)
        } else {
            dao.addFavoriteFighter(
                FavoriteFighterEntity(
                    fighterId = fighter.id,
                    name = fighter.name,
                    nickname = fighter.nickname,
                    division = fighter.division.label,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // ==========================================
    // FIGHTER META-ENGINE REPOSITORY METHODS
    // ==========================================

    suspend fun seedFighterMetaEngineIfNeeded() {
        val profiles = FighterMetaDataGenerator.generateProfiles(fighters)
        val stats = FighterMetaDataGenerator.generateStats(fighters)
        val records = FighterMetaDataGenerator.generateFightRecordHistory()
        val weightHistories = FighterMetaDataGenerator.generateWeightClassHistory()

        dao.insertFighterProfiles(profiles)
        dao.insertAllFighterStats(stats)
        dao.insertFightRecords(records)
        dao.insertWeightClassHistories(weightHistories)
    }

    fun getAllFighterProfiles(): Flow<List<com.example.data.local.FighterProfileEntity>> = dao.getAllFighterProfiles()

    fun getFighterStats(fighterId: String): Flow<com.example.data.local.FighterStatsEntity?> = dao.getFighterStatsById(fighterId)

    fun getFightRecords(fighterId: String): Flow<List<com.example.data.local.FightRecordHistoryEntity>> = dao.getFightRecordsForFighter(fighterId)

    fun getWeightClassHistory(fighterId: String): Flow<List<com.example.data.local.WeightClassHistoryEntity>> = dao.getWeightClassHistoryForFighter(fighterId)

    // =========================================================
    // AUTHENTICATION & ROLE-BASED ACCESS CONTROL (RBAC)
    // =========================================================

    suspend fun seedDefaultUsersAndChatIfNeeded(primaryBoutId: String = "bout_sedo_jhonny") {
        val demoUsers = listOf(
            UserAccountEntity(
                userId = "usr_mod_01",
                email = "mod@etfc.stream",
                handle = "@etfc_mod",
                displayName = "ETFC Arena Moderator",
                passwordHash = SecurityUtils.hashPassword("mod123"),
                phoneNumber = "+251911000001",
                countryCode = "+251",
                flagEmoji = "🇪🇹",
                role = UserRole.MODERATOR.name,
                authToken = SecurityUtils.generateAuthToken("usr_mod_01", UserRole.MODERATOR.name),
                createdTimestamp = System.currentTimeMillis() - 86400000L * 30,
                lastLoginTimestamp = System.currentTimeMillis()
            ),
            UserAccountEntity(
                userId = "usr_vip_02",
                email = "vip@etfc.stream",
                handle = "@habesha_vip",
                displayName = "Yonas Habesha",
                passwordHash = SecurityUtils.hashPassword("vip123"),
                phoneNumber = "+251911000002",
                countryCode = "+251",
                flagEmoji = "🇪🇹",
                role = UserRole.VIP_MEMBER.name,
                authToken = SecurityUtils.generateAuthToken("usr_vip_02", UserRole.VIP_MEMBER.name),
                createdTimestamp = System.currentTimeMillis() - 86400000L * 14,
                lastLoginTimestamp = System.currentTimeMillis()
            ),
            UserAccountEntity(
                userId = "usr_fan_03",
                email = "fan@etfc.stream",
                handle = "@sheger_striker",
                displayName = "Dawit Striker",
                passwordHash = SecurityUtils.hashPassword("fan123"),
                phoneNumber = "+251911000003",
                countryCode = "+251",
                flagEmoji = "🇪🇹",
                role = UserRole.PPV_BUYER.name,
                authToken = SecurityUtils.generateAuthToken("usr_fan_03", UserRole.PPV_BUYER.name),
                createdTimestamp = System.currentTimeMillis() - 86400000L * 7,
                lastLoginTimestamp = System.currentTimeMillis()
            ),
            UserAccountEntity(
                userId = "usr_free_04",
                email = "free@etfc.stream",
                handle = "@addis_newbie",
                displayName = "Abel Addis",
                passwordHash = SecurityUtils.hashPassword("free123"),
                phoneNumber = "+251911000004",
                countryCode = "+251",
                flagEmoji = "🇪🇹",
                role = UserRole.FREE_USER.name,
                authToken = SecurityUtils.generateAuthToken("usr_free_04", UserRole.FREE_USER.name),
                createdTimestamp = System.currentTimeMillis() - 86400000L * 2,
                lastLoginTimestamp = System.currentTimeMillis()
            ),
            UserAccountEntity(
                userId = "usr_admin_00",
                email = "admin@etfc.stream",
                handle = "@etfc_admin",
                displayName = "ETFC Commissioner",
                passwordHash = SecurityUtils.hashPassword("admin123"),
                phoneNumber = "+251911000000",
                countryCode = "+251",
                flagEmoji = "🇪🇹",
                role = UserRole.ADMIN.name,
                authToken = SecurityUtils.generateAuthToken("usr_admin_00", UserRole.ADMIN.name),
                createdTimestamp = System.currentTimeMillis() - 86400000L * 90,
                lastLoginTimestamp = System.currentTimeMillis()
            )
        )

        dao.insertUsersIfNotExist(demoUsers)

        // Seed rich initial live chat messages with mentions & pinned mod announcement
        val seedMessages = listOf(
            ChatMessageEntity(
                messageId = "msg_pin_01",
                boutId = primaryBoutId,
                userId = "usr_mod_01",
                userHandle = "@etfc_mod",
                userName = "ETFC Arena Moderator",
                userFlag = "🇪🇹",
                userRole = UserRole.MODERATOR.name,
                messageText = "📢 OFFICIAL ANNOUNCEMENT: Welcome to ETFC 002: Adwa Fight Night live from Adwa 00 Museum! Main event scheduled for 5 championship rounds.",
                timestamp = System.currentTimeMillis() - 300_000,
                isSuperFan = true,
                isPinned = true,
                cheerEmoji = "🏆"
            ),
            ChatMessageEntity(
                messageId = "msg_02",
                boutId = primaryBoutId,
                userId = "usr_vip_02",
                userHandle = "@habesha_vip",
                userName = "Yonas Habesha",
                userFlag = "🇪🇹",
                userRole = UserRole.VIP_MEMBER.name,
                messageText = "Ringside audio feed is insane! @jhonny looks locked in for this title defense 🥊",
                timestamp = System.currentTimeMillis() - 180_000,
                isSuperFan = true,
                cheerEmoji = "🥊",
                mentionedHandles = "@jhonny"
            ),
            ChatMessageEntity(
                messageId = "msg_03",
                boutId = primaryBoutId,
                userId = "usr_fan_03",
                userHandle = "@sheger_striker",
                userName = "Dawit Striker",
                userFlag = "🇪🇹",
                userRole = UserRole.PPV_BUYER.name,
                messageText = "@sedo's hammer cross is dangerous! Round 3 is going to be wild! 🔥",
                timestamp = System.currentTimeMillis() - 120_000,
                cheerEmoji = "🔥",
                mentionedHandles = "@sedo"
            ),
            ChatMessageEntity(
                messageId = "msg_04",
                boutId = primaryBoutId,
                userId = "usr_nairobi_05",
                userHandle = "@nairobi_mma",
                userName = "Brian Ochieng",
                userFlag = "🇰🇪",
                userRole = UserRole.PPV_BUYER.name,
                messageText = "East Africa MMA on top! Watching live via Nairobi edge node 🇰🇪 @etfc_mod stream is silky smooth!",
                timestamp = System.currentTimeMillis() - 60_000,
                mentionedHandles = "@etfc_mod"
            ),
            ChatMessageEntity(
                messageId = "msg_05",
                boutId = primaryBoutId,
                userId = "usr_free_04",
                userHandle = "@addis_newbie",
                userName = "Abel Addis",
                userFlag = "🇪🇹",
                userRole = UserRole.FREE_USER.name,
                messageText = "@sedo vs @jhonny best fight in Ethiopian martial arts history! 🇪🇹",
                timestamp = System.currentTimeMillis() - 25_000,
                mentionedHandles = "@sedo,@jhonny"
            )
        )

        dao.insertChatMessages(seedMessages)

        // Seed Dynamic Content (Hero posters, YouTube videos, and news articles)
        seedDynamicContentIfEmpty()
    }

    suspend fun registerUser(
        email: String,
        handle: String,
        displayName: String,
        password: String,
        phoneNumber: String,
        countryCode: String = "+251",
        flagEmoji: String = "🇪🇹"
    ): AuthResult {
        if (email.isBlank() || !email.contains("@")) {
            return AuthResult.Error("Please enter a valid email address.")
        }
        val cleanHandle = if (handle.startsWith("@")) handle.trim() else "@${handle.trim()}"
        if (cleanHandle.length < 3) {
            return AuthResult.Error("User handle must be at least 3 characters.")
        }
        if (password.length < 4) {
            return AuthResult.Error("Password must be at least 4 characters.")
        }

        val existingByEmail = dao.getUserByEmail(email.trim().lowercase())
        if (existingByEmail != null) {
            return AuthResult.Error("An account with this email already exists.")
        }

        val existingByHandle = dao.getUserByHandle(cleanHandle)
        if (existingByHandle != null) {
            return AuthResult.Error("The handle $cleanHandle is already taken. Please pick another.")
        }

        val userId = "usr_${UUID.randomUUID().toString().take(8)}"
        val passwordHash = SecurityUtils.hashPassword(password)
        val role = UserRole.FREE_USER.name
        val token = SecurityUtils.generateAuthToken(userId, role)

        val newEntity = UserAccountEntity(
            userId = userId,
            email = email.trim().lowercase(),
            handle = cleanHandle,
            displayName = displayName.trim().ifEmpty { cleanHandle.removePrefix("@") },
            passwordHash = passwordHash,
            phoneNumber = phoneNumber.trim(),
            countryCode = countryCode,
            flagEmoji = flagEmoji,
            role = role,
            authToken = token,
            createdTimestamp = System.currentTimeMillis(),
            lastLoginTimestamp = System.currentTimeMillis()
        )

        dao.insertUser(newEntity)
        return AuthResult.Success(mapUserEntityToModel(newEntity), "Registration successful! Welcome to ETFC.")
    }

    suspend fun loginUser(identifier: String, password: String): AuthResult {
        val cleanId = identifier.trim().lowercase()
        val userEntity = dao.getUserByEmailOrHandle(cleanId)
            ?: dao.getUserByHandle("@${cleanId.removePrefix("@")}")

        if (userEntity == null) {
            return AuthResult.Error("Account not found. Please check your email or @handle.")
        }

        if (userEntity.isBanned) {
            return AuthResult.Error("This account has been suspended by ETFC Arena Administration.")
        }

        val isPasswordValid = SecurityUtils.verifyPassword(password, userEntity.passwordHash)
        if (!isPasswordValid) {
            return AuthResult.Error("Incorrect password. Please try again.")
        }

        val newToken = SecurityUtils.generateAuthToken(userEntity.userId, userEntity.role)
        dao.updateLastLogin(userEntity.userId, System.currentTimeMillis(), newToken)

        val updatedEntity = userEntity.copy(
            lastLoginTimestamp = System.currentTimeMillis(),
            authToken = newToken
        )

        return AuthResult.Success(mapUserEntityToModel(updatedEntity), "Welcome back, ${updatedEntity.displayName}!")
    }

    suspend fun switchUserRole(userId: String, newRole: UserRole) {
        dao.updateUserRole(userId, newRole.name)
    }

    fun getAllUsersFlow(): Flow<List<UserAccount>> = dao.getAllUsers().map { list ->
        list.map { mapUserEntityToModel(it) }
    }

    private fun mapUserEntityToModel(entity: UserAccountEntity): UserAccount {
        val parsedRole = try {
            UserRole.valueOf(entity.role)
        } catch (_: Exception) {
            UserRole.FREE_USER
        }
        return UserAccount(
            userId = entity.userId,
            email = entity.email,
            handle = entity.handle,
            displayName = entity.displayName,
            phoneNumber = entity.phoneNumber,
            countryCode = entity.countryCode,
            flagEmoji = entity.flagEmoji,
            role = parsedRole,
            authToken = entity.authToken,
            createdTimestamp = entity.createdTimestamp,
            lastLoginTimestamp = entity.lastLoginTimestamp,
            isBanned = entity.isBanned,
            isMutedUntilTimestamp = entity.isMutedUntilTimestamp
        )
    }

    // =========================================================
    // REAL-TIME LIVE CHAT & MODERATION ENGINE
    // =========================================================

    fun getChatMessagesForBout(boutId: String): Flow<List<ChatMessage>> =
        dao.getMessagesForBout(boutId).map { list ->
            list.map { entity ->
                val parsedRole = try {
                    UserRole.valueOf(entity.userRole)
                } catch (_: Exception) {
                    UserRole.FREE_USER
                }
                val mentions = if (entity.mentionedHandles.isNotBlank()) {
                    entity.mentionedHandles.split(",").map { it.trim() }
                } else emptyList()

                ChatMessage(
                    id = entity.messageId,
                    boutId = entity.boutId,
                    userId = entity.userId,
                    userHandle = entity.userHandle,
                    userName = entity.userName,
                    userFlag = entity.userFlag,
                    userRole = parsedRole,
                    messageText = entity.messageText,
                    timestamp = entity.timestamp,
                    isSuperFan = entity.isSuperFan,
                    isPinned = entity.isPinned,
                    isDeleted = entity.isDeleted,
                    deletedByMod = entity.deletedByMod,
                    flagCount = entity.flagCount,
                    cheerEmoji = entity.cheerEmoji,
                    mentionedHandles = mentions
                )
            }
        }

    fun getPinnedMessage(boutId: String): Flow<ChatMessage?> =
        dao.getPinnedMessageForBout(boutId).map { entity ->
            entity?.let {
                val parsedRole = try {
                    UserRole.valueOf(it.userRole)
                } catch (_: Exception) {
                    UserRole.FREE_USER
                }
                ChatMessage(
                    id = it.messageId,
                    boutId = it.boutId,
                    userId = it.userId,
                    userHandle = it.userHandle,
                    userName = it.userName,
                    userFlag = it.userFlag,
                    userRole = parsedRole,
                    messageText = it.messageText,
                    timestamp = it.timestamp,
                    isSuperFan = it.isSuperFan,
                    isPinned = true,
                    isDeleted = it.isDeleted,
                    cheerEmoji = it.cheerEmoji
                )
            }
        }

    suspend fun sendChatMessage(
        boutId: String,
        user: UserAccount,
        messageText: String,
        cheerEmoji: String? = null
    ): Result<ChatMessage> {
        if (user.isBanned) {
            return Result.failure(Exception("You have been banned from ETFC Live Chat."))
        }
        if (user.isMutedUntilTimestamp > System.currentTimeMillis()) {
            val remainingSec = (user.isMutedUntilTimestamp - System.currentTimeMillis()) / 1000
            return Result.failure(Exception("You are on timeout for another ${remainingSec}s."))
        }
        if (messageText.isBlank() && cheerEmoji == null) {
            return Result.failure(Exception("Message cannot be empty."))
        }

        // Extract mentions (@handle)
        val mentionRegex = Regex("@[A-Za-z0-9_]+")
        val mentions = mentionRegex.findAll(messageText).map { it.value }.toList()
        val mentionedJoined = mentions.joinToString(",")

        val msgId = "msg_${UUID.randomUUID().toString().take(10)}"
        val isSuperFan = user.role == UserRole.VIP_MEMBER || user.role == UserRole.PPV_BUYER || user.role.canModerate()

        val entity = ChatMessageEntity(
            messageId = msgId,
            boutId = boutId,
            userId = user.userId,
            userHandle = user.handle,
            userName = user.displayName,
            userFlag = user.flagEmoji,
            userRole = user.role.name,
            messageText = messageText.trim(),
            timestamp = System.currentTimeMillis(),
            isSuperFan = isSuperFan,
            isPinned = false,
            isDeleted = false,
            deletedByMod = null,
            flagCount = 0,
            cheerEmoji = cheerEmoji,
            mentionedHandles = mentionedJoined
        )

        dao.insertChatMessage(entity)

        return Result.success(
            ChatMessage(
                id = msgId,
                boutId = boutId,
                userId = user.userId,
                userHandle = user.handle,
                userName = user.displayName,
                userFlag = user.flagEmoji,
                userRole = user.role,
                messageText = messageText.trim(),
                timestamp = System.currentTimeMillis(),
                isSuperFan = isSuperFan,
                isPinned = false,
                isDeleted = false,
                cheerEmoji = cheerEmoji,
                mentionedHandles = mentions
            )
        )
    }

    suspend fun pinChatMessage(messageId: String, boutId: String, modUser: UserAccount) {
        if (!modUser.role.canModerate()) return
        dao.unpinAllMessagesForBout(boutId)
        dao.setMessagePinned(messageId, true)
        dao.insertModerationLog(
            ModerationLogEntity(
                logId = "log_${UUID.randomUUID().toString().take(8)}",
                modUserId = modUser.userId,
                modHandle = modUser.handle,
                targetUserId = null,
                targetMessageId = messageId,
                actionType = ModActionType.PIN_MESSAGE.name,
                reason = "Pinned official announcement to arena feed",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun unpinChatMessage(messageId: String, modUser: UserAccount) {
        if (!modUser.role.canModerate()) return
        dao.setMessagePinned(messageId, false)
    }

    suspend fun deleteChatMessage(messageId: String, modUser: UserAccount, reason: String = "Inappropriate content") {
        if (!modUser.role.canModerate()) return
        dao.deleteChatMessage(messageId, modUser.handle)
        dao.insertModerationLog(
            ModerationLogEntity(
                logId = "log_${UUID.randomUUID().toString().take(8)}",
                modUserId = modUser.userId,
                modHandle = modUser.handle,
                targetUserId = null,
                targetMessageId = messageId,
                actionType = ModActionType.DELETE_MESSAGE.name,
                reason = reason,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun timeoutUser(targetUserId: String, targetHandle: String, modUser: UserAccount, durationSeconds: Long = 60) {
        if (!modUser.role.canModerate()) return
        val muteUntil = System.currentTimeMillis() + (durationSeconds * 1000)
        dao.setUserMutedUntil(targetUserId, muteUntil)
        dao.insertModerationLog(
            ModerationLogEntity(
                logId = "log_${UUID.randomUUID().toString().take(8)}",
                modUserId = modUser.userId,
                modHandle = modUser.handle,
                targetUserId = targetUserId,
                targetMessageId = null,
                actionType = ModActionType.TIMEOUT_USER_60S.name,
                reason = "Timed out $targetHandle for ${durationSeconds}s",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun banUser(targetUserId: String, targetHandle: String, modUser: UserAccount, reason: String = "Terms of Service violation") {
        if (!modUser.role.canModerate()) return
        dao.setUserBanned(targetUserId, true)
        dao.purgeAllMessagesFromUser(targetUserId)
        dao.insertModerationLog(
            ModerationLogEntity(
                logId = "log_${UUID.randomUUID().toString().take(8)}",
                modUserId = modUser.userId,
                modHandle = modUser.handle,
                targetUserId = targetUserId,
                targetMessageId = null,
                actionType = ModActionType.BAN_USER.name,
                reason = "Banned $targetHandle: $reason",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun flagChatMessage(messageId: String) {
        dao.incrementFlagCount(messageId)
    }

    fun getAvailableMentionHandles(): List<Pair<String, String>> {
        // List of fighters + moderators + superfans for autocomplete suggestions
        val fighterHandles = fighters.map { fighter ->
            val cleanName = fighter.name.lowercase().replace(" ", "")
            Pair("@$cleanName", "${fighter.name} (${fighter.division.label} Fighter)")
        }
        val staffHandles = listOf(
            Pair("@etfc_mod", "ETFC Arena Moderator 🛡️"),
            Pair("@etfc_admin", "ETFC Official Commissioner ⚡"),
            Pair("@habesha_vip", "Yonas Habesha (VIP Passholder 👑)"),
            Pair("@sheger_striker", "Dawit Striker (PPV Member 🥊)"),
            Pair("@addis_newbie", "Abel Addis (Fan 🇪🇹)")
        )
        return fighterHandles + staffHandles
    }

    // --- Home Screen Sliding Hero Items: Official Match Promo Posters ---
    val heroSlides: List<HeroSlide> = listOf(
        // Poster 1: Sedo vs Jhonny (Championship Main Event)
        HeroSlide(
            id = "hero_poster_sedo_jhonny",
            type = HeroSlideType.MATCH_PROMO_POSTER,
            tag = "🏆 UNDISPUTED TITLE FIGHT",
            title = "SEDO vs JHONNY",
            subtitle = "ETFC 002 Middleweight Championship Bout • Belt on the Line",
            eventDateOrCountdown = "AUGUST 27 • ADWA 00 MUSEUM",
            venueOrLocation = "Adwa 00 Museum, Addis Ababa",
            primaryCtaText = "WATCH BOUT / BUY PPV",
            secondaryCtaText = "TALE OF THE TAPE",
            badgeColorHex = 0xFFFAB818,
            discountText = "ETFC 002 CHAMPIONSHIP BOUT",
            redFighterName = "SEDO",
            blueFighterName = "JHONNY",
            weightClassText = "MIDDLEWEIGHT (185 LBS)",
            matchCategoryBadge = "MMA BOUT",
            eventEdition = "ETFC 002",
            redFlag = "🇪🇹",
            blueFlag = "🇪🇹",
            hasChampionshipBelt = true,
            targetTab = "LIVE_ARENA",
            ppvTierId = "tier_main_event"
        ),
        // Poster 2: Kaleab Girma vs Elezer Mussie
        HeroSlide(
            id = "hero_poster_kaleab_elezer",
            type = HeroSlideType.MATCH_PROMO_POSTER,
            tag = "🥊 FEATURED MMA BOUT",
            title = "KALEAB GIRMA vs ELEZER MUSSIE",
            subtitle = "High-Stakes Welterweight MMA Clash • Adwa Fight Night",
            eventDateOrCountdown = "AUGUST 27 • ADWA 00 MUSEUM",
            venueOrLocation = "Adwa 00 Museum, Addis Ababa",
            primaryCtaText = "VIEW MATCHUP & STATS",
            secondaryCtaText = "TALE OF THE TAPE",
            badgeColorHex = 0xFFFF4B60,
            discountText = "ADWA FIGHT NIGHT",
            redFighterName = "KALEAB GIRMA",
            blueFighterName = "ELEZER MUSSIE",
            weightClassText = "WELTERWEIGHT (170 LBS)",
            matchCategoryBadge = "MMA BOUT",
            eventEdition = "ADWA FIGHT NIGHT",
            redFlag = "🇪🇹",
            blueFlag = "🇪🇹",
            hasChampionshipBelt = false,
            targetTab = "FIGHT_CARD"
        ),
        // Poster 3: Yeamlaksira vs Zahara
        HeroSlide(
            id = "hero_poster_yeamlaksira_zahara",
            type = HeroSlideType.MATCH_PROMO_POSTER,
            tag = "🔥 CO-MAIN MUAY THAI",
            title = "YEAMLAKSIRA vs ZAHARA",
            subtitle = "Undefeated Striking Showdown • Women's Flyweight Muay Thai",
            eventDateOrCountdown = "AUGUST 27 • ADWA 00 MUSEUM",
            venueOrLocation = "Adwa 00 Museum, Addis Ababa",
            primaryCtaText = "VIEW FIGHT DETAILS",
            secondaryCtaText = "FIGHTER PROFILE",
            badgeColorHex = 0xFF38BDF8,
            discountText = "ETFC 002 MUAY THAI",
            redFighterName = "YEAMLAKSIRA",
            blueFighterName = "ZAHARA",
            weightClassText = "WOMEN'S FLYWEIGHT (125 LBS)",
            matchCategoryBadge = "MUAY THAI MATCH",
            eventEdition = "ETFC 002",
            redFlag = "🇪🇹",
            blueFlag = "🇪🇹",
            hasChampionshipBelt = false,
            targetTab = "FIGHT_CARD"
        ),
        // Poster 4: Desalegn vs Surafel
        HeroSlide(
            id = "hero_poster_desalegn_surafel",
            type = HeroSlideType.MATCH_PROMO_POSTER,
            tag = "⚡ PRO BOXING SHOWDOWN",
            title = "DESALEGN vs SURAFEL",
            subtitle = "Middleweight Boxing Slugfest • Adwa Fight Night 002",
            eventDateOrCountdown = "AUGUST 27 • ADWA 00 MUSEUM",
            venueOrLocation = "Adwa 00 Museum, Addis Ababa",
            primaryCtaText = "VIEW BOXING CARD",
            secondaryCtaText = "TALE OF THE TAPE",
            badgeColorHex = 0xFFFAB818,
            discountText = "ETFC 002 BOXING",
            redFighterName = "DESALEGN",
            blueFighterName = "SURAFEL",
            weightClassText = "MIDDLEWEIGHT (160 LBS)",
            matchCategoryBadge = "BOXING MATCH",
            eventEdition = "ETFC 002",
            redFlag = "🇪🇹",
            blueFlag = "🇪🇹",
            hasChampionshipBelt = false,
            targetTab = "FIGHT_CARD"
        ),
        // Poster 5: Rebik Sani vs Stephen Okony
        HeroSlide(
            id = "hero_poster_rebik_stephen",
            type = HeroSlideType.MATCH_PROMO_POSTER,
            tag = "🌍 INTERNATIONAL CLASH",
            title = "REBIK SANI vs STEPHEN OKONY",
            subtitle = "Ethiopia 🇪🇹 vs South Sudan 🇸🇸 • Featherweight Muay Thai War",
            eventDateOrCountdown = "AUGUST 27 • ADWA 00 MUSEUM",
            venueOrLocation = "Adwa 00 Museum, Addis Ababa",
            primaryCtaText = "EXPLORE INTERNATIONAL BOUT",
            secondaryCtaText = "TALE OF THE TAPE",
            badgeColorHex = 0xFF10B981,
            discountText = "EAST AFRICA RIVALRY",
            redFighterName = "REBIK SANI",
            blueFighterName = "STEPHEN OKONY",
            weightClassText = "FEATHERWEIGHT (145 LBS)",
            matchCategoryBadge = "MUAY THAI MATCH",
            eventEdition = "ETFC 002",
            redFlag = "🇪🇹",
            blueFlag = "🇸🇸",
            hasChampionshipBelt = false,
            targetTab = "FIGHT_CARD"
        ),
        // Poster 6: Mesfin vs Abenezer
        HeroSlide(
            id = "hero_poster_mesfin_abenezer",
            type = HeroSlideType.MATCH_PROMO_POSTER,
            tag = "🥊 EXPLOSIVE BOXING",
            title = "MESFIN vs ABENEZER",
            subtitle = "Lightweight Boxing Grudge Match • 4 Rounds of High Octane Action",
            eventDateOrCountdown = "AUGUST 27 • ADWA 00 MUSEUM",
            venueOrLocation = "Adwa 00 Museum, Addis Ababa",
            primaryCtaText = "VIEW FIGHT CARD",
            secondaryCtaText = "TALE OF THE TAPE",
            badgeColorHex = 0xFFFF4B60,
            discountText = "ETFC 002 BOXING",
            redFighterName = "MESFIN",
            blueFighterName = "ABENEZER",
            weightClassText = "LIGHTWEIGHT (135 LBS)",
            matchCategoryBadge = "BOXING MATCH",
            eventEdition = "ETFC 002",
            redFlag = "🇪🇹",
            blueFlag = "🇪🇹",
            hasChampionshipBelt = false,
            targetTab = "FIGHT_CARD"
        ),
        // Poster 7: Esubalew vs Biniyam
        HeroSlide(
            id = "hero_poster_esubalew_biniyam",
            type = HeroSlideType.MATCH_PROMO_POSTER,
            tag = "🥊 PRO BOXING BATTLE",
            title = "ESUBALEW vs BINIYAM",
            subtitle = "Welterweight Boxing Technical Showcase • Adwa Fight Night",
            eventDateOrCountdown = "AUGUST 27 • ADWA 00 MUSEUM",
            venueOrLocation = "Adwa 00 Museum, Addis Ababa",
            primaryCtaText = "VIEW FIGHT DETAILS",
            secondaryCtaText = "TALE OF THE TAPE",
            badgeColorHex = 0xFFFAB818,
            discountText = "ETFC 002 BOXING",
            redFighterName = "ESUBALEW",
            blueFighterName = "BINIYAM",
            weightClassText = "WELTERWEIGHT (147 LBS)",
            matchCategoryBadge = "BOXING MATCH",
            eventEdition = "ETFC 002",
            redFlag = "🇪🇹",
            blueFlag = "🇪🇹",
            hasChampionshipBelt = false,
            targetTab = "FIGHT_CARD"
        ),
        // Poster 8: Robel vs Nikatehilina
        HeroSlide(
            id = "hero_poster_robel_nikatehilina",
            type = HeroSlideType.MATCH_PROMO_POSTER,
            tag = "💥 MAIN CARD MMA",
            title = "ROBEL vs NIKATEHILINA",
            subtitle = "Featherweight Cage Bout • Striking vs Submissions",
            eventDateOrCountdown = "AUGUST 27 • ADWA 00 MUSEUM",
            venueOrLocation = "Adwa 00 Museum, Addis Ababa",
            primaryCtaText = "VIEW MMA BOUT",
            secondaryCtaText = "TALE OF THE TAPE",
            badgeColorHex = 0xFF38BDF8,
            discountText = "ETFC 002 MMA",
            redFighterName = "ROBEL",
            blueFighterName = "NIKATEHILINA",
            weightClassText = "FEATHERWEIGHT (145 LBS)",
            matchCategoryBadge = "MMA MATCH",
            eventEdition = "ETFC 002",
            redFlag = "🇪🇹",
            blueFlag = "🇪🇹",
            hasChampionshipBelt = false,
            targetTab = "FIGHT_CARD"
        ),
        // Poster 9: Habtamu vs Frezer
        HeroSlide(
            id = "hero_poster_habtamu_frezer",
            type = HeroSlideType.MATCH_PROMO_POSTER,
            tag = "⚡ MUAY THAI WAR",
            title = "HABTAMU vs FREZER",
            subtitle = "Bantamweight Muay Thai Clinch & Elbow Showcase",
            eventDateOrCountdown = "AUGUST 27 • ADWA 00 MUSEUM",
            venueOrLocation = "Adwa 00 Museum, Addis Ababa",
            primaryCtaText = "VIEW FIGHT CARD",
            secondaryCtaText = "TALE OF THE TAPE",
            badgeColorHex = 0xFF10B981,
            discountText = "ETFC 002 MUAY THAI",
            redFighterName = "HABTAMU",
            blueFighterName = "FREZER",
            weightClassText = "BANTAMWEIGHT (135 LBS)",
            matchCategoryBadge = "MUAY THAI MATCH",
            eventEdition = "ETFC 002",
            redFlag = "🇪🇹",
            blueFlag = "🇪🇹",
            hasChampionshipBelt = false,
            targetTab = "FIGHT_CARD"
        )
    )

    // --- Archive / Previous Fight Videos: Official YouTube Playlist ---
    val youtubePlaylistUrl: String = "https://youtube.com/playlist?list=PLeRATZC0Qfoo&si=dk9LKYg2Tdcg-uVm"
    val youtubePlaylistId: String = "PLeRATZC0Qfoo"

    val previousVideos: List<PreviousFightVideo> = listOf(
        PreviousFightVideo(
            id = "yt_vod_01",
            title = "ETFC 002: Sedo vs Jhonny (Full Championship Bout)",
            subtitle = "Adwa Fight Night Main Event - Undisputed Middleweight Championship Battle",
            eventName = "ETFC 002: Adwa Fight Night",
            category = VideoCategory.FULL_REPLAY,
            durationText = "24:18",
            durationSeconds = 1458,
            viewsCountStr = "340K views",
            publishedTimeAgo = "ETFC Official Archive",
            fighterRedName = "Sedo",
            fighterBlueName = "Jhonny",
            resultMethod = "KO (Adwa Hammer)",
            finishRoundText = "Round 3 • 2:48",
            fileSizeMb = 310,
            isTitleFight = true,
            contentTier = ContentTier.FREE_PRELIMS,
            likesCount = 9820,
            youtubePlaylistUrl = youtubePlaylistUrl,
            youtubePlaylistId = youtubePlaylistId,
            youtubeVideoId = "0a8G81bZ7X0",
            thumbnailUrl = "https://img.youtube.com/vi/0a8G81bZ7X0/hqdefault.jpg",
            youtubeEmbedUrl = "https://www.youtube-nocookie.com/embed/0a8G81bZ7X0?autoplay=1&enablejsapi=1"
        ),
        PreviousFightVideo(
            id = "yt_vod_02",
            title = "Kaleab Girma vs Elezer Mussie (Full MMA Match)",
            subtitle = "High-stakes Adwa Fight Night Welterweight MMA Clash",
            eventName = "ETFC 002: Adwa Fight Night",
            category = VideoCategory.FULL_REPLAY,
            durationText = "16:45",
            durationSeconds = 1005,
            viewsCountStr = "215K views",
            publishedTimeAgo = "ETFC Official Archive",
            fighterRedName = "Kaleab Girma",
            fighterBlueName = "Elezer Mussie",
            resultMethod = "TKO (Ground & Pound)",
            finishRoundText = "Round 2 • 3:12",
            fileSizeMb = 195,
            isTitleFight = false,
            contentTier = ContentTier.FREE_PRELIMS,
            likesCount = 5430,
            youtubePlaylistUrl = youtubePlaylistUrl,
            youtubePlaylistId = youtubePlaylistId,
            youtubeVideoId = "M7lc1UVf-VE",
            thumbnailUrl = "https://img.youtube.com/vi/M7lc1UVf-VE/hqdefault.jpg",
            youtubeEmbedUrl = "https://www.youtube-nocookie.com/embed/M7lc1UVf-VE?autoplay=1&enablejsapi=1"
        ),
        PreviousFightVideo(
            id = "yt_vod_03",
            title = "Yeamlaksira vs Zahara (Muay Thai Co-Main Event)",
            subtitle = "Explosive Women's Flyweight Muay Thai Clash with relentless combinations",
            eventName = "ETFC 002: Adwa Fight Night",
            category = VideoCategory.FULL_REPLAY,
            durationText = "12:30",
            durationSeconds = 750,
            viewsCountStr = "188K views",
            publishedTimeAgo = "ETFC Official Archive",
            fighterRedName = "Yeamlaksira",
            fighterBlueName = "Zahara",
            resultMethod = "Unanimous Decision",
            finishRoundText = "3 Full Rounds",
            fileSizeMb = 160,
            isTitleFight = false,
            contentTier = ContentTier.FREE_PRELIMS,
            likesCount = 7610,
            youtubePlaylistUrl = youtubePlaylistUrl,
            youtubePlaylistId = youtubePlaylistId,
            youtubeVideoId = "9bZkp7q19f0",
            thumbnailUrl = "https://img.youtube.com/vi/9bZkp7q19f0/hqdefault.jpg",
            youtubeEmbedUrl = "https://www.youtube-nocookie.com/embed/9bZkp7q19f0?autoplay=1&enablejsapi=1"
        ),
        PreviousFightVideo(
            id = "yt_vod_04",
            title = "Rebik Sani 🇪🇹 vs Stephen Okony 🇸🇸 (Muay Thai War)",
            subtitle = "Ethiopia vs South Sudan international Muay Thai clash at Adwa 00 Museum",
            eventName = "ETFC 002: Adwa Fight Night",
            category = VideoCategory.FULL_REPLAY,
            durationText = "14:10",
            durationSeconds = 850,
            viewsCountStr = "290K views",
            publishedTimeAgo = "ETFC Official Archive",
            fighterRedName = "Rebik Sani",
            fighterBlueName = "Stephen Okony",
            resultMethod = "KO (Flying Knee)",
            finishRoundText = "Round 2 • 1:55",
            fileSizeMb = 180,
            isTitleFight = false,
            contentTier = ContentTier.FREE_PRELIMS,
            likesCount = 8340,
            youtubePlaylistUrl = youtubePlaylistUrl,
            youtubePlaylistId = youtubePlaylistId,
            youtubeVideoId = "kJQP7kiw5Fk",
            thumbnailUrl = "https://img.youtube.com/vi/kJQP7kiw5Fk/hqdefault.jpg",
            youtubeEmbedUrl = "https://www.youtube-nocookie.com/embed/kJQP7kiw5Fk?autoplay=1&enablejsapi=1"
        ),
        PreviousFightVideo(
            id = "yt_vod_05",
            title = "Desalegn vs Surafel (Boxing Match Slugfest)",
            subtitle = "Heavyweight punching power exchange across 4 intense rounds",
            eventName = "ETFC 002: Adwa Fight Night",
            category = VideoCategory.FULL_REPLAY,
            durationText = "18:05",
            durationSeconds = 1085,
            viewsCountStr = "142K views",
            publishedTimeAgo = "ETFC Official Archive",
            fighterRedName = "Desalegn",
            fighterBlueName = "Surafel",
            resultMethod = "Split Decision (39-37)",
            finishRoundText = "4 Full Rounds",
            fileSizeMb = 220,
            isTitleFight = false,
            contentTier = ContentTier.FREE_PRELIMS,
            likesCount = 3980,
            youtubePlaylistUrl = youtubePlaylistUrl,
            youtubePlaylistId = youtubePlaylistId,
            youtubeVideoId = "fJ9rUzIMcZQ",
            thumbnailUrl = "https://img.youtube.com/vi/fJ9rUzIMcZQ/hqdefault.jpg",
            youtubeEmbedUrl = "https://www.youtube-nocookie.com/embed/fJ9rUzIMcZQ?autoplay=1&enablejsapi=1"
        ),
        PreviousFightVideo(
            id = "yt_vod_06",
            title = "ETFC 002: Top Knockouts & Highlight Reel",
            subtitle = "The most brutal stoppages and highlight moments from Adwa Fight Night",
            eventName = "ETFC 002 Highlights",
            category = VideoCategory.KO_HIGHLIGHTS,
            durationText = "08:45",
            durationSeconds = 525,
            viewsCountStr = "520K views",
            publishedTimeAgo = "ETFC Official Archive",
            fighterRedName = "All Contenders",
            fighterBlueName = "ETFC Roster",
            resultMethod = "Knockout Reel",
            finishRoundText = "All Finishes",
            fileSizeMb = 115,
            isTitleFight = true,
            contentTier = ContentTier.FREE_PRELIMS,
            likesCount = 14200,
            youtubePlaylistUrl = youtubePlaylistUrl,
            youtubePlaylistId = youtubePlaylistId,
            youtubeVideoId = "kXYiU_JCYtU",
            thumbnailUrl = "https://img.youtube.com/vi/kXYiU_JCYtU/hqdefault.jpg",
            youtubeEmbedUrl = "https://www.youtube-nocookie.com/embed/kXYiU_JCYtU?autoplay=1&enablejsapi=1"
        ),
        PreviousFightVideo(
            id = "yt_vod_07",
            title = "Mesfin vs Abenezer (Full Boxing Match)",
            subtitle = "Pugilistic masterclass and counter-striking showcase from Adwa 00 Museum",
            eventName = "ETFC 002: Adwa Fight Night",
            category = VideoCategory.FULL_REPLAY,
            durationText = "15:20",
            durationSeconds = 920,
            viewsCountStr = "128K views",
            publishedTimeAgo = "ETFC Official Archive",
            fighterRedName = "Mesfin",
            fighterBlueName = "Abenezer",
            resultMethod = "Unanimous Decision",
            finishRoundText = "4 Full Rounds",
            fileSizeMb = 190,
            isTitleFight = false,
            contentTier = ContentTier.FREE_PRELIMS,
            likesCount = 3110,
            youtubePlaylistUrl = youtubePlaylistUrl,
            youtubePlaylistId = youtubePlaylistId,
            youtubeVideoId = "C0DPdy98e4c",
            thumbnailUrl = "https://img.youtube.com/vi/C0DPdy98e4c/hqdefault.jpg",
            youtubeEmbedUrl = "https://www.youtube-nocookie.com/embed/C0DPdy98e4c?autoplay=1&enablejsapi=1"
        ),
        PreviousFightVideo(
            id = "yt_vod_08",
            title = "Post-Fight Press Conference & Bonuses: ETFC 002",
            subtitle = "Fighters react to dramatic finishes, bonus awards, and upcoming title challenges",
            eventName = "ETFC 002 Media Day",
            category = VideoCategory.PRESS_CONFERENCE,
            durationText = "32:40",
            durationSeconds = 1960,
            viewsCountStr = "95K views",
            publishedTimeAgo = "ETFC Official Archive",
            fighterRedName = "Adwa Roster",
            fighterBlueName = "Media Desk",
            resultMethod = "Press Briefing",
            finishRoundText = "Full Q&A",
            fileSizeMb = 240,
            isTitleFight = false,
            contentTier = ContentTier.FREE_PRELIMS,
            likesCount = 2890,
            youtubePlaylistUrl = youtubePlaylistUrl,
            youtubePlaylistId = youtubePlaylistId,
            youtubeVideoId = "RgKAFK5djSk",
            thumbnailUrl = "https://img.youtube.com/vi/RgKAFK5djSk/hqdefault.jpg",
            youtubeEmbedUrl = "https://www.youtube-nocookie.com/embed/RgKAFK5djSk?autoplay=1&enablejsapi=1"
        )
    )

    // --- News Blogs, Announcements & Press Releases ---
    val newsArticles: List<NewsArticle> = listOf(
        NewsArticle(
            id = "news_01",
            title = "ETFC Expands African MMA Broadcast with 1-Tap Telebirr & M-Pesa Micro-Payments",
            summary = "Fans across Ethiopia and Kenya can now secure 4K Pay-Per-View passes seamlessly using local mobile wallets with instant edge CDN streaming access.",
            fullContent = """
                ADDIS ABABA — Ethiopian Top Fight Championship (ETFC) today announced the full commercial rollout of its next-generation live sports streaming infrastructure, purpose-built for East African mobile connectivity and zero-friction micropayments.

                Starting with the upcoming ETFC 003: Great Rift Valley Clash at Millennium Hall, fight fans can purchase Early Bird passes with a 35% discount directly through Telebirr (Ethiopia) and Safaricom M-Pesa (Kenya). The integrated payment flow completes in under 3 seconds without requiring international credit cards.

                "Our mission is to celebrate authentic African martial arts excellence and make high-definition championship broadcasts accessible to every enthusiast from Addis to Nairobi," said the ETFC Commissioner. "By deploying edge CDN caching nodes across Addis Ababa, Nairobi, and Mombasa, we deliver ultra-low latency sub-second live feeds even on variable mobile cellular networks."

                In addition to live streaming, users gain offline vault caching capabilities to download full fight replays over Wi-Fi and watch anywhere without incurring data charges.
            """.trimIndent(),
            category = NewsCategory.PRESS_RELEASE,
            author = "ETFC Communications Team",
            authorRole = "Official Press Bureau",
            publishedDate = "August 28, 2026",
            readTimeMinutes = 3,
            isFeatured = true,
            tags = listOf("Broadcast", "Telebirr", "M-Pesa", "PPV", "East Africa"),
            relatedFighterNames = listOf("Elezar", "Stephen", "Sedo")
        ),
        NewsArticle(
            id = "news_02",
            title = "Early Bird Window Open: 35% Discount for ETFC 003 Welterweight Championship",
            summary = "Grab the Early Bird PPV ticket for only 250 ETB / KES 450. Includes multi-cam corner feeds, ref audio, and encrypted replay downloads.",
            fullContent = """
                The official Early Bird pre-sale window for ETFC 003: Great Rift Valley Clash is now live across the platform.

                Taking place live from the Millennium Hall in Addis Ababa, the main event will see undefeated knockout specialist Elezar 'The Lion' clash with Kenyan powerhouse Stephen 'Thunder' in a 5-round undisputed Welterweight title showdown.

                Early Bird Pass Perks:
                • Full Live 1080p 60fps Broadcast + ABR adaptive ladder
                • Multi-camera corner switcher (Red Corner, Blue Corner, Ref Cam)
                • Live Arena Chat with Gold Supporter badge
                • 4K Encrypted offline fight replay downloads

                To redeem the 35% savings, navigate to the PPV Passes tab or tap the Early Bird Hero banner on the home screen.
            """.trimIndent(),
            category = NewsCategory.TICKETS_PPV,
            author = "Ticket Operations Desk",
            authorRole = "Ticketing & Fan Experience",
            publishedDate = "August 27, 2026",
            readTimeMinutes = 2,
            isFeatured = false,
            tags = listOf("Early Bird", "ETFC 003", "Discount", "Tickets"),
            relatedFighterNames = listOf("Elezar", "Stephen")
        ),
        NewsArticle(
            id = "news_03",
            title = "Inside Mount Entoto Camp: How Altitude Conditioning is Transforming Ethiopian Fighters",
            summary = "Training at 3,000 meters above sea level produces unmatched oxygen efficiency and fourth-round cardio supremacy.",
            fullContent = """
                Nestled in the eucalyptus-covered ridges overlooking Addis Ababa, Mount Entoto has long been the world's most feared proving ground for Olympic distance runners. Now, ETFC's championship athletes are turning the same thin air into a lethal weapon inside the cage.

                Head Coach Yonas explains: "When fighters spar at 3,000 meters, their red blood cell density and VO2 max skyrocket. By the time they step into the climate-controlled arena, 5 rounds of championship pressure feel like an easy warm-up."

                Middleweight champion Sedo spent 8 weeks in the Entoto compound preparing for his title defense, logging over 100 miles of mountain sprints and grueling grappling circuits. The results speak for themselves: an average output of 68 significant strikes per round with zero visible fatigue.
            """.trimIndent(),
            category = NewsCategory.TRAINING_CAMP,
            author = "Dawit Tadesse",
            authorRole = "Senior MMA Analyst",
            publishedDate = "August 25, 2026",
            readTimeMinutes = 4,
            isFeatured = false,
            tags = listOf("Mount Entoto", "Conditioning", "Altitude", "MMA Science"),
            relatedFighterNames = listOf("Sedo", "Desalegn")
        ),
        NewsArticle(
            id = "news_04",
            title = "Real-Time Arena Live Chat: User Mentions, Superfans & Anti-Toxicity Moderation",
            summary = "ETFC launches real-time chat with autocomplete mentions (@fighter), slow mode regulation, and verified staff moderation badges.",
            fullContent = """
                ETFC has officially rolled out its real-time interactive live fan arena chat engine, giving viewers a communal stadium atmosphere right on their mobile screens.

                Key Features:
                • @ Mentions: Type '@' to autocomplete mentions for fighters, champions, and arena moderators.
                • Dynamic Role Badges: Distinct visual styling for Guests, Free Users, PPV Passholders, VIP Ringside, and Certified Moderators.
                • Arena Cheer Reactions: Instant tap reactions with crowd meters updating in real time.
                • Comprehensive Moderation: Certified moderators can pin announcements, delete harmful messages, enforce 60s cooldowns, and apply slow-mode pacing.
            """.trimIndent(),
            category = NewsCategory.TECH_ANNOUNCEMENT,
            author = "ETFC Platform Engineering",
            authorRole = "Core Tech Team",
            publishedDate = "August 24, 2026",
            readTimeMinutes = 3,
            isFeatured = false,
            tags = listOf("Live Chat", "Mentions", "Moderation", "Community")
        ),
        NewsArticle(
            id = "news_05",
            title = "Fight Breakdown: Why Sedo vs Desalegn Was the Clash of the Century",
            summary = "A round-by-round tactical analysis of the striking exchanges, cage control maneuvers, and decisive grappling scrambles.",
            fullContent = """
                The Adwa 00 Museum arena was electric as Sedo and Desalegn battled through fierce exchanges.

                From the opening bell, Desalegn utilized crisp straight jabs and low calf kicks to disrupt Sedo's explosive blitzes. However, Sedo's relentless forward pressure and thunderous body hooks steadily wore down the challenger's defenses in the championship rounds.

                Telemetry Data Recap:
                • Sedo: 52 Significant Strikes (72% accuracy), 1 Takedown, 58% Cage Control.
                • Desalegn: 41 Significant Strikes (61% accuracy), 2 Takedowns, 42% Cage Control.
            """.trimIndent(),
            category = NewsCategory.FIGHT_NEWS,
            author = "Eskinder Lemma",
            authorRole = "ETFC Color Commentator",
            publishedDate = "August 22, 2026",
            readTimeMinutes = 4,
            isFeatured = false,
            tags = listOf("Fight Recap", "Adwa Museum", "Telemetry", "MMA"),
            relatedFighterNames = listOf("Sedo", "Desalegn")
        )
    )

    // ==========================================
    // DYNAMIC PERSISTENCE, ADMIN CRUD & GITHUB SYNC
    // ==========================================

    suspend fun seedDynamicContentIfEmpty() {
        // Seed Hero Slides
        val initialSlides = heroSlides.mapIndexed { index, slide ->
            slide.toEntity(order = index)
        }
        dao.insertHeroSlides(initialSlides)

        // Seed Previous Videos
        val initialVideos = previousVideos.mapIndexed { index, video ->
            video.toEntity(order = index)
        }
        dao.insertVideos(initialVideos)

        // Seed News Articles
        val initialNews = newsArticles.map { it.toEntity() }
        dao.insertNewsArticles(initialNews)
    }

    suspend fun saveHeroSlide(slide: HeroSlide, isPinned: Boolean = false) {
        dao.insertHeroSlide(slide.toEntity(isPinned = isPinned))
    }

    suspend fun deleteHeroSlide(slideId: String) {
        dao.deleteHeroSlide(slideId)
    }

    suspend fun savePreviousVideo(video: PreviousFightVideo, displayOrder: Int = 0) {
        dao.insertVideo(video.toEntity(order = displayOrder))
    }

    suspend fun deletePreviousVideo(videoId: String) {
        dao.deleteVideo(videoId)
    }

    suspend fun saveNewsArticle(article: NewsArticle) {
        dao.insertNewsArticle(article.toEntity())
    }

    suspend fun deleteNewsArticle(articleId: String) {
        dao.deleteNewsArticle(articleId)
    }

    suspend fun updateFighterProfile(
        fighterId: String,
        name: String,
        nickname: String,
        country: String,
        flag: String,
        bio: String,
        stance: String,
        signatureMove: String,
        reachInches: Int,
        heightCm: Int,
        photoUrl: String?
    ) {
        val existing = dao.getFighterProfileDirect(fighterId)
        val baseFighter = fighters.firstOrNull { it.id == fighterId }
        val updated = FighterProfileEntity(
            fighterId = fighterId,
            name = name,
            nickname = nickname,
            countryName = country,
            flagEmoji = flag,
            division = existing?.division ?: baseFighter?.division?.label ?: "Middleweight",
            stance = stance,
            reachInches = reachInches,
            heightCm = heightCm,
            isChampion = existing?.isChampion ?: baseFighter?.isChampion ?: false,
            rank = existing?.rank ?: baseFighter?.rank ?: 1,
            bio = bio,
            signatureMove = signatureMove,
            primaryColorHex = existing?.primaryColorHex ?: baseFighter?.primaryColorHex ?: 0xFFEF4444,
            hairStyle = existing?.hairStyle ?: baseFighter?.hairStyle?.name ?: "FADE_BEARD",
            discipline = existing?.discipline ?: baseFighter?.discipline?.name ?: "MMA",
            lastUpdatedTimestamp = System.currentTimeMillis(),
            photoUrl = photoUrl
        )
        dao.insertFighterProfile(updated)
    }

    // --- JSON Serialization for GitHub Repository Sync ---

    fun exportToGitHubJson(): String {
        val root = JSONObject()
        root.put("version", 1)
        root.put("schema", "ETFC_CONTENT_SYNC_V1")
        root.put("exportedAt", System.currentTimeMillis())

        // Hero Slides Array
        val slidesArray = JSONArray()
        heroSlides.forEach { slide ->
            val obj = JSONObject()
            obj.put("id", slide.id)
            obj.put("type", slide.type.name)
            obj.put("tag", slide.tag)
            obj.put("title", slide.title)
            obj.put("subtitle", slide.subtitle)
            obj.put("eventDateOrCountdown", slide.eventDateOrCountdown)
            obj.put("venueOrLocation", slide.venueOrLocation)
            obj.put("primaryCtaText", slide.primaryCtaText)
            obj.put("secondaryCtaText", slide.secondaryCtaText ?: "")
            obj.put("badgeColorHex", slide.badgeColorHex)
            obj.put("discountText", slide.discountText ?: "")
            obj.put("redFighterName", slide.redFighterName ?: "")
            obj.put("blueFighterName", slide.blueFighterName ?: "")
            obj.put("weightClassText", slide.weightClassText ?: "")
            obj.put("matchCategoryBadge", slide.matchCategoryBadge ?: "")
            obj.put("eventEdition", slide.eventEdition)
            obj.put("redFlag", slide.redFlag)
            obj.put("blueFlag", slide.blueFlag)
            obj.put("hasChampionshipBelt", slide.hasChampionshipBelt)
            obj.put("targetTab", slide.targetTab)
            obj.put("ppvTierId", slide.ppvTierId ?: "")
            obj.put("posterImageUrl", slide.posterImageUrl ?: "")
            slidesArray.put(obj)
        }
        root.put("heroSlides", slidesArray)

        // Previous Videos Array
        val videosArray = JSONArray()
        previousVideos.forEach { video ->
            val obj = JSONObject()
            obj.put("id", video.id)
            obj.put("title", video.title)
            obj.put("subtitle", video.subtitle)
            obj.put("eventName", video.eventName)
            obj.put("category", video.category.name)
            obj.put("durationText", video.durationText)
            obj.put("durationSeconds", video.durationSeconds)
            obj.put("viewsCountStr", video.viewsCountStr)
            obj.put("publishedTimeAgo", video.publishedTimeAgo)
            obj.put("fighterRedName", video.fighterRedName)
            obj.put("fighterBlueName", video.fighterBlueName)
            obj.put("resultMethod", video.resultMethod)
            obj.put("finishRoundText", video.finishRoundText)
            obj.put("fileSizeMb", video.fileSizeMb)
            obj.put("resolutionLabel", video.resolutionLabel)
            obj.put("isTitleFight", video.isTitleFight)
            obj.put("contentTier", video.contentTier.name)
            obj.put("likesCount", video.likesCount)
            obj.put("youtubePlaylistUrl", video.youtubePlaylistUrl)
            obj.put("youtubePlaylistId", video.youtubePlaylistId)
            obj.put("youtubeVideoId", video.youtubeVideoId)
            obj.put("thumbnailUrl", video.thumbnailUrl)
            obj.put("youtubeEmbedUrl", video.youtubeEmbedUrl)
            videosArray.put(obj)
        }
        root.put("previousVideos", videosArray)

        // News Announcements Array
        val newsArray = JSONArray()
        newsArticles.forEach { article ->
            val obj = JSONObject()
            obj.put("id", article.id)
            obj.put("title", article.title)
            obj.put("summary", article.summary)
            obj.put("fullContent", article.fullContent)
            obj.put("category", article.category.name)
            obj.put("author", article.author)
            obj.put("authorRole", article.authorRole)
            obj.put("publishedDate", article.publishedDate)
            obj.put("readTimeMinutes", article.readTimeMinutes)
            obj.put("isFeatured", article.isFeatured)
            obj.put("tags", article.tags.joinToString(","))
            obj.put("relatedFighterNames", article.relatedFighterNames.joinToString(","))
            obj.put("imageUrl", article.imageUrl ?: "")
            newsArray.put(obj)
        }
        root.put("newsArticles", newsArray)

        // Fighters Profile Array
        val fightersArray = JSONArray()
        fighters.forEach { fighter ->
            val obj = JSONObject()
            obj.put("fighterId", fighter.id)
            obj.put("name", fighter.name)
            obj.put("nickname", fighter.nickname)
            obj.put("countryName", fighter.countryName)
            obj.put("flagEmoji", fighter.flagEmoji)
            obj.put("division", fighter.division.label)
            obj.put("stance", fighter.stance)
            obj.put("reachInches", fighter.reachInches)
            obj.put("heightCm", fighter.heightCm)
            obj.put("isChampion", fighter.isChampion)
            obj.put("rank", fighter.rank)
            obj.put("bio", fighter.bio)
            obj.put("signatureMove", fighter.signatureMove)
            obj.put("primaryColorHex", fighter.primaryColorHex)
            obj.put("photoUrl", fighter.photoUrl ?: "")
            fightersArray.put(obj)
        }
        root.put("fighters", fightersArray)

        return root.toString(2)
    }

    suspend fun syncFromGitHubJson(jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            var totalItemsSynced = 0

            // 1. Sync Hero Slides
            if (root.has("heroSlides")) {
                val slidesArray = root.getJSONArray("heroSlides")
                val slideEntities = mutableListOf<HeroSlideEntity>()
                for (i in 0 until slidesArray.length()) {
                    val obj = slidesArray.getJSONObject(i)
                    slideEntities.add(
                        HeroSlideEntity(
                            id = obj.optString("id", "slide_$i"),
                            type = obj.optString("type", HeroSlideType.MATCH_PROMO_POSTER.name),
                            tag = obj.optString("tag", "🔥 FEATURED"),
                            title = obj.optString("title", "ETFC EVENT"),
                            subtitle = obj.optString("subtitle", ""),
                            eventDateOrCountdown = obj.optString("eventDateOrCountdown", "COMING SOON"),
                            venueOrLocation = obj.optString("venueOrLocation", "Adwa 00 Museum"),
                            primaryCtaText = obj.optString("primaryCtaText", "VIEW MATCH"),
                            secondaryCtaText = obj.optString("secondaryCtaText").takeIf { it.isNotBlank() },
                            badgeColorHex = obj.optLong("badgeColorHex", 0xFFFAB818),
                            discountText = obj.optString("discountText").takeIf { it.isNotBlank() },
                            redFighterName = obj.optString("redFighterName").takeIf { it.isNotBlank() },
                            blueFighterName = obj.optString("blueFighterName").takeIf { it.isNotBlank() },
                            weightClassText = obj.optString("weightClassText").takeIf { it.isNotBlank() },
                            matchCategoryBadge = obj.optString("matchCategoryBadge").takeIf { it.isNotBlank() },
                            eventEdition = obj.optString("eventEdition", "ETFC 002"),
                            redFlag = obj.optString("redFlag", "🇪🇹"),
                            blueFlag = obj.optString("blueFlag", "🇪🇹"),
                            hasChampionshipBelt = obj.optBoolean("hasChampionshipBelt", false),
                            targetTab = obj.optString("targetTab", "FIGHT_CARD"),
                            ppvTierId = obj.optString("ppvTierId").takeIf { it.isNotBlank() },
                            posterImageUrl = obj.optString("posterImageUrl").takeIf { it.isNotBlank() },
                            displayOrder = i,
                            isPinned = obj.optBoolean("isPinned", false)
                        )
                    )
                }
                if (slideEntities.isNotEmpty()) {
                    dao.clearAllHeroSlides()
                    dao.insertHeroSlides(slideEntities)
                    totalItemsSynced += slideEntities.size
                }
            }

            // 2. Sync Previous Fight Videos
            if (root.has("previousVideos")) {
                val videosArray = root.getJSONArray("previousVideos")
                val videoEntities = mutableListOf<PreviousVideoEntity>()
                for (i in 0 until videosArray.length()) {
                    val obj = videosArray.getJSONObject(i)
                    val vId = obj.optString("youtubeVideoId", "0a8G81bZ7X0")
                    videoEntities.add(
                        PreviousVideoEntity(
                            id = obj.optString("id", "yt_vod_$i"),
                            title = obj.optString("title", "ETFC Match Replay"),
                            subtitle = obj.optString("subtitle", "Adwa Fight Night Archive"),
                            eventName = obj.optString("eventName", "ETFC 002: Adwa Fight Night"),
                            category = obj.optString("category", VideoCategory.FULL_REPLAY.name),
                            durationText = obj.optString("durationText", "15:00"),
                            durationSeconds = obj.optInt("durationSeconds", 900),
                            viewsCountStr = obj.optString("viewsCountStr", "100K views"),
                            publishedTimeAgo = obj.optString("publishedTimeAgo", "ETFC Archive"),
                            fighterRedName = obj.optString("fighterRedName", "Red Corner"),
                            fighterBlueName = obj.optString("fighterBlueName", "Blue Corner"),
                            resultMethod = obj.optString("resultMethod", "Decision"),
                            finishRoundText = obj.optString("finishRoundText", "Full Bout"),
                            fileSizeMb = obj.optInt("fileSizeMb", 180),
                            resolutionLabel = obj.optString("resolutionLabel", "1080p HD"),
                            isTitleFight = obj.optBoolean("isTitleFight", false),
                            contentTier = obj.optString("contentTier", ContentTier.FREE_PRELIMS.name),
                            likesCount = obj.optInt("likesCount", 1000),
                            videoUrl = obj.optString("videoUrl", ""),
                            youtubePlaylistUrl = obj.optString("youtubePlaylistUrl", youtubePlaylistUrl),
                            youtubePlaylistId = obj.optString("youtubePlaylistId", youtubePlaylistId),
                            youtubeVideoId = vId,
                            thumbnailUrl = obj.optString("thumbnailUrl", "https://img.youtube.com/vi/$vId/hqdefault.jpg"),
                            youtubeEmbedUrl = obj.optString("youtubeEmbedUrl", "https://www.youtube-nocookie.com/embed/$vId?autoplay=1&enablejsapi=1"),
                            displayOrder = i
                        )
                    )
                }
                if (videoEntities.isNotEmpty()) {
                    dao.clearAllVideos()
                    dao.insertVideos(videoEntities)
                    totalItemsSynced += videoEntities.size
                }
            }

            // 3. Sync News & Announcements
            if (root.has("newsArticles")) {
                val newsArray = root.getJSONArray("newsArticles")
                val newsEntities = mutableListOf<NewsArticleEntity>()
                for (i in 0 until newsArray.length()) {
                    val obj = newsArray.getJSONObject(i)
                    newsEntities.add(
                        NewsArticleEntity(
                            id = obj.optString("id", "news_$i"),
                            title = obj.optString("title", "ETFC Update"),
                            summary = obj.optString("summary", ""),
                            fullContent = obj.optString("fullContent", ""),
                            category = obj.optString("category", NewsCategory.PRESS_RELEASE.name),
                            author = obj.optString("author", "ETFC Desk"),
                            authorRole = obj.optString("authorRole", "Official Media"),
                            publishedDate = obj.optString("publishedDate", "Recent"),
                            readTimeMinutes = obj.optInt("readTimeMinutes", 3),
                            isFeatured = obj.optBoolean("isFeatured", false),
                            tags = obj.optString("tags", ""),
                            relatedFighterNames = obj.optString("relatedFighterNames", ""),
                            imageUrl = obj.optString("imageUrl").takeIf { it.isNotBlank() },
                            timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                        )
                    )
                }
                if (newsEntities.isNotEmpty()) {
                    dao.clearAllNewsArticles()
                    dao.insertNewsArticles(newsEntities)
                    totalItemsSynced += newsEntities.size
                }
            }

            // 4. Sync Fighter Profiles & Photos
            if (root.has("fighters")) {
                val fightersArray = root.getJSONArray("fighters")
                val profileEntities = mutableListOf<FighterProfileEntity>()
                for (i in 0 until fightersArray.length()) {
                    val obj = fightersArray.getJSONObject(i)
                    val fId = obj.optString("fighterId", "f_$i")
                    val baseFighter = fighters.firstOrNull { it.id == fId }
                    profileEntities.add(
                        FighterProfileEntity(
                            fighterId = fId,
                            name = obj.optString("name", baseFighter?.name ?: "Fighter"),
                            nickname = obj.optString("nickname", baseFighter?.nickname ?: ""),
                            countryName = obj.optString("countryName", baseFighter?.countryName ?: "Ethiopia"),
                            flagEmoji = obj.optString("flagEmoji", baseFighter?.flagEmoji ?: "🇪🇹"),
                            division = obj.optString("division", baseFighter?.division?.label ?: "Middleweight"),
                            stance = obj.optString("stance", baseFighter?.stance ?: "Orthodox"),
                            reachInches = obj.optInt("reachInches", baseFighter?.reachInches ?: 72),
                            heightCm = obj.optInt("heightCm", baseFighter?.heightCm ?: 180),
                            isChampion = obj.optBoolean("isChampion", baseFighter?.isChampion ?: false),
                            rank = obj.optInt("rank", baseFighter?.rank ?: 1),
                            bio = obj.optString("bio", baseFighter?.bio ?: ""),
                            signatureMove = obj.optString("signatureMove", baseFighter?.signatureMove ?: ""),
                            primaryColorHex = obj.optLong("primaryColorHex", baseFighter?.primaryColorHex ?: 0xFFEF4444),
                            hairStyle = obj.optString("hairStyle", baseFighter?.hairStyle?.name ?: "FADE_BEARD"),
                            discipline = obj.optString("discipline", baseFighter?.discipline?.name ?: "MMA"),
                            lastUpdatedTimestamp = System.currentTimeMillis(),
                            photoUrl = obj.optString("photoUrl").takeIf { it.isNotBlank() }
                        )
                    )
                }
                if (profileEntities.isNotEmpty()) {
                    dao.insertFighterProfiles(profileEntities)
                    totalItemsSynced += profileEntities.size
                }
            }

            Result.success(totalItemsSynced)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchAndSyncFromRemoteUrl(urlString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 10000
                readTimeout = 10000
                requestMethod = "GET"
                setRequestProperty("Accept", "application/json")
            }
            if (connection.responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val jsonString = reader.readText()
                reader.close()
                syncFromGitHubJson(jsonString)
            } else {
                Result.failure(Exception("HTTP error code: ${connection.responseCode}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetDynamicDataToDefault() {
        dao.clearAllHeroSlides()
        dao.clearAllVideos()
        dao.clearAllNewsArticles()
        seedDynamicContentIfEmpty()
    }
}

// Extension functions for entity mapping
fun HeroSlide.toEntity(order: Int = 0, isPinned: Boolean = false): HeroSlideEntity {
    return HeroSlideEntity(
        id = this.id,
        type = this.type.name,
        tag = this.tag,
        title = this.title,
        subtitle = this.subtitle,
        eventDateOrCountdown = this.eventDateOrCountdown,
        venueOrLocation = this.venueOrLocation,
        primaryCtaText = this.primaryCtaText,
        secondaryCtaText = this.secondaryCtaText,
        badgeColorHex = this.badgeColorHex,
        discountText = this.discountText,
        redFighterName = this.redFighterName,
        blueFighterName = this.blueFighterName,
        weightClassText = this.weightClassText,
        matchCategoryBadge = this.matchCategoryBadge,
        eventEdition = this.eventEdition,
        redFlag = this.redFlag,
        blueFlag = this.blueFlag,
        hasChampionshipBelt = this.hasChampionshipBelt,
        targetTab = this.targetTab,
        ppvTierId = this.ppvTierId,
        posterImageUrl = this.posterImageUrl,
        displayOrder = order,
        isPinned = isPinned
    )
}

fun HeroSlideEntity.toModel(): HeroSlide {
    val slideType = try {
        HeroSlideType.valueOf(this.type)
    } catch (e: Exception) {
        HeroSlideType.MATCH_PROMO_POSTER
    }
    return HeroSlide(
        id = this.id,
        type = slideType,
        tag = this.tag,
        title = this.title,
        subtitle = this.subtitle,
        eventDateOrCountdown = this.eventDateOrCountdown,
        venueOrLocation = this.venueOrLocation,
        primaryCtaText = this.primaryCtaText,
        secondaryCtaText = this.secondaryCtaText,
        badgeColorHex = this.badgeColorHex,
        discountText = this.discountText,
        redFighterName = this.redFighterName,
        blueFighterName = this.blueFighterName,
        weightClassText = this.weightClassText,
        matchCategoryBadge = this.matchCategoryBadge,
        eventEdition = this.eventEdition,
        redFlag = this.redFlag,
        blueFlag = this.blueFlag,
        hasChampionshipBelt = this.hasChampionshipBelt,
        targetTab = this.targetTab,
        ppvTierId = this.ppvTierId,
        posterImageUrl = this.posterImageUrl
    )
}

fun PreviousFightVideo.toEntity(order: Int = 0): PreviousVideoEntity {
    return PreviousVideoEntity(
        id = this.id,
        title = this.title,
        subtitle = this.subtitle,
        eventName = this.eventName,
        category = this.category.name,
        durationText = this.durationText,
        durationSeconds = this.durationSeconds,
        viewsCountStr = this.viewsCountStr,
        publishedTimeAgo = this.publishedTimeAgo,
        fighterRedName = this.fighterRedName,
        fighterBlueName = this.fighterBlueName,
        resultMethod = this.resultMethod,
        finishRoundText = this.finishRoundText,
        fileSizeMb = this.fileSizeMb,
        resolutionLabel = this.resolutionLabel,
        isTitleFight = this.isTitleFight,
        contentTier = this.contentTier.name,
        likesCount = this.likesCount,
        videoUrl = this.videoUrl,
        youtubePlaylistUrl = this.youtubePlaylistUrl,
        youtubePlaylistId = this.youtubePlaylistId,
        youtubeVideoId = this.youtubeVideoId ?: "0a8G81bZ7X0",
        thumbnailUrl = this.thumbnailUrl,
        youtubeEmbedUrl = this.youtubeEmbedUrl,
        displayOrder = order
    )
}

fun PreviousVideoEntity.toModel(): PreviousFightVideo {
    val cat = try {
        VideoCategory.valueOf(this.category)
    } catch (e: Exception) {
        VideoCategory.FULL_REPLAY
    }
    val tier = try {
        ContentTier.valueOf(this.contentTier)
    } catch (e: Exception) {
        ContentTier.FREE_PRELIMS
    }
    return PreviousFightVideo(
        id = this.id,
        title = this.title,
        subtitle = this.subtitle,
        eventName = this.eventName,
        category = cat,
        durationText = this.durationText,
        durationSeconds = this.durationSeconds,
        viewsCountStr = this.viewsCountStr,
        publishedTimeAgo = this.publishedTimeAgo,
        fighterRedName = this.fighterRedName,
        fighterBlueName = this.fighterBlueName,
        resultMethod = this.resultMethod,
        finishRoundText = this.finishRoundText,
        fileSizeMb = this.fileSizeMb,
        resolutionLabel = this.resolutionLabel,
        isTitleFight = this.isTitleFight,
        contentTier = tier,
        likesCount = this.likesCount,
        videoUrl = this.videoUrl,
        youtubePlaylistUrl = this.youtubePlaylistUrl,
        youtubePlaylistId = this.youtubePlaylistId,
        youtubeVideoId = this.youtubeVideoId,
        thumbnailUrl = this.thumbnailUrl,
        youtubeEmbedUrl = this.youtubeEmbedUrl
    )
}

fun NewsArticle.toEntity(): NewsArticleEntity {
    return NewsArticleEntity(
        id = this.id,
        title = this.title,
        summary = this.summary,
        fullContent = this.fullContent,
        category = this.category.name,
        author = this.author,
        authorRole = this.authorRole,
        publishedDate = this.publishedDate,
        readTimeMinutes = this.readTimeMinutes,
        isFeatured = this.isFeatured,
        tags = this.tags.joinToString(","),
        relatedFighterNames = this.relatedFighterNames.joinToString(","),
        imageUrl = this.imageUrl,
        timestamp = System.currentTimeMillis()
    )
}

fun NewsArticleEntity.toModel(): NewsArticle {
    val cat = try {
        NewsCategory.valueOf(this.category)
    } catch (e: Exception) {
        NewsCategory.PRESS_RELEASE
    }
    return NewsArticle(
        id = this.id,
        title = this.title,
        summary = this.summary,
        fullContent = this.fullContent,
        category = cat,
        author = this.author,
        authorRole = this.authorRole,
        publishedDate = this.publishedDate,
        readTimeMinutes = this.readTimeMinutes,
        isFeatured = this.isFeatured,
        tags = if (this.tags.isBlank()) emptyList() else this.tags.split(","),
        relatedFighterNames = if (this.relatedFighterNames.isBlank()) emptyList() else this.relatedFighterNames.split(","),
        imageUrl = this.imageUrl
    )
}

