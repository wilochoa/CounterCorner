package com.cornershop.counterstest.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cornershop.counterstest.data.database.dao.CounterDao
import com.cornershop.counterstest.data.database.entities.Counter

@Database(entities = [Counter::class], version = 1)
abstract class CounterDatabase : RoomDatabase() {

    abstract fun counterDao(): CounterDao

}