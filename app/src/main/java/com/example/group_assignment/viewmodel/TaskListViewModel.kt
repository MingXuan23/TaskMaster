package com.example.group_assignment.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.group_assignment.data.ITaskRepository
import com.example.group_assignment.data.Task
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskListViewModel(private val repository: ITaskRepository) : ViewModel() {

    enum class SortOption { TITLE, DUE_DATE, DONE }
    private val _sortOption = MutableStateFlow(SortOption.TITLE)

    val tasks: StateFlow<List<Task>> =
        repository.getTasks()
            .combine(_sortOption) { taskList, option ->
                when (option) {
                    SortOption.TITLE -> taskList.sortedBy { it.title.lowercase() }
                    SortOption.DUE_DATE -> taskList.sortedBy { it.dueAt ?: Long.MAX_VALUE }
                    SortOption.DONE -> taskList.sortedByDescending { it.done }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun toggleTaskDone(task: Task, isDone: Boolean) {
        viewModelScope.launch {
            repository.toggleDone(task.id, isDone)
        }
    }

    fun addTask(title: String, priority: Int, dueAt: Long?) {
        viewModelScope.launch {
            repository.save(
                Task(
                    title = title,
                    priority = priority,
                    dueAt = dueAt,
                    done = false
                )
            )
        }
    }

    // --- NEW: get a task by ID (for edit) ---
    fun getTaskById(taskId: Long, callback: (Task?) -> Unit) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            callback(task)
        }
    }

    // --- NEW: update existing task ---
    fun updateTask(taskId: Long, title: String, priority: Int, dueAt: Long?) {
        viewModelScope.launch {
            repository.updateTask(taskId, title, priority, dueAt)
        }
    }

    // --- Optional: delete task ---
    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }
}
