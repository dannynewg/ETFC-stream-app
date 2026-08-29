package com.example.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

class EtfcNotificationManager(private val context: Context) {

    companion object {
        const val CHANNEL_LIVE_EVENTS = "etfc_channel_live_events"
        const val CHANNEL_BOUT_ALERTS = "etfc_channel_bout_alerts"
        const val CHANNEL_PLATFORM_UPDATES = "etfc_channel_platform_updates"

        const val EXTRA_TARGET_TAB = "extra_target_tab"
        const val EXTRA_BOUT_ID = "extra_bout_id"

        const val NOTIFICATION_ID_LIVE_EVENT = 1001
        const val NOTIFICATION_ID_BOUT_START = 1002
        const val NOTIFICATION_ID_PLATFORM = 1003
        const val NOTIFICATION_ID_KO_ALERT = 1004
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Channel 1: Live Event Alerts (High Importance)
            val liveEventChannel = NotificationChannel(
                CHANNEL_LIVE_EVENTS,
                "ETFC Live Events & Main Card",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Urgent alerts when ETFC live fight nights, main events, and title bouts go live."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300)
                setShowBadge(true)
            }

            // Channel 2: Fighter & Bout Alerts (High Importance)
            val boutAlertChannel = NotificationChannel(
                CHANNEL_BOUT_ALERTS,
                "Fighter Walkout & Bout Starts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications when your favorited fighters make their walkout or bout starts."
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 200, 100, 200)
                setShowBadge(true)
            }

            // Channel 3: Platform Updates & PPV Offers (Default Importance)
            val platformChannel = NotificationChannel(
                CHANNEL_PLATFORM_UPDATES,
                "Platform Updates & Announcements",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Official announcements, new CDN edge locations, and Telebirr/M-Pesa PPV perks."
                setShowBadge(true)
            }

            notificationManager.createNotificationChannels(
                listOf(liveEventChannel, boutAlertChannel, platformChannel)
            )
        }
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        }
    }

    private fun createPendingIntent(targetTab: String, boutId: String? = null, requestCode: Int): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_TARGET_TAB, targetTab)
            boutId?.let { putExtra(EXTRA_BOUT_ID, it) }
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, requestCode, intent, flags)
    }

    fun sendLiveEventStartNotification(
        eventName: String = "ETFC 002: Adwa Fight Night",
        mainEventTitle: String = "Sedo vs. Jhonny • Middleweight Championship",
        venue: String = "Adwa 00 Museum, Addis Ababa",
        boutId: String = "bout_1"
    ) {
        if (!hasNotificationPermission()) return

        val mainIntent = createPendingIntent(targetTab = "LIVE_ARENA", boutId = boutId, requestCode = 101)
        val cardIntent = createPendingIntent(targetTab = "FIGHT_CARD", boutId = boutId, requestCode = 102)

        val notification = NotificationCompat.Builder(context, CHANNEL_LIVE_EVENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔴 ETFC LIVE: $eventName")
            .setContentText("MAIN EVENT: $mainEventTitle")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("The Octagon is LIVE at $venue!\n\nMain Event title showdown: $mainEventTitle.\nStream in adaptive 1080p ABR with low-latency CDN now.")
            )
            .setColor(0xFFEF4444.toInt())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setContentIntent(mainIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Watch Stream Live", mainIntent)
            .addAction(R.drawable.ic_launcher_foreground, "View Match Card", cardIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_LIVE_EVENT, notification)
        } catch (e: SecurityException) {
            // Ignored if permission revoked
        }
    }

    fun sendBoutStartingNotification(
        fighterRed: String,
        fighterBlue: String,
        matchTitle: String,
        boutId: String = "bout_1"
    ) {
        if (!hasNotificationPermission()) return

        val watchIntent = createPendingIntent(targetTab = "LIVE_ARENA", boutId = boutId, requestCode = 201)

        val notification = NotificationCompat.Builder(context, CHANNEL_BOUT_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🥊 BOUT STARTING: $fighterRed vs $fighterBlue")
            .setContentText("$matchTitle is entering the cage now!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Fighters have completed cage walkouts: $fighterRed (Red Corner) vs $fighterBlue (Blue Corner). Round 1 begins in 60 seconds!")
            )
            .setColor(0xFFFAB818.toInt())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(watchIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Jump To Stream", watchIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_BOUT_START, notification)
        } catch (e: SecurityException) {
            // Ignored
        }
    }

    fun sendPlatformUpdateNotification(
        title: String = "⚡ ETFC Stream Platform Update",
        message: String = "New Addis Ababa Edge Node CDN enabled for sub-25ms ultra low-latency playback across Ethiopia.",
        promoCode: String? = "ADWA2026"
    ) {
        if (!hasNotificationPermission()) return

        val ppvIntent = createPendingIntent(targetTab = "PPV_PASSES", requestCode = 301)

        val expandedText = if (promoCode != null) {
            "$message\n\n🎁 Use Code $promoCode on Telebirr or M-Pesa for 20% off All-Access VIP PPV."
        } else {
            message
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_PLATFORM_UPDATES)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(expandedText))
            .setColor(0xFF38BDF8.toInt())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(ppvIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Explore PPV Passes", ppvIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_PLATFORM, notification)
        } catch (e: SecurityException) {
            // Ignored
        }
    }

    fun sendKoFinishNotification(
        winner: String,
        loser: String,
        method: String = "KO (Adwa Hammer Cross)",
        round: Int = 3,
        time: String = "2:14"
    ) {
        if (!hasNotificationPermission()) return

        val streamIntent = createPendingIntent(targetTab = "LIVE_ARENA", requestCode = 401)

        val notification = NotificationCompat.Builder(context, CHANNEL_LIVE_EVENTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("💥 HIGHLIGHT FINISH: $winner wins by $method!")
            .setContentText("Round $round ($time) — Replay is now ready on demand.")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("STUNNING FINISH! $winner finishes $loser via $method at $time of Round $round. Instant HD replay is now processing.")
            )
            .setColor(0xFFEF4444.toInt())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(streamIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Watch Replay", streamIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_KO_ALERT, notification)
        } catch (e: SecurityException) {
            // Ignored
        }
    }
}
