package com.app.bustracking.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val baseUrl = ""


    fun createService(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okhttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun okhttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(interceptor())
            .build()
    }

    private fun interceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

}