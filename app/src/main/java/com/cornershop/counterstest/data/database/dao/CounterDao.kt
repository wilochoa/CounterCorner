package com.cornershop.counterstest.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.cornershop.counterstest.data.database.entities.Counter
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterDao {

    @Query("SELECT * FROM counters_table")
    fun getAll(): Flow<List<Counter>>

    @Insert
    suspend fun insertAll(counters: List<Counter>)

    @Query("DELETE FROM counters_table")
    suspend fun clearAll()

    @Query("SELECT COUNT(id) FROM counters_table")
    suspend fun counterCount(): Int

    @Query("SELECT * FROM counters_table WHERE id = :counterId")
    suspend fun getById(counterId: String): Counter

    @Query("SELECT * FROM counters_table WHERE title LIKE :counterTitle")
    fun getByTitle(counterTitle: String): Flow<List<Counter>>

}