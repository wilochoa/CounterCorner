package com.cornershop.counterstest.data.database

import com.cornershop.counterstest.data.database.entities.Counter
import com.cornershop.counterstest.data.repository.LocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RoomDataSource @Inject constructor(database: CounterDatabase) : LocalDataSource {

    private val counterDao = database.counterDao()

    override suspend fun isEmpty(): Boolean = counterDao.counterCount() <= 0

    override suspend fun count(): Int = counterDao.counterCount()

    override suspend fun saveCounters(counters: List<Counter>) = counterDao.insertAll(counters)

    override suspend fun clearAll() = counterDao.clearAll()

    override suspend fun getCounters(): Flow<List<Counter>> = counterDao.getAll()

    override suspend fun searchById(id: String): Counter = counterDao.getById(id)

    override suspend fun searchByTitle(title: String): Flow<List<Counter>> = counterDao.getByTitle(title)

}