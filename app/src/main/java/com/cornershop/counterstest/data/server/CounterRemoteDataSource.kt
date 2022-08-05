package com.cornershop.counterstest.data.server

import com.cornershop.counterstest.data.repository.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CounterRemoteDataSource @Inject constructor(
    private val counterApi: CounterApi
) : RemoteDataSource {

    override fun fetchAllCounters(): Flow<Result<List<Counter>>> = flow {
        val counters = counterApi.fetchAllCounters()
        emit(counters) // Emits the result of the request to the flow
    }

    override fun createCounter(counter: Counter): Flow<Result<List<Counter>>> = flow {
        val counters = counterApi.createCounter(counter)
        emit(counters)
    }

    override fun deleteCounter(counters: List<Counter>): Flow<Result<List<Counter>>> = flow {
       for(counter in counters){
           val counters = counterApi.deleteCounter(counter)
           emit(counters)
       }
    }

    override fun incrementCounter(counter: Counter): Flow<Result<List<Counter>>> = flow {
        val counters = counterApi.incrementCounter(counter)
        emit(counters)
    }

    override fun decrementCounter(counter: Counter): Flow<Result<List<Counter>>> = flow {
        val counters = counterApi.decrementCounter(counter)
        emit(counters)
    }
}