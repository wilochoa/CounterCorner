package com.cornershop.counterstest.domain.usecases


import com.cornershop.counterstest.data.models.ResultCounter
import com.cornershop.counterstest.data.repository.CounterRepository
import com.cornershop.counterstest.domain.entity.Counter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeleteCounterUseCase @Inject constructor(private val repository: CounterRepository) {

    operator fun invoke(counters: List<Counter>): Flow<ResultCounter> = repository.deleteCounter(counters)

}