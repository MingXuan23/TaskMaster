package com.example.group_assignment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.group_assignment.data.ITaskRepository
import com.example.group_assignment.data.Task
import com.example.group_assignment.databinding.FragmentTaskListBinding
import com.example.group_assignment.viewmodel.TaskListAdapter
import com.example.group_assignment.viewmodel.TaskListViewModel
import com.example.group_assignment.viewmodel.TaskListViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TaskListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskListFragment : Fragment() {
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TaskListViewModel by viewModels {
        TaskListViewModelFactory(ServiceLocator.repo(requireContext()))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // Crucial for the menu to appear
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
       // setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val taskAdapter = TaskListAdapter { task, isChecked ->
            viewModel.toggleTaskDone(task, isChecked)
        }

        binding.recycler.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tasks.collect { taskAdapter.submitList(it) }
        }

//        val dummyData = listOf(
//            Task(
//                title = "Buy Groceries",
//                priority = 1,
//                dueAt = System.currentTimeMillis(),
//                done = false
//            ),
//            Task(title = "Submit Lab", priority = 2, dueAt = System.currentTimeMillis(), done = true)
//        )
//        taskAdapter.submitList(dummyData)

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.taskFormFragment)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            // Navigation to Settings (Role A Task)
            R.id.action_settings -> {
                findNavController().navigate(R.id.action_taskListFragment_to_settingsFragment)
                true
            }
            // Sorting Logic (Role B Task)
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

