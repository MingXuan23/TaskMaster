package com.example.group_assignment.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import com.example.group_assignment.data.ITaskRepository
import com.example.group_assignment.data.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class TaskListViewModel(private val repository: ITaskRepository) : ViewModel() {


    private val dummyListFlow = flowOf(
        listOf(
            Task(1, "Buy Groceries", 1, 1000L, false),
            Task(2, "Submit Lab", 2, 5000L, true),
            Task(3, "Apple", 0, 2000L, false)
        )
    )

    enum class SortOption { TITLE, DUE_DATE, DONE }
    private val _sortOption = MutableStateFlow(SortOption.TITLE)

    //val tasks: StateFlow<List<Task>> = repository.getTasks()
    val tasks: StateFlow<List<Task>> = dummyListFlow
        .combine(_sortOption) { taskList, option ->
            when (option) {
                SortOption.TITLE -> taskList.sortedBy { it.title.lowercase() }
                SortOption.DUE_DATE -> taskList.sortedBy { it.dueAt ?: Long.MAX_VALUE }
                SortOption.DONE -> taskList.sortedByDescending { it.done }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // [cite: 550]
            initialValue = emptyList() // [cite: 551]
        )

    fun setSortOption(option: SortOption) {
        _sortOption.value = option
    }

    fun toggleTaskDone(task: Task, isDone: Boolean) {
        viewModelScope.launch {
            repository.toggleDone(task.id, isDone) // [cite: 552, 554]
        }
    }


}