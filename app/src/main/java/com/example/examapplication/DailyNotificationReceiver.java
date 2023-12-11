package com.example.examapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class DailyNotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "DailyNotificationChannel";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        int dayOfWeek = intent.getIntExtra("dayOfWeek", -1);

        if (dayOfWeek != -1) {
            // Implementa la logica per mostrare la notifica all'utente
            showNotification(context, dayOfWeek);
        }
    }

    private void showNotification(Context context, int dayOfWeek) {
        // Crea un NotificationManager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Controlla se il dispositivo è su Android Oreo o versione successiva
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crea un NotificationChannel per le notifiche
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Daily Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        // Costruisci la notifica
        Notification notification = new Notification.Builder(context, CHANNEL_ID)
                .setContentTitle("Ricorda di studiare!")
                .setContentText("Sarà il momento di studiare il " + getDayOfWeekString(dayOfWeek) + ".")
                .setSmallIcon(R.drawable.ic_notification_icon)
                .build();

        // Mostra la notifica
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private String getDayOfWeekString(int dayOfWeek) {
        // Converte il giorno della settimana in una stringa
        String[] daysOfWeek = {"Domenica", "Lunedì", "Martedì", "Mercoledì", "Giovedì", "Venerdì", "Sabato"};
        return daysOfWeek[dayOfWeek - 1];  // -1 perché Calendar.SUNDAY è 1, Calendar.MONDAY è 2, ecc.
    }
}
