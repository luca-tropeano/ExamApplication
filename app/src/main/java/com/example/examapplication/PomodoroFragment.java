package com.example.examapplication;

import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PomodoroFragment extends Fragment {

    private static final int POMODORO_DURATION_MINUTES = 1;
    private static final int BREAK_DURATION_MINUTES = 5;
    private static final int LONG_BREAK_DURATION_MINUTES = 15;
    private static final int POMODORI_FOR_LONG_BREAK = 4;

    private TextView timerTextView;
    private Button startPomodoroButton;
    private Button startShortBreakButton;
    private Button startLongBreakButton;
    private ImageView tomatoImageView; // Cambiato il nome dell'ImageView

    private CountDownTimer countDownTimer;
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
        tomatoImageView = view.findViewById(R.id.tomatoImageView); // Cambiato il nome dell'ImageView

        startPomodoroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning || currentTimerType != 0) {
                    stopTimer();
                    startTimer(POMODORO_DURATION_MINUTES, 0);
                }
            }
        });

        startShortBreakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning || currentTimerType != 1) {
                    stopTimer();
                    startTimer(BREAK_DURATION_MINUTES, 1);
                }
            }
        });

        startLongBreakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isTimerRunning || currentTimerType != 2) {
                    stopTimer();
                    startTimer(LONG_BREAK_DURATION_MINUTES, 2);
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

                // Hide tomato image for breaks
                tomatoImageView.setVisibility(View.GONE);

                if (currentTimerType == 0) {
                    pomodoriCompleted++;
                    if (pomodoriCompleted < POMODORI_FOR_LONG_BREAK) {
                        // Show tomato image only if less than four pomodori completed
                        int tomatoImageId = getResources().getIdentifier("ic_tomato_" + pomodoriCompleted, "drawable", getActivity().getPackageName());
                        if (tomatoImageId != 0) {
                            tomatoImageView.setImageResource(tomatoImageId);
                            tomatoImageView.setVisibility(View.VISIBLE);
                        } else {
                            Log.e("PomodoroFragment", "Resource not found for ic_tomato_" + pomodoriCompleted);
                        }
                    } else {
                        // Reset pomodori count
                        pomodoriCompleted = 0;
                        tomatoImageView.setVisibility(View.VISIBLE);
                        startTimer(LONG_BREAK_DURATION_MINUTES, 2);
                    }
                }

                // Restart Pomodoro timer after Short Break or Long Break
                if (currentTimerType != 0) {
                    startTimer(POMODORO_DURATION_MINUTES, 0);
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
}