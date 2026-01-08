package com.example.group_assignment.data.local

import androidx.room.*
import com.example.group_assignment.data.Task
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY done ASC, priority DESC, dueAt ASC")
    fun observeAll(): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: Task): Long

    @Delete
    suspend fun delete(task: Task) // optional if you want delete by object

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteById(taskId: Long) // <- required for repository

    @Query("UPDATE tasks SET done = :isDone WHERE id = :id")
    suspend fun setDone(id: Long, isDone: Boolean)

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): Task?

    @Query("UPDATE tasks SET title = :title, priority = :priority, dueAt = :dueAt WHERE id = :taskId")
    suspend fun updateTask(taskId: Long, title: String, priority: Int, dueAt: Long?)
}

