package com.example.group_assignment.data

import kotlinx.coroutines.flow.Flow

interface ITaskRepository {
    fun getTasks(): Flow<List<Task>>
    suspend fun save(task: Task)
    suspend fun delete(task: Task)
    suspend fun toggleDone(id: Long, isDone: Boolean)
}