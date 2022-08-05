package com.cornershop.counterstest.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cornershop.counterstest.domain.entity.Counter as CounterDomain
import com.cornershop.counterstest.data.server.Counter as CounterServer

@Entity(tableName = "counters_table")
data class Counter(
    @PrimaryKey
    val id: String,
    val title: String,
    val count: Int
)

fun CounterDomain.toDatabase() =
    Counter(
        id = id,
        title = title,
        count = count
    )

fun CounterServer.toDatabase() =
    Counter(
        id = id,
        title = title,
        count = count
    )