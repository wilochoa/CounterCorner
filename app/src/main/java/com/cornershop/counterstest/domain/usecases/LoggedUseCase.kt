package com.cornershop.counterstest.domain.usecases


import com.cornershop.counterstest.data.repository.LoggedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoggedUseCase @Inject constructor(private val repository: LoggedRepository) {
    suspend fun setLoggedState(logged: Boolean) = repository.saveUserLoggedInState(logged)
    fun getLoggedState(): Flow<Boolean> = repository.getUserLoggedInState()
}