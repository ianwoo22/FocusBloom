package com.focusbloom.app.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.focusbloom.app.databinding.ItemReflectionBinding;
import com.focusbloom.app.models.Reflection;
import java.util.List;

public class ReflectionAdapter extends RecyclerView.Adapter<ReflectionAdapter.ViewHolder> {

    private final List<Reflection> reflections;

    public ReflectionAdapter(List<Reflection> reflections) {
        this.reflections = reflections;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReflectionBinding binding = ItemReflectionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(reflections.get(position));
    }

    @Override
    public int getItemCount() {
        return reflections.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemReflectionBinding binding;

        public ViewHolder(ItemReflectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Reflection reflection) {
            binding.tvReflectionText.setText(reflection.getText());
            binding.tvReflectionDate.setText(reflection.getDate());
        }
    }
}