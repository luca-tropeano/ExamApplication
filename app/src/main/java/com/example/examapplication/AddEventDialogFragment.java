package com.example.examapplication;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.TimeZone;

public class AddEventDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_event_dialog, null);

        final EditText titleEditText = view.findViewById(R.id.editTextTitle);
        final EditText descriptionEditText = view.findViewById(R.id.editTextDescription);

        builder.setView(view)
                .setTitle("Aggiungi Evento")
                .setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ottieni le informazioni sull'evento
                        String title = titleEditText.getText().toString();
                        String description = descriptionEditText.getText().toString();

                        // Ottieni la data selezionata dal CalendarView (passata attraverso gli arguments)
                        long selectedDateMillis = getArguments().getLong("selectedDate");

                        // Creare l'evento nel calendario
                        createCalendarEvent(selectedDateMillis, title, description);
                    }
                })
                .setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Annulla l'aggiunta dell'evento
                        dismiss();
                    }
                });

        return builder.create();
    }

    private void createCalendarEvent(long selectedDateMillis, String title, String description) {
        ContentResolver contentResolver = requireActivity().getContentResolver();

        // Creare un ContentValues per le informazioni dell'evento
        ContentValues values = new ContentValues();
        values.put(Events.DTSTART, selectedDateMillis);
        values.put(Events.DTEND, selectedDateMillis);
        values.put(Events.TITLE, title);
        values.put(Events.DESCRIPTION, description);
        values.put(Events.CALENDAR_ID, 1); // ID del calendario. Puoi ottenere i calendari disponibili dalla tua app.
        values.put(Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

        // Inserire l'evento nel calendario
        Uri uri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);

        // Ottenere l'ID dell'evento appena creato
        long eventID = Long.parseLong(uri.getLastPathSegment());

        Toast.makeText(requireContext(), "Evento creato con successo", Toast.LENGTH_SHORT).show();
    }
}
