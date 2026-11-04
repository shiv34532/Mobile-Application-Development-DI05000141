package com.example.arpitapplication;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Practical10TaskAdapter extends RecyclerView.Adapter<Practical10TaskAdapter.TaskViewHolder> {

    public interface OnTaskActionListener {
        void onToggleStatus(TaskEntity task, boolean isCompleted);
        void onEdit(TaskEntity task);
        void onDelete(TaskEntity task);
    }

    private List<TaskEntity> taskList = new ArrayList<>();
    private final OnTaskActionListener listener;

    public Practical10TaskAdapter(OnTaskActionListener listener) {
        this.listener = listener;
    }

    public void setTasks(List<TaskEntity> tasks) {
        this.taskList = tasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.practical10_item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        TaskEntity task = taskList.get(position);

        holder.tvTaskTitle.setText(task.getTitle());

        // Temporarily clear listener before setting checked status to avoid recursive triggers
        holder.cbTaskStatus.setOnCheckedChangeListener(null);
        holder.cbTaskStatus.setChecked(task.isCompleted());

        // Visual Strikethrough & Status Badge styling
        if (task.isCompleted()) {
            holder.tvTaskTitle.setPaintFlags(holder.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.tvTaskTitle.setTextColor(0xFF94A3B8);

            holder.tvTaskStatusBadge.setText("✅ Completed");
            holder.tvTaskStatusBadge.setTextColor(0xFF15803D);
            holder.tvTaskStatusBadge.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_done));
        } else {
            holder.tvTaskTitle.setPaintFlags(holder.tvTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            holder.tvTaskTitle.setTextColor(0xFF1E293B);

            holder.tvTaskStatusBadge.setText("⏳ Pending");
            holder.tvTaskStatusBadge.setTextColor(0xFFB45309);
            holder.tvTaskStatusBadge.setBackground(ContextCompat.getDrawable(holder.itemView.getContext(), R.drawable.bg_badge_pending));
        }

        // Toggle status listener (UPDATE)
        holder.cbTaskStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onToggleStatus(task, isChecked);
            }
        });

        // Edit button listener (UPDATE)
        holder.btnEditTask.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(task);
            }
        });

        // Delete button listener (DELETE)
        holder.btnDeleteTask.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(task);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList != null ? taskList.size() : 0;
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbTaskStatus;
        TextView tvTaskTitle, tvTaskStatusBadge;
        TextView btnEditTask, btnDeleteTask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            cbTaskStatus = itemView.findViewById(R.id.cbTaskStatus);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskStatusBadge = itemView.findViewById(R.id.tvTaskStatusBadge);
            btnEditTask = itemView.findViewById(R.id.btnEditTask);
            btnDeleteTask = itemView.findViewById(R.id.btnDeleteTask);
        }
    }
}
