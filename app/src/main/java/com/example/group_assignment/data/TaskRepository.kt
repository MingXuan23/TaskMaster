package com.example.group_assignment.data

import com.example.group_assignment.data.local.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) : ITaskRepository {
    override fun getTasks(): Flow<List<Task>> = dao.observeAll() // [cite: 514]

    override suspend fun save(task: Task) {
        dao.upsert(task) // [cite: 514]
    }

    override suspend fun delete(task: Task) {
        dao.delete(task) // [cite: 514]
    }

    override suspend fun toggleDone(id: Long, isDone: Boolean) {
        dao.setDone(id, isDone) // [cite: 514, 516]
    }
}