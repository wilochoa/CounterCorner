package com.cornershop.counterstest.di

import com.cornershop.counterstest.BuildConfig
import com.cornershop.counterstest.data.repository.RemoteDataSource
import com.cornershop.counterstest.data.server.CounterApi
import com.cornershop.counterstest.data.server.CounterRemoteDataSource
import com.example.errorhandling.exception.ResultCallAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHeaderInterceptor(): Interceptor {
        return Interceptor {
            val requestBuilder = it.request().newBuilder()
            //hear you can add all headers you want by calling 'requestBuilder.addHeader(name ,  value)'
            it.proceed(requestBuilder.build())
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        headerInterceptor: Interceptor,
    ): OkHttpClient {

        val okHttpClientBuilder = OkHttpClient().newBuilder()
        okHttpClientBuilder.addInterceptor(headerInterceptor)

        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun provideMovieApiService(client: OkHttpClient): CounterApi = Retrofit.Builder()
        .baseUrl(BuildConfig.API_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(ResultCallAdapterFactory())
        .build()
        .run { create(CounterApi::class.java) }

    @Provides
    fun remoteDataSourceProvider(counterApi: CounterApi): RemoteDataSource =
        CounterRemoteDataSource(counterApi)
}