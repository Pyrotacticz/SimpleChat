package com.jatruong.simplechat

import android.app.Application
import com.parse.Parse
import com.parse.ParseObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor


class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        ParseObject.registerSubclass(Message::class.java)

        // Use for monitoring Parse network traffic
        val builder = OkHttpClient.Builder()
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        // any network interceptors must be added with the Configuration Builder given this syntax
        builder.networkInterceptors().add(httpLoggingInterceptor)

        // Set applicationId and server based on the values in the Back4App settings.
        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId("IT9CFEu9H3rj1FHLMckJVUfExqCKZ3k4CWzpCj6L")
                .clientKey("tWEHG16AVN7KpuePR9NCjf7lLTzItE7DUmFeaLvB")
                .clientBuilder(builder)
                .server("https://parseapi.back4app.com").build()
        )
    }
}