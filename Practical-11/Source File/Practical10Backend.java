package com.example.arpitapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Practical10Backend extends AppCompatActivity implements Practical10TaskAdapter.OnTaskActionListener {

    private EditText etTaskInput;
    private Button btnAddTask;
    private TextView tvTotalPill, tvCompletedPill, tvClearAll;
    private RecyclerView rvTasks;
    private LinearLayout layoutEmptyState;
    private Practical10TaskAdapter adapter;

    private AppDatabase database;
    private final ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical10_frontend);

        // Bind UI Elements
        etTaskInput = findViewById(R.id.etTaskInput);
        btnAddTask = findViewById(R.id.btnAddTask);
        tvTotalPill = findViewById(R.id.tvTotalPill);
        tvCompletedPill = findViewById(R.id.tvCompletedPill);
        tvClearAll = findViewById(R.id.tvClearAll);
        rvTasks = findViewById(R.id.rvTasks);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);

        // Initialize Room Database
        database = AppDatabase.getInstance(this);

        // Setup RecyclerView
        rvTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Practical10TaskAdapter(this);
        rvTasks.setAdapter(adapter);

        // 1. CREATE Operation: Add new task
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String taskTitle = etTaskInput.getText().toString().trim();
                if (TextUtils.isEmpty(taskTitle)) {
                    Toast.makeText(Practical10Backend.this, "Please enter a task title!", Toast.LENGTH_SHORT).show();
                    return;
                }

                TaskEntity newTask = new TaskEntity(taskTitle, false);
                databaseExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        database.taskDao().insert(newTask);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                etTaskInput.setText("");
                                Toast.makeText(Practical10Backend.this, "Task created in Room DB!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        loadTasksFromDatabase();
                    }
                });
            }
        });

        // Clear All Tasks
        tvClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Practical10Backend.this)
                        .setTitle("Clear All Tasks")
                        .setMessage("Are you sure you want to delete all tasks from the database?")
                        .setPositiveButton("Clear All", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseExecutor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        database.taskDao().deleteAll();
                                        loadTasksFromDatabase();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        // 2. READ Operation: Fetch all tasks from database on activity start
        loadTasksFromDatabase();
    }

    // READ Operation: Query Room Database
    private void loadTasksFromDatabase() {
        databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<TaskEntity> tasks = database.taskDao().getAllTasks();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setTasks(tasks);

                        int completedCount = 0;
                        for (TaskEntity task : tasks) {
                            if (task.isCompleted()) {
                                completedCount++;
                            }
                        }

                        tvTotalPill.setText("📋 Total: " + tasks.size());
                        tvCompletedPill.setText("✅ Done: " + completedCount);

                        // Toggle Empty State vs List visibility
                        if (tasks.isEmpty()) {
                            layoutEmptyState.setVisibility(View.VISIBLE);
                            rvTasks.setVisibility(View.GONE);
                        } else {
                            layoutEmptyState.setVisibility(View.GONE);
                            rvTasks.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    // 3. UPDATE Operation: Toggle status (completed vs pending)
    @Override
    public void onToggleStatus(TaskEntity task, boolean isCompleted) {
        task.setCompleted(isCompleted);
        databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                database.taskDao().update(task);
                loadTasksFromDatabase();
            }
        });
    }

    // 3. UPDATE Operation: Edit task title via dialog
    @Override
    public void onEdit(final TaskEntity task) {
        final EditText input = new EditText(this);
        input.setText(task.getTitle());
        input.setSelection(task.getTitle().length());

        new AlertDialog.Builder(this)
                .setTitle("Edit Task (Update)")
                .setView(input)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String updatedTitle = input.getText().toString().trim();
                        if (!TextUtils.isEmpty(updatedTitle)) {
                            task.setTitle(updatedTitle);
                            databaseExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    database.taskDao().update(task);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(Practical10Backend.this, "Task updated in Room DB!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    loadTasksFromDatabase();
                                }
                            });
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // 4. DELETE Operation: Remove task from Room DB
    @Override
    public void onDelete(final TaskEntity task) {
        databaseExecutor.execute(new Runnable() {
            @Override
            public void run() {
                database.taskDao().delete(task);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Practical10Backend.this, "Task deleted from Room DB!", Toast.LENGTH_SHORT).show();
                    }
                });
                loadTasksFromDatabase();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseExecutor.shutdown();
    }
}
