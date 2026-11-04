package com.example.arpitapplication;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    // 1. CREATE: Insert a new task
    @Insert
    void insert(TaskEntity task);

    // 2. READ: Get all tasks in descending order of ID
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    List<TaskEntity> getAllTasks();

    // 3. UPDATE: Update an existing task (title or completion status)
    @Update
    void update(TaskEntity task);

    // 4. DELETE: Delete a specific task
    @Delete
    void delete(TaskEntity task);

    // Clear all tasks
    @Query("DELETE FROM tasks")
    void deleteAll();
}
