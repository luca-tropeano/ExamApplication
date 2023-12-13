package com.example.examapplication;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class PomodoroFragment extends Fragment {

    private static final int POMODORO_DURATION_MINUTES = 1;
    private static final int BREAK_DURATION_MINUTES = 5;
    private static final int LONG_BREAK_DURATION_MINUTES = 15;
    private static final int POMODORI_FOR_LONG_BREAK = 4;

    private TextView timerTextView;
    private Button startPomodoroButton;
    private Button startShortBreakButton;
    private Button startLongBreakButton;
    private ImageView tomatoImageView;
    private GridLayout tomatoImagesGrid;
    private CountDownTimer countDownTimer;
    private MediaPlayer mediaPlayer;

    private boolean shouldStartPomodoroAfterBreak = false;
    private boolean isTimerRunning = false;

    private int currentTimerType = 0; // 0: Pomodoro, 1: Short Break, 2: Long Break
    private int pomodoriCompleted = 0;

    public PomodoroFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pomodoro, container, false);

        timerTextView = view.findViewById(R.id.timerTextView);
        startPomodoroButton = view.findViewById(R.id.startPomodoroButton);
        startShortBreakButton = view.findViewById(R.id.startShortBreakButton);
        startLongBreakButton = view.findViewById(R.id.startLongBreakButton);
        tomatoImageView = view.findViewById(R.id.tomatoImageView);
        tomatoImagesGrid = view.findViewById(R.id.tomatoImagesGrid);

        // Inizializza il MediaPlayer
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.alarm_buzzer);

        startPomodoroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning || currentTimerType != 0) {
                    stopTimer();
                    shouldStartPomodoroAfterBreak = false;  // Modifica
                    startTimer(POMODORO_DURATION_MINUTES, 0);
                }
            }
        });

        startShortBreakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning || currentTimerType != 1) {
                    stopTimer();

                    // Verifica se ci sono abbastanza pomodori completati per avviare la pausa breve
                    if (pomodoriCompleted >= 3) {
                        removeTomatoImages(3);
                        startTimer(BREAK_DURATION_MINUTES, 1);
                    } else {
                        // Notifica o log che non ci sono abbastanza ic_tomato
                        Log.e("PomodoroFragment", "Not enough ic_tomato for Short Break");
                    }
                }
            }
        });

        startLongBreakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning || currentTimerType != 2) {
                    stopTimer();

                    // Verifica se ci sono abbastanza pomodori completati per avviare la pausa lunga
                    if (pomodoriCompleted >= POMODORI_FOR_LONG_BREAK) {
                        removeTomatoImages(POMODORI_FOR_LONG_BREAK);
                        startTimer(LONG_BREAK_DURATION_MINUTES, 2);
                    } else {
                        // Notifica o log che non ci sono abbastanza ic_tomato
                        Log.e("PomodoroFragment", "Not enough ic_tomato for Long Break");
                    }
                }
            }
        });

        return view;
    }

    private void startTimer(int durationInMinutes, int timerType) {
        isTimerRunning = true;
        currentTimerType = timerType;

        countDownTimer = new CountDownTimer(durationInMinutes * 60 * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                updateTimerText(millisUntilFinished);
            }

            public void onFinish() {
                timerTextView.setText("Timer expired");
                isTimerRunning = false;

                // Riproduci il suono quando il countdown finisce
                mediaPlayer.start();

                // Rilascia le risorse del MediaPlayer dopo la riproduzione
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.release();
                    }
                });

                // Show tomato image for completed pomodori
                if (currentTimerType == 0) {
                    pomodoriCompleted++;

                    if (pomodoriCompleted <= POMODORI_FOR_LONG_BREAK) {
                        int tomatoImageId = getResources().getIdentifier("ic_tomato_" + pomodoriCompleted, "drawable", getActivity().getPackageName());

                        if (tomatoImageId != 0) {
                            // Create ImageView dynamically
                            ImageView imageView = new ImageView(requireContext());
                            imageView.setImageResource(tomatoImageId);

                            // Add ImageView to GridLayout
                            tomatoImagesGrid.addView(imageView);

                            // Show or hide tomato images as needed
                            tomatoImagesGrid.setVisibility(View.VISIBLE);
                            tomatoImageView.setVisibility(View.GONE);
                        } else {
                            Log.e("PomodoroFragment", "Resource not found for ic_tomato_" + pomodoriCompleted);
                        }
                    } else {
                        // Reset pomodori count
                        pomodoriCompleted = 0;

                        // Imposto il flag per iniziare il Pomodoro dopo una pausa breve
                        shouldStartPomodoroAfterBreak = true;
                        // startTimer(LONG_BREAK_DURATION_MINUTES, 2);
                    }
                }

                // Restart Pomodoro timer after Short Break or Long Break
                if (currentTimerType != 0) {
                    if (shouldStartPomodoroAfterBreak) {
                        // Avvia il Pomodoro solo se il flag è impostato
                        startTimer(POMODORO_DURATION_MINUTES, 0);
                        shouldStartPomodoroAfterBreak = false; // Resetta il flag
                    }
                }
            }
        }.start();
    }

    private void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void updateTimerText(long millisUntilFinished) {
        long minutes = millisUntilFinished / 60000;
        long seconds = (millisUntilFinished % 60000) / 1000;
        String timeLeft = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeLeft);
    }

    // Metodo per rimuovere un numero specificato di immagini ic_tomato dal GridLayout
    private void removeTomatoImages(int count) {
        int imagesToRemove = Math.min(count, tomatoImagesGrid.getChildCount());

        for

        (int i = 0; i < imagesToRemove; i++) {
            tomatoImagesGrid.removeViewAt(0); // Rimuovi la prima immagine
        }
    }
}