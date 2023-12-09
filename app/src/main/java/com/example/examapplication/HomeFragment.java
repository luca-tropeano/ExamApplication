package com.example.examapplication;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.TimeZone;

public class HomeFragment extends Fragment {

    private static final int REQUEST_WRITE_CALENDAR_PERMISSION = 1;

    private Calendar selectedCalendar = Calendar.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CalendarView calendarView = view.findViewById(R.id.calendarView);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                // Chiamato quando l'utente seleziona una data
                showDatePickerDialog(year, month, dayOfMonth);
            }
        });

        return view;
    }

    private void showDatePickerDialog(int year, int month, int dayOfMonth) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Imposta la data selezionata
                        selectedCalendar.set(Calendar.YEAR, year);
                        selectedCalendar.set(Calendar.MONTH, month);
                        selectedCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        // Richiedi i permessi prima di mostrare il TimePickerDialog
                        checkAndRequestCalendarPermission();
                    }
                },
                year, month, dayOfMonth
        );
        datePickerDialog.show();
    }

    private void checkAndRequestCalendarPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            // Se i permessi non sono concessi, richiedi i permessi
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.WRITE_CALENDAR},
                    REQUEST_WRITE_CALENDAR_PERMISSION
            );
        } else {
            // Se i permessi sono già concessi, procedi con la creazione dell'evento
            showTimePickerDialog();
        }
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                requireContext(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Imposta l'ora di inizio
                        selectedCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedCalendar.set(Calendar.MINUTE, minute);

                        // Ora che hai la data e l'ora, puoi creare l'evento nel calendario
                        createCalendarEvent();
                    }
                },
                selectedCalendar.get(Calendar.HOUR_OF_DAY),
                selectedCalendar.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(requireContext())
        );
        timePickerDialog.show();
    }

    private void createCalendarEvent() {
        ContentResolver contentResolver = requireActivity().getContentResolver();

        // Creare un ContentValues per le informazioni dell'evento
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, selectedCalendar.getTimeInMillis());
        values.put(CalendarContract.Events.DTEND, selectedCalendar.getTimeInMillis() + (60 * 60 * 1000)); // Esempio: Durata di un'ora
        values.put(CalendarContract.Events.TITLE, "Il tuo titolo dell'evento");
        values.put(CalendarContract.Events.DESCRIPTION, "Descrizione dell'evento");
        values.put(CalendarContract.Events.CALENDAR_ID, 1); // ID del calendario. Puoi ottenere i calendari disponibili dalla tua app.
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        // Inserire l'evento nel calendario
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);

        // Ottenere l'ID dell'evento appena creato
        long eventID = Long.parseLong(uri.getLastPathSegment());

        Toast.makeText(requireContext(), "Evento creato con successo", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_CALENDAR_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permesso concesso, procedere con la creazione dell'evento
                showTimePickerDialog();
            } else {
                // Permesso negato, gestire di conseguenza (ad esempio, mostrare un messaggio all'utente)
                Toast.makeText(requireContext(), "Permesso negato, impossibile creare l'evento nel calendario", Toast.LENGTH_SHORT).show();
            }
        }
    }
}