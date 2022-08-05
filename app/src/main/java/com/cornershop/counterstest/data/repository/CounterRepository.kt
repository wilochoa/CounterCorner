package com.cornershop.counterstest.data.repository

import android.util.Log
import com.cornershop.counterstest.data.database.entities.toDatabase
import com.cornershop.counterstest.data.models.ResultCounter
import com.cornershop.counterstest.data.server.toServer
import com.cornershop.counterstest.domain.entity.Counter
import com.cornershop.counterstest.domain.entity.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import com.cornershop.counterstest.data.database.entities.Counter as CounterDB
import com.cornershop.counterstest.data.server.Counter as CounterServer


class CounterRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) {
    val TAG = "CounterRepository"
    fun allCounters(): Flow<ResultCounter> = flow {
        var message = ""
        remoteDataSource.fetchAllCounters().collect { counterList ->
            counterList.onSuccess {
                localDataSource.clearAll()
                localDataSource.saveCounters(it.map { it.toDatabase() })
            }
                .onFailure {
                    message = it.message.toString()
                }
        }

        localDataSource.getCounters().collect {
            emit(ResultCounter(it.map { counter -> counter.toDomain() }, message))
        }
    }

    fun incrementCounter(counter: Counter): Flow<ResultCounter> = flow {
        var message = ""
        remoteDataSource.incrementCounter(counter.toServer()).collect { counterList ->
            counterList.onSuccess {
                localDataSource.clearAll()
                localDataSource.saveCounters(it.map { it.toDatabase() })
            }
                .onFailure {
                    message = it.message.toString()
                }
        }

        localDataSource.getCounters().collect {
            emit(ResultCounter(it.map { counter -> counter.toDomain() }, message))
        }
    }

    fun decrementCounter(counter: Counter): Flow<ResultCounter> = flow {
        var message = ""
        remoteDataSource.decrementCounter(counter.toServer()).collect { counterList ->
            counterList.onSuccess {
                localDataSource.clearAll()
                localDataSource.saveCounters(it.map { it.toDatabase() })
            }
                .onFailure {
                    message = it.message.toString()
                }
        }

        localDataSource.getCounters().collect {
            emit(ResultCounter(it.map { counter -> counter.toDomain() }, message))
        }
    }

    fun createCounter(counter: Counter): Flow<ResultCounter> = flow {
        var message = ""
        remoteDataSource.createCounter(counter.toServer()).collect { counterList ->
            counterList.onSuccess {
                localDataSource.clearAll()
                localDataSource.saveCounters(it.map { it.toDatabase() })
            }
                .onFailure {
                    message = it.message.toString()
                }
        }

        localDataSource.getCounters().collect {
            emit(ResultCounter(it.map { counter -> counter.toDomain() }, message))
        }
    }

    fun deleteCounter(counters: List<Counter>): Flow<ResultCounter> = flow {
        var message = ""
        var countersDomain: MutableList<CounterServer> = arrayListOf()
        for (counter in counters) {
            var counterDomain: CounterServer = counter.toServer()
            countersDomain.add(counterDomain)
        }
        remoteDataSource.deleteCounter(countersDomain).collect { counterList ->
            counterList.onSuccess {
                localDataSource.clearAll()
                localDataSource.saveCounters(it.map { it.toDatabase() })
            }
                .onFailure {
                    message = it.message.toString()
                }

        }

        localDataSource.getCounters().collect {
            emit(ResultCounter(it.map { counter -> counter.toDomain() }, message))
        }
    }

    fun searchByTitle(title: String): Flow<List<Counter>> = flow {
        localDataSource.searchByTitle(title).collect {
            emit(it.map { counter -> counter.toDomain() })
        }
    }

    fun searchById(id: String): Flow<Counter> = flow {
        localDataSource.searchById(id)
    }


}

interface RemoteDataSource {
    fun fetchAllCounters(): Flow<Result<List<CounterServer>>>
    fun createCounter(counter: CounterServer): Flow<Result<List<CounterServer>>>
    fun deleteCounter(counter: List<CounterServer>): Flow<Result<List<CounterServer>>>
    fun incrementCounter(counter: CounterServer): Flow<Result<List<CounterServer>>>
    fun decrementCounter(counter: CounterServer): Flow<Result<List<CounterServer>>>
}

interface LocalDataSource {
    suspend fun isEmpty(): Boolean
    suspend fun count(): Int
    suspend fun saveCounters(counters: List<CounterDB>)
    suspend fun clearAll()
    suspend fun getCounters(): Flow<List<CounterDB>>
    suspend fun searchById(id: String): CounterDB
    suspend fun searchByTitle(title: String): Flow<List<CounterDB>>

}