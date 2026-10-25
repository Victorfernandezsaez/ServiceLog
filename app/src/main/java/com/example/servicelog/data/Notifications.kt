package com.example.servicelog.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.servicelog.R
import com.example.servicelog.domain.Reminder
import com.example.servicelog.domain.ReminderKind

const val DUE_CHANNEL_ID = "due_reminders"

fun ensureNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        DUE_CHANNEL_ID,
        context.getString(R.string.channel_due_name),
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = context.getString(R.string.channel_due_description)
    }
    NotificationManagerCompat.from(context).createNotificationChannel(channel)
}

fun showDueNotification(context: Context, reminder: Reminder) {
    val title = reminder.status.interval.title
    val text = when (reminder.kind) {
        ReminderKind.WEEK_BEFORE -> context.getString(R.string.due_in_a_week)
        ReminderKind.DAY_BEFORE -> context.getString(R.string.due_tomorrow)
    }

    val notification = NotificationCompat.Builder(context, DUE_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(text)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .build()

    try {
        NotificationManagerCompat.from(context)
            .notify(reminder.status.interval.id.toInt(), notification)
    } catch (e: SecurityException) {
        // permission not granted
    }
}