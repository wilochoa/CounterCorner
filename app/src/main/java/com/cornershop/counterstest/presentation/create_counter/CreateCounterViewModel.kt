package com.cornershop.counterstest.presentation.create_counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.cornershop.counterstest.domain.entity.Counter
import com.cornershop.counterstest.domain.usecases.CreateCounterUseCase
import com.cornershop.counterstest.presentation.main.MainViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CreateCounterViewModel  @Inject constructor(
    private val createCounterUseCase: CreateCounterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState>(UIState.Initial)
    val uiState: StateFlow<UIState> get() = _uiState

    fun save(title: String) {
        val counter = Counter(title = title)
        viewModelScope.launch {
            _uiState.update { UIState.Saving }

            createCounterUseCase.invoke(counter).collect { resultCounter ->
                if (resultCounter.message.isNotEmpty()) {
                    _uiState.update { UIState.Error }
                } else {
                    _uiState.update { UIState.Saved }
                }
            }
        }
    }

    sealed class UIState() {
        object Initial : UIState()
        object Saving : UIState()
        object Saved : UIState()
        object Error : UIState()
    }
}