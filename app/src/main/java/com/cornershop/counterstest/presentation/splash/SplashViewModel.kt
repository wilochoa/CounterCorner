package com.cornershop.counterstest.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cornershop.counterstest.domain.entity.Counter
import com.cornershop.counterstest.domain.usecases.*
import com.cornershop.counterstest.presentation.main.MainViewModel
import com.cornershop.counterstest.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getLoggedUseCase: LoggedUseCase,
) : ViewModel() {

    var _isLogged = SingleLiveEvent<Boolean>()

    fun setLoggedState(isLogged: Boolean) {
        viewModelScope.launch {
            getLoggedUseCase.setLoggedState(isLogged)
        }
    }

    fun getLoggedState() {
        viewModelScope.launch {
            getLoggedUseCase.getLoggedState().collect {
                _isLogged.postValue(it)
            }
        }
    }
}