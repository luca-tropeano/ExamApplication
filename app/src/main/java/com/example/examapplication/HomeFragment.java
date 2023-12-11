
package com.example.examapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.examapplication.R;
import com.example.examapplication.DailyNotificationReceiver;
import android.widget.Button;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.util.Calendar;

public class HomeFragment extends Fragment {

    private static final int REQUEST_WRITE_CALENDAR_PERMISSION = 1;

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

                        // Avvia la logica per la notifica giornaliera
                        scheduleDailyNotification(hourOfDay, minute);
                    }
                },
                12, 0, false);
        timePickerDialog.show();
    }

    private void saveReminderTime(int hour, int minute) {
        // Salva l'orario selezionato dall'utente nelle preferenze con SharedPreferences
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit();
        editor.putInt("reminderHour", hour);
        editor.putInt("reminderMinute", minute);
        editor.apply();
    }

    private void scheduleDailyNotification(int hour, int minute) {
        // Ottieni l'AlarmManager dal sistema Android
        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);

        // Calcola il tempo in millisecondi per la notifica giornaliera
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Se l'orario è già passato oggi, imposta la notifica per domani
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }

        // Ripeti la notifica per ogni giorno della settimana
        for (int dayOfWeek = Calendar.SUNDAY; dayOfWeek <= Calendar.SATURDAY; dayOfWeek++) {
            // Crea un intent per lanciare il BroadcastReceiver che gestirà la notifica
            Intent intent = new Intent(requireContext(), DailyNotificationReceiver.class);

            // Aggiungi dati extra per identificare il giorno della settimana
            intent.putExtra("dayOfWeek", dayOfWeek);

            // Aggiungi il flag FLAG_IMMUTABLE
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    requireContext(),
                    dayOfWeek,  // Usa il giorno della settimana come requestCode
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            // Imposta la notifica giornaliera che si ripete ogni giorno
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY * 7,  // Intervallo settimanale
                    pendingIntent
            );
        }
    }
}