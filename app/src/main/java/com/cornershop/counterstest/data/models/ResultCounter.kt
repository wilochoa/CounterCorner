package com.cornershop.counterstest.data.models

import com.cornershop.counterstest.domain.entity.Counter
import kotlinx.coroutines.flow.Flow

data class ResultCounter(
    val listCounter:List<Counter>,
    val message:String
)