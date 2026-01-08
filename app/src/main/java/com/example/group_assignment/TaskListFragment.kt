package com.example.group_assignment

import android.os.Bundle
import android.view.*
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
        setHasOptionsMenu(true) // for menu options like settings & sort
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Initialize adapter ---
        taskAdapter = TaskListAdapter { task, isDone ->
            viewModel.toggleTaskDone(task, isDone)
        }

        // --- Setup RecyclerView ---
        binding.recycler.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // --- Observe tasks from ViewModel ---
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasks.collect { taskAdapter.submitList(it) }
        }

        // --- FAB to add new task ---
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.taskFormFragment)
        }
    }

    // --- Menu options (settings & sort) ---
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                findNavController().navigate(R.id.action_taskListFragment_to_settingsFragment)
                true
            }
            R.id.sort_title -> {
                viewModel.setSortOption(TaskListViewModel.SortOption.TITLE)
                true
            }
            R.id.sort_due -> {
                viewModel.setSortOption(TaskListViewModel.SortOption.DUE_DATE)
                true
            }
            R.id.sort_done -> {
                viewModel.setSortOption(TaskListViewModel.SortOption.DONE)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
