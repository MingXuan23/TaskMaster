package com.example.group_assignment

import android.content.ContentValues
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.group_assignment.databinding.FragmentSettingsBinding
import com.example.group_assignment.viewmodel.TaskListViewModel
import com.example.group_assignment.viewmodel.TaskListViewModelFactory
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private var currentTaskList: List<Any> = emptyList()

    private val viewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(ServiceLocator.repo(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasks.collect { tasks ->
                currentTaskList = tasks
            }
        }


        binding.btnBackup.setOnClickListener {
            if (currentTaskList.isNotEmpty()) {

                exportTasksToPublicStorage(currentTaskList)
            } else {

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val taskList = viewModel.tasks.first()
                        if (taskList.isNotEmpty()) {
                            exportTasksToPublicStorage(taskList)
                        } else {
                            Toast.makeText(context, "No tasks available to backup.", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun exportTasksToPublicStorage(taskList: List<Any>) {
        try {

            val gson = GsonBuilder().setPrettyPrinting().create()

            val backupWrapper = mapOf(
                "app_name" to "TaskMaster Pro",
                "export_date" to SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date()),
                "total_records" to taskList.size,
                "data" to taskList
            )

            val jsonString = gson.toJson(backupWrapper)
            val fileName = "TaskMaster_Backup_${System.currentTimeMillis()}.json"

            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/json")
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
            }

            val resolver = requireContext().contentResolver
            val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                MediaStore.Downloads.EXTERNAL_CONTENT_URI
            } else {
                MediaStore.Files.getContentUri("external")
            }

            val uri = resolver.insert(collection, values)
            uri?.let {
                resolver.openOutputStream(it)?.use { out ->
                    out.write(jsonString.toByteArray())
                }

                Toast.makeText(context, "Backup Successful! Saved to Downloads", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}