package com.example.group_assignment.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks") //
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, //

    val title: String, //

    /**
     * Priority levels:
     * 0 = Low, 1 = Medium, 2 = High
     */
    val priority: Int, // [cite: 471]

    /**
     * Due date stored as epoch milliseconds [cite: 476]
     */
    val dueAt: Long?, // [cite: 472]

    val done: Boolean = false, //

    val iconRes: Int? = null // [cite: 474]
)