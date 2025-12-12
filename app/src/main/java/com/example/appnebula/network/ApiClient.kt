package com.example.appnebula.network

import android.content.Context
import com.example.appnebula.data.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 es una IP especial que apunta al localhost de la máquina anfitriona (tu PC).
    private const val BASE_URL = "http://10.0.2.2:9090/"

    private var retrofit: Retrofit? = null

    fun getInstance(context: Context): ApiService {
        if (retrofit == null) {
            val sessionManager = SessionManager(context.applicationContext)
            val authInterceptor = Interceptor { chain ->
                val token = sessionManager.getToken()
                val requestBuilder = chain.request().newBuilder()

                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }

                chain.proceed(requestBuilder.build())
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!.create(ApiService::class.java)
    }
}
