package com.example.group_assignment.data.local

import androidx.room.*
import androidx.room.RoomDatabase
import com.example.group_assignment.data.Task

@Database(entities = [Task::class], version = 1) // [cite: 504]
abstract class AppDb : RoomDatabase() {
    abstract fun taskDao(): TaskDao // [cite: 506]
}