package com.example.group_assignment.data

import kotlinx.coroutines.flow.Flow

interface ITaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun save(task: Task)

    // --- delete by ID ---
    suspend fun deleteTask(taskId: Long)

    suspend fun toggleDone(id: Long, isDone: Boolean)

    // --- get single task by ID ---
    suspend fun getTaskById(taskId: Long): Task?

    // --- update existing task ---
    suspend fun updateTask(taskId: Long, title: String, priority: Int, dueAt: Long?)
}
