package com.cornershop.counterstest.presentation.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import com.cornershop.counterstest.R
import com.cornershop.counterstest.databinding.FragmentMainBinding
import com.cornershop.counterstest.presentation.MainActivity
import com.cornershop.counterstest.presentation.main.adapter.KeyProvider
import com.cornershop.counterstest.presentation.main.adapter.MainAdapter
import com.example.android.extensions.showAlert
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(), ActionMode.Callback,
    android.widget.SearchView.OnQueryTextListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: MainAdapter
    private lateinit var tracker: SelectionTracker<String>
    private var actionMode: ActionMode? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAddCounter.setOnClickListener {
            findNavController()
                .navigate(R.id.action_mainFragment_to_createCounterFragment)
        }
        adapter = MainAdapter { viewModel.onAction(it) }
        binding.recyclerView.adapter = adapter

        initUI()
        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            when (uiState) {

                is MainViewModel.UIState.HasContent -> hasContentState(uiState)

                is MainViewModel.UIState.NoConnectionDialog -> noContentStateDialog(uiState)

                MainViewModel.UIState.Loading -> binding.swipeRefresh.isRefreshing

                MainViewModel.UIState.NoContent -> noContentState()

                MainViewModel.UIState.NoConnection -> noConnectionState()

                else -> {}
            }
        }
    }

    private fun initUI() {
        setupToolbar()
        initTracker()
    }

    private fun initTracker() {
        tracker = SelectionTracker.Builder(
            "selectionItem",
            binding.recyclerView,
            KeyProvider(adapter),
            MainAdapter.DetailsLookup(binding.recyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()

                    if (actionMode == null) {
                        val currentActivity = activity as MainActivity
                        actionMode = currentActivity.startSupportActionMode(this@MainFragment)
                    }

                    val items = tracker.selection.size()
                    if (items > 0) {
                        actionMode?.title = getString(R.string.action_selected, items)
                    } else {
                        actionMode?.finish()
                    }
                }
            })

        adapter.selectionTracker = tracker
    }

    private fun noContentStateDialog(uiState: MainViewModel.UIState.NoConnectionDialog) {
        requireContext().showAlert(
            getString(R.string.error_updating_counter_title, uiState.counter, uiState.amount),
            getString(R.string.connection_error_description), getString(R.string.dismiss)
        ) { }
    }

    private fun noContentState() {
        binding.textTimes.visibility = View.INVISIBLE
        binding.textItems.visibility = View.INVISIBLE
        binding.textNoCounters.root.visibility = View.VISIBLE
        binding.textOffline.root.visibility = View.GONE
    }

    private fun noConnectionState() {
        binding.textTimes.visibility = View.INVISIBLE
        binding.textItems.visibility = View.INVISIBLE
        binding.textNoCounters.root.visibility = View.GONE
        binding.textOffline.root.visibility = View.VISIBLE
        binding.textOffline.buttonRetry.setOnClickListener {
            viewModel.getCounters()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun hasContentState(uiState: MainViewModel.UIState.HasContent) {
        adapter.submitList(uiState.counters)
        adapter.notifyDataSetChanged()
        binding.textTimes.text = resources.getQuantityString(
            R.plurals.numberOfTimes,
            uiState.times,
            uiState.times
        )
        binding.swipeRefresh.isRefreshing = false
        binding.textTimes.visibility = View.VISIBLE
        val size = uiState.counters.size
        binding.textItems.text =
            resources.getQuantityString(R.plurals.numberOfItems, size, size)
        binding.textItems.visibility = View.VISIBLE
        binding.textNoCounters.root.visibility = View.GONE
        binding.textOffline.root.visibility = View.GONE
        binding.searchBar.setOnQueryTextListener(this@MainFragment)
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getCounters()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        newText?.let { search(it) }
        return true
    }

    private fun search(query: String) {
        val searchQuery = "%$query%"

        viewModel.searchCounter(searchQuery).observe(this) {
            if (it.isEmpty()) {

            } else {
                binding.textTimes.text = resources.getQuantityString(
                    R.plurals.numberOfTimes,
                    it.sumOf { it.count },
                    it.sumOf { it.count },
                )
                binding.textTimes.visibility = View.VISIBLE
                val size = it.size
                binding.textItems.text =
                    resources.getQuantityString(R.plurals.numberOfItems, size, size)
                adapter.submitList(it)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        tracker.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        tracker.onRestoreInstanceState(savedInstanceState)
        if (tracker.hasSelection()) {
            actionMode = (activity as MainActivity).startSupportActionMode(this@MainFragment)
            actionMode?.title = getString(R.string.action_selected, tracker.selection.size())
        }

        super.onViewStateRestored(savedInstanceState)
    }

    private fun setupToolbar() {
        val appCompatActivity = activity as AppCompatActivity
        appCompatActivity.setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.menu_actions, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?) = true

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_delete -> {
                val mainAdapter = binding.recyclerView.adapter as MainAdapter

                val selected = mainAdapter.currentList.filter {
                    tracker.selection.contains(it.id)
                }.toMutableList()

                var dialogMessage = ""
                dialogMessage = if (selected.size == 1)
                    getString(R.string.delete_x_question, selected.first().title)
                else getString(R.string.delete_x_question, selected.size.toString() + " counters")

                requireContext().showAlert(
                    "",
                    dialogMessage,
                    getString(R.string.delete),
                    getString(R.string.cancel),
                    {
                        viewModel.deleteCounters(selected)
                    },
                    {}
                )

                /*   val counters = mainAdapter.currentList.toMutableList()

                   counters.removeAll(selected)*/

                //  updateAndSave(groceries)
                actionMode?.finish()
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        tracker.clearSelection()
        actionMode = null

        val adapter = (binding.recyclerView.adapter as MainAdapter)
        binding.recyclerView.adapter = adapter
    }

}