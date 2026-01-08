package com.example.group_assignment.data

import com.example.group_assignment.data.local.TaskDao
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val dao: TaskDao) : ITaskRepository {
    override fun getTasks(): Flow<List<Task>> = dao.observeAll() // existing

    override suspend fun save(task: Task) {
        dao.upsert(task)
    }

    override suspend fun deleteTask(taskId: Long) = dao.deleteById(taskId)

    override suspend fun toggleDone(id: Long, isDone: Boolean) = dao.setDone(id, isDone)

    // --- NEW ---
    override suspend fun getTaskById(taskId: Long): Task? = dao.getTaskById(taskId)

    override suspend fun updateTask(taskId: Long, title: String, priority: Int, dueAt: Long?) =
        dao.updateTask(taskId, title, priority, dueAt)
}
