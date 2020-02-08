package io.github.wulkanowy.services.alarm

import android.app.AlarmManager
import android.app.AlarmManager.RTC_WAKEUP
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.LESSON_END
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.LESSON_NEXT_ROOM
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.LESSON_NEXT_TITLE
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.LESSON_ROOM
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.LESSON_START
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.LESSON_TITLE
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.LESSON_TYPE
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.NOTIFICATION_TYPE_CURRENT
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.NOTIFICATION_TYPE_LAST_LESSON_CANCELLATION
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.NOTIFICATION_TYPE_UPCOMING
import io.github.wulkanowy.services.alarm.TimetableNotificationBroadcastReceiver.Companion.STUDENT_NAME
import io.github.wulkanowy.utils.toTimestamp
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalDateTime.now
import timber.log.Timber
import javax.inject.Inject

class TimetableNotificationSchedulerHelper @Inject constructor(
    private val context: Context,
    private val alarmManager: AlarmManager,
    private val preferencesRepository: PreferencesRepository
) {

    private fun getRequestCode(time: LocalDateTime, studentId: Int) = (time.toTimestamp() * studentId).toInt()

    private fun getUpcomingLessonTime(index: Int, day: List<Timetable>, lesson: Timetable): LocalDateTime {
        return day.getOrNull(index - 1)?.end ?: lesson.start.minusMinutes(30)
    }

    fun cancelNotifications(lessons: List<Timetable>, studentId: Int = 1) {
        lessons.sortedBy { it.start }.forEachIndexed { index, lesson ->
            alarmManager.cancel(PendingIntent.getBroadcast(context, getRequestCode(getUpcomingLessonTime(index, lessons, lesson), studentId), Intent(), FLAG_CANCEL_CURRENT))
            alarmManager.cancel(PendingIntent.getBroadcast(context, getRequestCode(lesson.start, studentId), Intent(), FLAG_CANCEL_CURRENT))
        }
        Timber.d("Timetable notifications canceled")
    }

    fun scheduleNotifications(lessons: List<Timetable>, student: Student) {
        if (!preferencesRepository.isUpcomingLessonsNotificationsEnable) return cancelNotifications(lessons, student.studentId)

        lessons.groupBy { it.date }
            .map { it.value.sortedBy { lesson -> lesson.start } }
            .map { it.filter { lesson -> !lesson.canceled } }
            .map { day ->
                day.forEachIndexed { index, lesson ->
                    val intent = createIntent(student, lesson, day.getOrNull(index + 1))

                    if (lesson.start > now()) {
                        scheduleBroadcast(intent, student.studentId, NOTIFICATION_TYPE_UPCOMING, getUpcomingLessonTime(index, day, lesson))
                    }

                    if (lesson.end > now()) {
                        scheduleBroadcast(intent, student.studentId, NOTIFICATION_TYPE_CURRENT, lesson.start)
                        if (day.lastIndex == index) scheduleBroadcast(intent, student.studentId, NOTIFICATION_TYPE_LAST_LESSON_CANCELLATION, lesson.end)
                    }
                }
            }

        Timber.d("Timetable notifications scheduled")

//        cancelNotifications(lessons.filter { it.canceled }, student.studentId)
    }

    private fun createIntent(student: Student, lesson: Timetable, nextLesson: Timetable?): Intent {
        return Intent(context, TimetableNotificationBroadcastReceiver::class.java).apply {
            putExtra(STUDENT_NAME, student.studentName)
            putExtra(LESSON_ROOM, lesson.room)
            putExtra(LESSON_START, lesson.start.toTimestamp())
            putExtra(LESSON_END, lesson.end.toTimestamp())
            putExtra(LESSON_TITLE, lesson.subject)
            putExtra(LESSON_NEXT_TITLE, nextLesson?.subject)
            putExtra(LESSON_NEXT_ROOM, nextLesson?.room)
        }
    }

    private fun scheduleBroadcast(intent: Intent, studentId: Int, notificationType: Int, time: LocalDateTime) {
        AlarmManagerCompat.setExactAndAllowWhileIdle(alarmManager, RTC_WAKEUP, time.toTimestamp(),
            PendingIntent.getBroadcast(context, getRequestCode(time, studentId), intent.also {
                it.putExtra(LESSON_TYPE, notificationType)
            }, FLAG_CANCEL_CURRENT)
        )
        Timber.d("Timetable notification from studentId: $studentId, with notification type: $notificationType scheduled at $time")
    }
}
