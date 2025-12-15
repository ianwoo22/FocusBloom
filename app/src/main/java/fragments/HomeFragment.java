package com.focusbloom.app.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.focusbloom.app.R;
import com.focusbloom.app.databinding.FragmentHomeBinding;
import com.focusbloom.app.utils.PreferenceManager;
import com.focusbloom.app.utils.TimerService;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private PreferenceManager preferenceManager;
    private TimerService timerService;
    private boolean isServiceBound = false;
    private boolean isSessionActive = false;
    private int sessionDuration = 25; // minutes
    private float sessionStartProgress = 0f;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            timerService = binder.getService();
            isServiceBound = true;

            timerService.setTimerListener(new TimerService.TimerListener() {
                @Override
                public void onTick(long millisUntilFinished) {
                    updateTimerUI(millisUntilFinished);
                    updateBloomProgress(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    handleSessionComplete();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferenceManager = new PreferenceManager(requireContext());

        updateBloomDisplay();
        setupSessionSetup();

        Intent intent = new Intent(requireContext(), TimerService.class);
        requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void updateBloomDisplay() {
        float progress = preferenceManager.getBloomProgress();
        binding.tvBloomProgress.setText(String.format("%.0f%%", progress));
        binding.progressBloom.setProgress((int) progress);

        String status;
        if (progress < 30) {
            status = "Just planted 🌱";
        } else if (progress < 70) {
            status = "Growing strong 🌿";
        } else if (progress < 100) {
            status = "Almost there! 🌸";
        } else {
            status = "Fully bloomed! 🌺";
        }
        binding.tvBloomStatus.setText(status);

        animateBloom(progress);
    }

    private void animateBloom(float progress) {
        if (binding.bloomAnimation instanceof com.airbnb.lottie.LottieAnimationView) {
            com.airbnb.lottie.LottieAnimationView lottieView =
                    (com.airbnb.lottie.LottieAnimationView) binding.bloomAnimation;
            lottieView.setProgress(progress / 100f);
        }
    }

    private void setupSessionSetup() {
        View sessionSetup = getLayoutInflater().inflate(R.layout.layout_session_setup,
                binding.timerContainer, false);
        binding.timerContainer.removeAllViews();
        binding.timerContainer.addView(sessionSetup);

        sessionSetup.findViewById(R.id.btnQuick).setOnClickListener(v -> {
            sessionDuration = 15;
            Toast.makeText(requireContext(), "15 minutes selected", Toast.LENGTH_SHORT).show();
        });
        sessionSetup.findViewById(R.id.btnPomodoro).setOnClickListener(v -> {
            sessionDuration = 25;
            Toast.makeText(requireContext(), "25 minutes selected", Toast.LENGTH_SHORT).show();
        });
        sessionSetup.findViewById(R.id.btnDeepWork).setOnClickListener(v -> {
            sessionDuration = 45;
            Toast.makeText(requireContext(), "45 minutes selected", Toast.LENGTH_SHORT).show();
        });
        sessionSetup.findViewById(R.id.btnExtended).setOnClickListener(v -> {
            sessionDuration = 60;
            Toast.makeText(requireContext(), "60 minutes selected", Toast.LENGTH_SHORT).show();
        });

        sessionSetup.findViewById(R.id.btnStartSession).setOnClickListener(v -> startSession());
    }

    private void startSession() {
        isSessionActive = true;
        sessionStartProgress = preferenceManager.getBloomProgress();

        View timerLayout = getLayoutInflater().inflate(R.layout.layout_focus_timer,
                binding.timerContainer, false);
        binding.timerContainer.removeAllViews();
        binding.timerContainer.addView(timerLayout);

        if (isServiceBound && timerService != null) {
            long durationMillis = sessionDuration * 60 * 1000L;
            timerService.startTimer(durationMillis);
            Toast.makeText(requireContext(), "Starting " + sessionDuration + " min session", Toast.LENGTH_SHORT).show();
        }

        timerLayout.findViewById(R.id.btnPauseResume).setOnClickListener(v -> toggleTimer());
        timerLayout.findViewById(R.id.btnReset).setOnClickListener(v -> resetTimer());
    }

    private void toggleTimer() {
        if (isServiceBound && timerService != null) {
            if (timerService.isRunning()) {
                timerService.pauseTimer();
            } else {
                timerService.startTimer(timerService.getTimeLeft());
            }
        }
    }

    private void resetTimer() {
        if (isServiceBound && timerService != null) {
            timerService.pauseTimer();
        }
        isSessionActive = false;
        setupSessionSetup();
    }

    private void updateTimerUI(long millisUntilFinished) {
        View timerLayout = binding.timerContainer.getChildAt(0);
        if (timerLayout != null) {
            int minutes = (int) (millisUntilFinished / 1000) / 60;
            int seconds = (int) (millisUntilFinished / 1000) % 60;
            String timeText = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

            TextView tvTimer = timerLayout.findViewById(R.id.tvTimer);
            if (tvTimer != null) {
                tvTimer.setText(timeText);
            }

            long totalMillis = sessionDuration * 60 * 1000L;
            long elapsedMillis = totalMillis - millisUntilFinished;
            float progressPercentage = (elapsedMillis / (float) totalMillis) * 100f;

            com.mikhaellopez.circularprogressbar.CircularProgressBar circularProgress =
                    timerLayout.findViewById(R.id.circularProgress);
            if (circularProgress != null) {
                circularProgress.setProgress(progressPercentage);
            }
        }
    }

    private void updateBloomProgress(long millisUntilFinished) {
        long totalMillis = sessionDuration * 60 * 1000L;
        long elapsedMillis = totalMillis - millisUntilFinished;
        float sessionProgressPercentage = (elapsedMillis / (float) totalMillis) * 100f;

        float sessionContribution = 15f;
        float incrementalProgress = (sessionProgressPercentage / 100f) * sessionContribution;

        float currentProgress = Math.min(sessionStartProgress + incrementalProgress, 100f);

        binding.tvBloomProgress.setText(String.format("%.0f%%", currentProgress));
        binding.progressBloom.setProgress((int) currentProgress);
        animateBloom(currentProgress);
    }

    private void handleSessionComplete() {
        isSessionActive = false;

        preferenceManager.incrementFocusSessions();
        preferenceManager.addFocusTime(sessionDuration);

        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        preferenceManager.updateStreak(today);

        float currentProgress = preferenceManager.getBloomProgress();
        preferenceManager.updateBloomProgress(Math.min(currentProgress + 15f, 100f));

        Toast.makeText(requireContext(), "Focus session complete! 🎉", Toast.LENGTH_SHORT).show();

        updateBloomDisplay();
        setupSessionSetup();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isServiceBound) {
            requireContext().unbindService(serviceConnection);
            isServiceBound = false;
        }
    }
}