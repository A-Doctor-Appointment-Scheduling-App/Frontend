package com.example.doccur.api

import android.util.Log
import com.example.doccur.util.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://192.168.244.132:8000/" // Use 10.0.2.2 for Android emulator to access localhost
//    private var tokenManager: TokenManager? = null
//
//    fun initialize(tokenManager: TokenManager) {
//        this.tokenManager = tokenManager
//    }
//
//    private val authInterceptor = Interceptor { chain ->
//        val originalRequest = chain.request()
//        val token = tokenManager?.getAccessToken()
//
//        val request = if (token != null) {
//            originalRequest.newBuilder()
//                .header("Authorization", "Bearer $token")
//                .build()
//
//        } else {
//
//            originalRequest
//        }
//
//        chain.proceed(request)
//    }

//    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(authInterceptor)
//        .connectTimeout(30, TimeUnit.SECONDS)
//        .readTimeout(30, TimeUnit.SECONDS)
//        .writeTimeout(30, TimeUnit.SECONDS)
//        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
//            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}