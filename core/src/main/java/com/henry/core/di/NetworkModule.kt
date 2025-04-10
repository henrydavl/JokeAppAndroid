package com.henry.core.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.henry.core.BuildConfig
import com.henry.core.BuildConfig.API_CONNECT_TIMEOUT
import com.henry.core.BuildConfig.API_READ_TIMEOUT
import com.henry.core.BuildConfig.API_WRITE_TIMEOUT
import com.henry.core.BuildConfig.BASE_URL
import com.henry.core.BuildConfig.ENABLE_API_LOG
import com.henry.core.utils.NetworkConstants.ACCEPT
import com.henry.core.utils.NetworkConstants.CONTENT_TYPE_1
import com.henry.core.utils.NetworkConstants.JSON_TYPE
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()


    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor() = HttpLoggingInterceptor()

    @Provides
    @Singleton
    fun provideJokeApi(
        moshi: Moshi,
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        val chuckerCollector = ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_HOUR
        )

        return ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector)
            .maxContentLength(250_000L)
            .alwaysReadResponseBody(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .applyTimeouts()
            .addDefaultHeaders()
            .applyLoggingInterceptors(httpLoggingInterceptor, chuckerInterceptor)
            .build()
    }

    private fun OkHttpClient.Builder.applyTimeouts() = apply {
        connectTimeout(API_CONNECT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
        writeTimeout(API_WRITE_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
        readTimeout(API_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
    }

    private fun OkHttpClient.Builder.addDefaultHeaders() = apply {
        addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder().applyDefaultHeaders().build()
            )
        }
    }

    private fun Request.Builder.applyDefaultHeaders() = apply {
        addHeader(ACCEPT, JSON_TYPE)
        header(ACCEPT, CONTENT_TYPE_1)
    }

    private fun OkHttpClient.Builder.applyLoggingInterceptors(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        chuckerInterceptor: ChuckerInterceptor,
    ) = apply {
        if (ENABLE_API_LOG && BuildConfig.DEBUG) {
            addInterceptor(httpLoggingInterceptor)
            addInterceptor(chuckerInterceptor)
        }
    }
}