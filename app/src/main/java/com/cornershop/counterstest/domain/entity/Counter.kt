package com.cornershop.counterstest.domain.entity

import com.cornershop.counterstest.data.database.entities.Counter as CounterDB

data class Counter(
    val id: String,
    val title: String,
    val count: Int
) {
    constructor(title: String) : this("", title, 0)
}

fun CounterDB.toDomain() =
    Counter(
        id = id,
        title = title,
        count = count
    )