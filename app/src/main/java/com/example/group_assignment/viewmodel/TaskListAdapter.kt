package com.example.group_assignment.viewmodel

import android.R
import android.view.LayoutInflater
import android.view.*
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.group_assignment.data.Task
import com.example.group_assignment.databinding.ItemListBinding
import com.example.group_assignment.viewmodel.TaskListAdapter.TaskViewHolder

class TaskListAdapter(
    private val onTaskChecked: (Task, Boolean) -> Unit
) : ListAdapter<Task, TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            binding.txtTitle.text = task.title
            binding.chkDone.isChecked = task.done
            task.iconRes?.let { binding.imgIcon.setImageResource(it) }

            // Nullify listener before setting state to avoid trigger on bind
            binding.chkDone.setOnCheckedChangeListener(null)
            binding.chkDone.isChecked = task.done

            binding.chkDone.setOnCheckedChangeListener { _, isChecked ->
                onTaskChecked(task, isChecked)
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }
}