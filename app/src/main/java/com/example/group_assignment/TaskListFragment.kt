package com.example.group_assignment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.AdapterView
import android.widget.EditText
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.group_assignment.databinding.FragmentTaskListBinding
import com.example.group_assignment.viewmodel.TaskListAdapter
import com.example.group_assignment.viewmodel.TaskListViewModel
import com.example.group_assignment.viewmodel.TaskListViewModelFactory
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(ServiceLocator.repo(requireContext()))
    }

    private lateinit var taskAdapter: TaskListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSettings = view.findViewById<android.widget.ImageButton>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_settingsFragment)
        }

        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                // When user clicks a tab, update the ViewModel
                viewModel.setTab(tab?.position ?: 0)
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })

        taskAdapter = TaskListAdapter(
            onTaskChecked = { task, isDone ->
                viewModel.toggleTaskDone(task, isDone)
            },
            onDeleteClick = { task ->
                com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete '${task.title}'?")
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setPositiveButton("Delete") { dialog, _ ->
                        viewModel.deleteTask(task.id)
                        dialog.dismiss()
                    }
                    .show()
            },
            onEditClick = { task ->
                val bundle = Bundle().apply {
                    putLong("taskId", task.id)
                }
                findNavController().navigate(R.id.taskFormFragment, bundle)
            }
        )

        binding.recycler.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasks.collect { taskAdapter.submitList(it) }
        }

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.taskFormFragment)
        }

        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        val spinnerSort = view.findViewById<Spinner>(R.id.spinnerSort)
        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val option = when (position) {
                    0 -> TaskListViewModel.SortOption.DATE_NEAREST
                    1 -> TaskListViewModel.SortOption.PRIORITY_HIGH_LOW
                    2 -> TaskListViewModel.SortOption.PRIORITY_LOW_HIGH
                    else -> TaskListViewModel.SortOption.DATE_NEAREST
                }
                viewModel.setSortOption(option)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
