package com.focusbloom.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.focusbloom.app.adapters.ReflectionAdapter;
import com.focusbloom.app.databinding.FragmentReflectBinding;
import com.focusbloom.app.models.Reflection;
import com.focusbloom.app.utils.PreferenceManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class ReflectFragment extends Fragment {

    private FragmentReflectBinding binding;
    private List<Reflection> reflections;
    private ReflectionAdapter adapter;
    private String[] prompts = {
            "How do you feel after this focus session?",
            "What challenges did you face while staying focused?",
            "What helped you maintain your concentration?",
            "How can you improve your next focus session?",
            "What did you accomplish during this focused time?",
            "What emotions arose when you felt the urge to check your phone?"
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentReflectBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadReflections();
        setupPrompt();
        setupRecyclerView();
        setupSaveButton();
    }

    private void loadReflections() {
        // Load from SharedPreferences (in a real app, use Room database)
        String json = requireContext().getSharedPreferences("FocusBloomPrefs", 0)
                .getString("reflections", "[]");
        Type type = new TypeToken<List<Reflection>>(){}.getType();
        reflections = new Gson().fromJson(json, type);
        if (reflections == null) {
            reflections = new ArrayList<>();
        }
    }

    private void setupPrompt() {
        Random random = new Random();
        String prompt = prompts[random.nextInt(prompts.length)];
        binding.tvPrompt.setText(prompt);
    }

    private void setupRecyclerView() {
        adapter = new ReflectionAdapter(reflections);
        binding.rvReflections.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvReflections.setAdapter(adapter);

        if (reflections.isEmpty()) {
            binding.tvNoReflections.setVisibility(View.VISIBLE);
            binding.rvReflections.setVisibility(View.GONE);
        } else {
            binding.tvNoReflections.setVisibility(View.GONE);
            binding.rvReflections.setVisibility(View.VISIBLE);
        }
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> saveReflection());
    }

    private void saveReflection() {
        String text = binding.etReflection.getText().toString().trim();

        if (text.isEmpty()) {
            Toast.makeText(requireContext(), "Please write something", Toast.LENGTH_SHORT).show();
            return;
        }

        String date = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        Reflection reflection = new Reflection(text, date);
        reflections.add(0, reflection); // Add to beginning

        // Save to SharedPreferences
        String json = new Gson().toJson(reflections);
        requireContext().getSharedPreferences("FocusBloomPrefs", 0)
                .edit()
                .putString("reflections", json)
                .apply();

        adapter.notifyItemInserted(0);
        binding.rvReflections.scrollToPosition(0);
        binding.etReflection.setText("");

        binding.tvNoReflections.setVisibility(View.GONE);
        binding.rvReflections.setVisibility(View.VISIBLE);

        Toast.makeText(requireContext(), "Reflection saved! 🌸", Toast.LENGTH_SHORT).show();
    }
}