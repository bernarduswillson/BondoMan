package com.example.transactionapp.domain

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.transactionapp.domain.api.TransactionAPI
import com.example.transactionapp.domain.api.logger.LoggingInterceptor
import com.example.transactionapp.domain.api.repo.TransactionAPIRepoImpl
import com.example.transactionapp.domain.db.TransactionDatabase
import com.example.transactionapp.domain.db.dao.TransactionDao
import com.example.transactionapp.domain.db.repo.TransactionDatabaseRepoImpl
import com.example.transactionapp.service.TokenService
import com.example.transactionapp.ui.viewmodel.auth.Auth
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Component
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    fun providesAlertDao(transactionDatabase: TransactionDatabase):TransactionDao = transactionDatabase.transactionDao()

    @Provides
    @Singleton
    fun providesAlertDatabase(@ApplicationContext context: Context):TransactionDatabase
            = Room
                .databaseBuilder(context,TransactionDatabase::class.java,"TransactionDatabase")
                .allowMainThreadQueries()
                .build()

    @Provides
    fun providesUserRepository(transactionDao: TransactionDao) : TransactionDatabaseRepoImpl
            = TransactionDatabaseRepoImpl(transactionDao)


    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit.Builder {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        Log.d("ApiModule", "Base URL: https://pbd-backend-2024.vercel.app/")
        return Retrofit.Builder()
            .baseUrl("https://pbd-backend-2024.vercel.app/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
    }

    @Provides
    fun provideApi(builder: Retrofit.Builder): TransactionAPI {
        return builder
            .build()
            .create(TransactionAPI::class.java)
    }

    @Provides
    fun provideOkHttp(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    @Provides
    fun provideHttpLoggingInterceptor(loggingInterceptor: LoggingInterceptor): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor(loggingInterceptor)
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return httpLoggingInterceptor

    }

    @Provides
    fun provideAuth(transactionAPIRepoImpl: TransactionAPIRepoImpl): Auth {
        return Auth(transactionAPIRepoImpl)
    }
}

@Component(modules = [ApiModule::class])
interface AppComponent {
    fun inject(service: TokenService)
}