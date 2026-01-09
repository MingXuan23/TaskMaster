package com.example.group_assignment.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.group_assignment.data.ITaskRepository
import com.example.group_assignment.data.Task
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TaskListViewModel(private val repository: ITaskRepository) : ViewModel() {

    enum class SortOption { DATE_NEAREST, PRIORITY_HIGH_LOW, PRIORITY_LOW_HIGH }
    private val _sortOption = MutableStateFlow(SortOption.DATE_NEAREST)
    private val _currentTab = MutableStateFlow(0)

    private val _searchQuery = MutableStateFlow("")

    val tasks: StateFlow<List<Task>> =
        combine(
            repository.getTasks(),
            _sortOption,
            _currentTab,
            _searchQuery
        ) { allTasks, sort, tabIndex, query ->

            // Filter by Tab
            var filtered = if (tabIndex == 0) {
                allTasks.filter { !it.done }
            } else {
                allTasks.filter { it.done }
            }

            // Filter by Search
            if (query.isNotEmpty()) {
                filtered = filtered.filter {
                    it.title.contains(query, ignoreCase = true)
                }
            }

            // Sort
            when (sort) {
                SortOption.DATE_NEAREST -> {
                    // Sort by date.
                    filtered.sortedBy { it.dueAt ?: Long.MAX_VALUE }
                }
                SortOption.PRIORITY_HIGH_LOW -> {
                    // High to Low
                    filtered.sortedByDescending { it.priority }
                }
                SortOption.PRIORITY_LOW_HIGH -> {
                    // Low to High
                    filtered.sortedBy { it.priority }
                }
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

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
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

    fun getTaskById(taskId: Long, callback: (Task?) -> Unit) {
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            callback(task)
        }
    }

    fun updateTask(taskId: Long, title: String, priority: Int, dueAt: Long?) {
        viewModelScope.launch {
            repository.updateTask(taskId, title, priority, dueAt)
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }
}
