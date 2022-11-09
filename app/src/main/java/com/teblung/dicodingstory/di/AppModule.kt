package com.teblung.dicodingstory.di

import android.content.Context
import androidx.room.Room
import com.teblung.dicodingstory.BuildConfig
import com.teblung.dicodingstory.MyApplication.Companion.BASE_URL
import com.teblung.dicodingstory.data.source.local.room.Database
import com.teblung.dicodingstory.data.source.remote.service.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "dicoding_story_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideStoryDao(database: Database) = database.storyDao()

    @Provides
    fun provideRetrofitConfig(): Retrofit {
        val loggingInterceptor = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        val client = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .build()
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun getApiService(retrofit: Retrofit): ApiService {
        val api: ApiService by lazy { retrofit.create(ApiService::class.java) }
        return api
    }
}