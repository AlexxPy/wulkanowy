package io.github.wulkanowy.services.sync.channels

import android.annotation.TargetApi
import android.app.Notification.VISIBILITY_PUBLIC
import android.app.NotificationChannel
import android.app.NotificationManager.IMPORTANCE_DEFAULT
import android.content.Context
import androidx.core.app.NotificationManagerCompat
import io.github.wulkanowy.R
import javax.inject.Inject

@TargetApi(26)
class UpcomingLessonsChannel @Inject constructor(
    private val notificationManager: NotificationManagerCompat,
    private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "lesson_channel"
    }

    fun create() {
        notificationManager.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, context.getString(R.string.channel_upcoming_lessons), IMPORTANCE_DEFAULT).apply {
                lockscreenVisibility = VISIBILITY_PUBLIC
                setShowBadge(false)
            }
        )
    }
}
