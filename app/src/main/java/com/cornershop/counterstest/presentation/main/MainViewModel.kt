package com.cornershop.counterstest.presentation.main


import androidx.lifecycle.*
import com.cornershop.counterstest.domain.entity.Counter
import com.cornershop.counterstest.domain.usecases.*
import com.cornershop.counterstest.presentation.main.adapter.MainAdapter.ItemAction
import com.cornershop.counterstest.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCountersUseCase: GetCountersUseCase,
    private val decrementCounterUseCase: DecrementCounterUseCase,
    private val incrementCounterUseCase: IncrementCounterUseCase,
    private val deleteCounterUseCase: DeleteCounterUseCase,
    private val searchCounterByTitleUseCase: SearchCounterByTitleUseCase
) : ViewModel() {

    private val _uiState = SingleLiveEvent<UIState>()
    val uiState: LiveData<UIState> get() = _uiState

    init {
        getCounters()
    }

    fun getCounters() {
        viewModelScope.launch {
            _uiState.postValue(UIState.Loading)
            getCountersUseCase.invoke().collect { resultCounter ->
                if (resultCounter.listCounter.isEmpty() && resultCounter.message.isEmpty()) {
                    _uiState.postValue(UIState.NoContent)
                } else if (resultCounter.listCounter.isEmpty() && resultCounter.message.contains("No internet")) {
                    _uiState.postValue(UIState.NoConnection)
                } else {
                    _uiState.postValue(
                        UIState.HasContent(
                            resultCounter.listCounter,
                            resultCounter.listCounter.sumOf { it.count })
                    )
                }
            }
        }
    }

    fun onAction(action: ItemAction) {
        when (action) {
            is ItemAction.Click -> {

            }
            is ItemAction.Decrement -> decrementCounter(action)
            is ItemAction.Increment -> incrementCounter(action)
        }
    }

    private fun incrementCounter(action: ItemAction.Increment) {
        viewModelScope.launch {
            _uiState.postValue(UIState.Loading)
            incrementCounterUseCase.invoke(action.counter).collect { resultCounter ->
                if (resultCounter.message.isNotEmpty()) {
                    _uiState.postValue(
                        UIState.NoConnectionDialog(
                            action.counter.title,
                            action.amount
                        )
                    )
                } else {
                    _uiState.postValue(
                        UIState.HasContent(
                            resultCounter.listCounter,
                            resultCounter.listCounter.sumOf { it.count })
                    )
                }
            }
        }
    }

    private fun decrementCounter(action: ItemAction.Decrement) {
        viewModelScope.launch {
            _uiState.postValue(UIState.Loading)
            decrementCounterUseCase.invoke(action.counter).collect { resultCounter ->
                if (resultCounter.message.isNotEmpty()) {
                    _uiState.postValue(
                        UIState.NoConnectionDialog(
                            action.counter.title,
                            action.amount
                        )
                    )
                } else {
                    _uiState.postValue(
                        UIState.HasContent(
                            resultCounter.listCounter,
                            resultCounter.listCounter.sumOf { it.count })
                    )
                }
            }
        }
    }

    fun searchCounter(searchQuery: String): LiveData<List<Counter>> {
        return searchCounterByTitleUseCase.invoke(searchQuery).asLiveData()
    }

    fun deleteCounters(counter: List<Counter>) {
        viewModelScope.launch {
            deleteCounterUseCase.invoke(counter).collect { resultCounter ->
                if (resultCounter.listCounter.isEmpty() && resultCounter.message.isEmpty()) {
                    _uiState.postValue(UIState.NoContent)
                } else if (resultCounter.listCounter.isEmpty() && resultCounter.message.contains("No internet")) {
                    _uiState.postValue(UIState.NoConnection)
                } else {
                    _uiState.postValue(
                        UIState.HasContent(
                            resultCounter.listCounter,
                            resultCounter.listCounter.sumOf { it.count })
                    )
                }
            }
        }
    }


    sealed class UIState() {
        object NoContent : UIState()
        object NoConnection : UIState()
        object Loading : UIState()
        data class NoConnectionDialog(val counter: String, val amount: Int) : UIState()
        data class Search(val counters: List<Counter>) : UIState()
        data class HasContent(val counters: List<Counter>, val times: Int) : UIState()
    }
}