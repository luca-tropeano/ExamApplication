package com.example.examapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.widget.TextView;
import java.util.Locale;
import android.app.AlarmManager;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private TextView reminderTimeTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Infla il layout per questo frammento
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // Trova il bottone nel layout e aggiungi un listener per l'evento di clic
        Button setReminderButton = rootView.findViewById(R.id.setReminderButton);
        setReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mostra il TimePickerDialog quando il pulsante viene cliccato
                showTimePickerDialog();
            }
        });

        // Trova il TextView per l'orario nel layout
        reminderTimeTextView = rootView.findViewById(R.id.reminderTimeTextView);

        // Visualizza l'orario salvato, se disponibile
        displaySavedReminderTime();

        return rootView;
    }

    private void showTimePickerDialog() {
        // Crea un TimePickerDialog per consentire all'utente di selezionare l'orario
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Salva l'orario selezionato dall'utente
                        saveReminderTime(hourOfDay, minute);
                        // Visualizza immediatamente il nuovo orario
                        displaySavedReminderTime();
                        // Programma la notifica con il nuovo orario
                        scheduleDailyNotification(requireContext(), hourOfDay, minute);
                    }
                },
                12, 0, false);
        timePickerDialog.show();
    }
    private void scheduleDailyNotification(Context context, int hour, int minute) {
        // Ottieni l'AlarmManager dal sistema Android
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Calcola l'orario desiderato per la notifica giornaliera
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Crea un intent per lanciare il BroadcastReceiver che gestirà la notifica
        Intent intent = new Intent(context, DailyNotificationReceiver.class);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        intent.putExtra("dayOfWeek", dayOfWeek);

        // Crea un PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                0,  // requestCode
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Usa setExactAndAllowWhileIdle per garantire che la notifica venga inviata anche in modalità Doze
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
        }
    }


    private void saveReminderTime(int hour, int minute) {
        // Salva l'orario selezionato dall'utente nelle preferenze con SharedPreferences
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
        editor.putInt("reminderHour", hour);
        editor.putInt("reminderMinute", minute);
        editor.apply();
    }

    private void displaySavedReminderTime() {
        // Recupera l'orario salvato dalle preferenze
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        int hour = preferences.getInt("reminderHour", -1);
        int minute = preferences.getInt("reminderMinute", -1);

        // Mostra l'orario nel TextView
        if (hour != -1 && minute != -1) {
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
            reminderTimeTextView.setText("Orario impostato: " + formattedTime);
        } else {
            reminderTimeTextView.setText("Orario non impostato");
        }
    }
}
