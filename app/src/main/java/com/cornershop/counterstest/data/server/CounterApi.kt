package com.cornershop.counterstest.data.server

import retrofit2.http.*

interface CounterApi {

    @GET("/api/v1/counters")
    suspend fun fetchAllCounters(): Result<List<Counter>>

    @POST("/api/v1/counter")
    suspend fun createCounter(@Body counter: Counter): Result<List<Counter>>

    @POST("/api/v1/counter/inc")
    suspend fun incrementCounter(@Body counter: Counter): Result<List<Counter>>

    @POST("/api/v1/counter/dec")
    suspend fun decrementCounter(@Body counter: Counter): Result<List<Counter>>

    @HTTP(method = "DELETE", path = "/api/v1/counter", hasBody = true)
    suspend fun deleteCounter(@Body counter: Counter): Result<List<Counter>>

}