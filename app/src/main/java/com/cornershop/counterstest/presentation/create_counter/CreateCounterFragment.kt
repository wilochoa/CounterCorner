package com.cornershop.counterstest.presentation.create_counter

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.cornershop.counterstest.R
import com.cornershop.counterstest.databinding.FragmentCreateCounterBinding
import com.example.android.extensions.showAlert
import com.example.android.extensions.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateCounterFragment : Fragment() {

    private var _binding: FragmentCreateCounterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CreateCounterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateCounterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonClose.setOnClickListener {
            findNavController()
                .navigate(R.id.action_createCounterFragment_to_mainFragment)
        }

        binding.buttonSave.setOnClickListener {
            saveCounter()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycleScope.launchWhenStarted {
                viewModel.uiState.collect { uiState ->
                    when (uiState) {
                        is CreateCounterViewModel.UIState.Error -> {
                            requireContext().showAlert(
                                getString(R.string.error_creating_counter_title),
                                getString(R.string.connection_error_description),
                                getString(R.string.dismiss)
                            ) {}
                            binding.progressBar.visibility = View.GONE
                            binding.buttonSave.visibility = View.VISIBLE
                        }
                        CreateCounterViewModel.UIState.Initial -> {
                            binding.textCreate.isEnabled = true
                            binding.textCreate.isFocusable = true
                            binding.textCreate.inputType = InputType.TYPE_CLASS_TEXT
                            binding.progressBar.visibility = View.GONE
                            binding.buttonSave.visibility = View.VISIBLE
                        }
                        CreateCounterViewModel.UIState.Saved -> {
                            showSnackBar(
                                R.string.counter_created,
                                ContextCompat.getColor(requireContext(), R.color.green)
                            )
                            binding.textCreate.isEnabled = true
                            binding.textCreate.isFocusable = true
                            binding.textCreate.inputType = InputType.TYPE_CLASS_TEXT
                            binding.progressBar.visibility = View.GONE
                            binding.buttonSave.visibility = View.VISIBLE
                            findNavController()
                                .navigate(R.id.action_createCounterFragment_to_mainFragment)
                        }
                        CreateCounterViewModel.UIState.Saving -> {
                            binding.textCreate.isEnabled = false
                            binding.textCreate.isFocusable = false
                            binding.textCreate.inputType = InputType.TYPE_NULL
                            binding.progressBar.visibility = View.VISIBLE
                            binding.buttonSave.visibility = View.INVISIBLE
                        }
                    }
                }
            }
        }
    }

    private fun saveCounter() {
        val title = binding.editQuery.text.toString()
        viewModel.save(title)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}