package com.focusbloom.app.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.focusbloom.app.databinding.FragmentStatsBinding;
import com.focusbloom.app.utils.PreferenceManager;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private PreferenceManager preferenceManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferenceManager = new PreferenceManager(requireContext());

        loadStats();
        setupWeeklyChart();
    }

    private void loadStats() {
        int sessions = preferenceManager.getFocusSessions();
        int totalTime = preferenceManager.getTotalFocusTime();
        int streak = preferenceManager.getCurrentStreak();
        float bloomProgress = preferenceManager.getBloomProgress();

        binding.tvFocusSessions.setText(String.valueOf(sessions));
        binding.tvTotalFocusTime.setText(formatTime(totalTime));
        binding.tvCurrentStreak.setText(streak + " days");
        binding.tvBloomProgress.setText(String.format("%.0f%%", bloomProgress));
    }

    private String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        if (hours > 0) {
            return hours + "h " + mins + "m";
        }
        return mins + "m";
    }

    private void setupWeeklyChart() {
        BarChart chart = binding.weeklyChart;

        // Sample data (in a real app, you'd fetch this from database)
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 45));
        entries.add(new BarEntry(1, 60));
        entries.add(new BarEntry(2, 30));
        entries.add(new BarEntry(3, 75));
        entries.add(new BarEntry(4, 50));
        entries.add(new BarEntry(5, 40));
        entries.add(new BarEntry(6, 65));

        BarDataSet dataSet = new BarDataSet(entries, "Focus Minutes");
        dataSet.setColors(
                Color.parseColor("#10B981"),
                Color.parseColor("#3B82F6"),
                Color.parseColor("#8B5CF6"),
                Color.parseColor("#F97316"),
                Color.parseColor("#22C55E"),
                Color.parseColor("#6366F1"),
                Color.parseColor("#EC4899")
        );
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        chart.setData(barData);

        // Customize chart
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);

        // X-axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(
                new String[]{"M", "T", "W", "T", "F", "S", "S"}
        ));

        // Y-axis
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setEnabled(false);

        chart.animateY(1000);
        chart.invalidate();
    }
}