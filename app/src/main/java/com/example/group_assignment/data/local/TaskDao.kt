package com.example.group_assignment.data.local

import androidx.room.*
import com.example.group_assignment.data.Task
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY done ASC, priority DESC, dueAt ASC") // [cite: 485]
    fun observeAll(): Flow<List<Task>> // [cite: 486]

    @Insert(onConflict = OnConflictStrategy.REPLACE) // [cite: 487, 488]
    suspend fun upsert(task: Task): Long // [cite: 489]

    @Delete
    suspend fun delete(task: Task) // [cite: 490]

    @Query("UPDATE tasks SET done = :isDone WHERE id = :id") // [cite: 491]
    suspend fun setDone(id: Long, isDone: Boolean) // [cite: 492]


}