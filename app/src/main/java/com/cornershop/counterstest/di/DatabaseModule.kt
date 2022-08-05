package com.cornershop.counterstest.di

import android.content.Context
import androidx.room.Room
import com.cornershop.counterstest.data.database.CounterDatabase
import com.cornershop.counterstest.data.database.RoomDataSource
import com.cornershop.counterstest.data.repository.LocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(
        context,
        CounterDatabase::class.java,
        "counter_database"
    ).build()

    @Singleton
    @Provides
    fun provideDao(database: CounterDatabase) = database.counterDao()

    @Provides
    fun localDataSourceProvider(db: CounterDatabase): LocalDataSource =
        RoomDataSource(db)
}