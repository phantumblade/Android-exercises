package com.example.application1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskTitle = intent.getStringExtra("TASK_TITLE") ?: "Task"
        val channelId = "todo_channel"
        val notifId = System.currentTimeMillis().toInt() // ID univoco casuale

        // 1. Crea il canale se necessario (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Scadenze Task", NotificationManager.IMPORTANCE_HIGH)
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        // 2. Prepara l'intent per aprire l'app al click
        val tapIntent = Intent(context, TaskActionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TASK_TITLE", taskTitle)
            putExtra("NOTIFICATION_ID", notifId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context, notifId, tapIntent, PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Costruisci la notifica
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_stat_name) // Usa la tua icona
            .setContentTitle("⏰ È ora!")
            .setContentText("Scadenza: $taskTitle")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 4. Invia (Nota: qui controlliamo il permesso in modo semplificato per brevità,
        // in un'app reale andrebbe gestito prima)
        try {
            val manager = NotificationManagerCompat.from(context)
            manager.notify(notifId, builder.build())
        } catch (e: SecurityException) {
            // Permesso mancante
        }
    }
}
