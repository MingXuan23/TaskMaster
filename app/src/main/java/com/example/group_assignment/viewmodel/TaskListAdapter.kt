package com.example.group_assignment.viewmodel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.group_assignment.R
import com.example.group_assignment.data.Task
import com.example.group_assignment.databinding.ItemListBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskListAdapter(
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit
) : ListAdapter<Task, TaskListAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TaskViewHolder(private val binding: ItemListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.txtTitle.text = task.title

            // --- Priority ---
            val (priorityText, priorityColor) = when (task.priority) {
                0 -> "Low" to android.R.color.holo_green_dark
                1 -> "Medium" to android.R.color.holo_orange_dark
                2 -> "High" to android.R.color.holo_red_dark
                else -> "Unknown" to android.R.color.black
            }
            binding.tvPriority.text = "Priority: $priorityText"
            binding.tvPriority.setTextColor(ContextCompat.getColor(binding.root.context, priorityColor))

            // --- Date ---
            task.dueAt?.let {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.tvDueDate.text = "Due: ${sdf.format(Date(it))}"
            } ?: run {
                binding.tvDueDate.text = "Due: Not set"
            }

            // --- Status ---
            binding.chkDone.setOnCheckedChangeListener(null)
            binding.chkDone.isChecked = task.done
            updateStatus(task)

            binding.chkDone.setOnCheckedChangeListener { _, isChecked ->
                val updatedTask = task.copy(done = isChecked)
                updateStatus(updatedTask)
                onTaskChecked(updatedTask, isChecked)
            }

            // --- EDIT BUTTON ---
            binding.btnEdit.setOnClickListener {
                onEditClick(task)
            }

            // --- DELETE BUTTON ---
            binding.btnDelete.setOnClickListener {
                onDeleteClick(task)
            }
        }

//        private fun updateStatus(task: Task) {
//            val now = System.currentTimeMillis()
//            when {
//                task.done -> {
//                    binding.tvStatus.text = "Status: Done"
//                    binding.tvStatus.setTextColor(ContextCompat.getColor(binding.root.context, android.R.color.holo_green_dark))
//                }
//                task.dueAt != null && task.dueAt < now -> {
//                    binding.tvStatus.text = "Status: Overdue"
//                    binding.tvStatus.setTextColor(ContextCompat.getColor(binding.root.context, android.R.color.holo_red_dark))
//                }
//                else -> {
//                    binding.tvStatus.text = "Status: Pending"
//                    binding.tvStatus.setTextColor(ContextCompat.getColor(binding.root.context, android.R.color.black))
//                }
//            }
//        }

        private fun updateStatus(task: Task) {
            val now = System.currentTimeMillis()
            val context = binding.root.context

            when {
                task.done -> {
                    binding.tvStatus.text = "Status: Done"
                    binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_green))

                    binding.txtTitle.paintFlags = binding.txtTitle.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                    binding.txtTitle.alpha = 0.5f
                }

                task.dueAt != null && task.dueAt < now -> {
                    binding.tvStatus.text = "Status: Overdue"
                    binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_red))

                    binding.txtTitle.paintFlags = binding.txtTitle.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    binding.txtTitle.alpha = 1.0f
                }

                else -> {
                    binding.tvStatus.text = "Status: Pending"
                    binding.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.status_pending))

                    binding.txtTitle.paintFlags = binding.txtTitle.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    binding.txtTitle.alpha = 1.0f
                }
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean = oldItem == newItem
    }
}