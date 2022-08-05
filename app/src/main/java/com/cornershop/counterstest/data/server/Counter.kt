package com.cornershop.counterstest.data.server

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.cornershop.counterstest.data.database.entities.Counter as CounterDB
import com.cornershop.counterstest.domain.entity.Counter as CounterDomain

@Parcelize
data class Counter(
    val id: String,
    val title: String,
    val count: Int,
) : Parcelable

fun CounterDB.toServer() =
    Counter(
        id = id,
        title = title,
        count = count
    )

fun CounterDomain.toServer() =
    Counter(
        id = id,
        title = title,
        count = count
    )

