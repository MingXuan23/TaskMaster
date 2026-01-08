package com.example.group_assignment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.group_assignment.viewmodel.TaskListViewModel
import com.example.group_assignment.viewmodel.TaskListViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskFormFragment : Fragment(R.layout.fragment_task_form) {

    private val viewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(ServiceLocator.repo(requireContext()))
    }

    private var selectedDueAt: Long? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTitle = view.findViewById<EditText>(R.id.etTitle)
        val spinnerPriority = view.findViewById<Spinner>(R.id.spinnerPriority)
        val btnPickDate = view.findViewById<Button>(R.id.btnPickDate)
        val tvDueDate = view.findViewById<TextView>(R.id.tvDueDate)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // --- Date Picker ---
        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            selectedDueAt?.let { calendar.timeInMillis = it }

            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val dpd = DatePickerDialog(requireContext(), { _, y, m, d ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(y, m, d, 0, 0, 0)
                selectedCal.set(Calendar.MILLISECOND, 0)

                selectedDueAt = selectedCal.timeInMillis
                tvDueDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(selectedDueAt)
            }, year, month, day)

            // Disable past dates
            dpd.datePicker.minDate = System.currentTimeMillis()
            dpd.show()
        }

        // --- Save button ---
        btnSave.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val priority = spinnerPriority.selectedItemPosition

            if (title.isEmpty()) {
                etTitle.error = "Title required"
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // Always add new task — no editing
                viewModel.addTask(title, priority, selectedDueAt)
                findNavController().navigateUp()
            }
        }
    }
}
