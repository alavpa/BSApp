package com.alavpa.bsproducts.data.di

import androidx.preference.PreferenceManager
import com.alavpa.bsproducts.data.BuildConfig
import com.alavpa.bsproducts.data.ProductDataRepository
import com.alavpa.bsproducts.data.api.Api
import com.alavpa.bsproducts.data.api.ApiDataSource
import com.alavpa.bsproducts.data.api.RemoteDataSource
import com.alavpa.bsproducts.data.local.LocalDataSource
import com.alavpa.bsproducts.data.local.PreferencesDataSource
import com.alavpa.bsproducts.domain.repository.ProductRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single { httpLoggingInterceptor() }
    single { httpClient(get()) }
    single { GsonBuilder().setDateFormat("yyyy-MM-dd").create() }
    single { api(get(), get()) }
    single { ApiDataSource(get(), get()) } bind RemoteDataSource::class
    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single { PreferencesDataSource(get(), get()) } bind LocalDataSource::class
    single { ProductDataRepository(get(), get()) } bind ProductRepository::class
}

fun httpLoggingInterceptor(): HttpLoggingInterceptor = if (BuildConfig.DEBUG)
    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
else HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)

fun httpClient(interceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(interceptor)
    .build()

fun api(client: OkHttpClient, gson: Gson): Api = Retrofit.Builder()
    .baseUrl(Api.BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create(gson))
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()
    .create(Api::class.java)
