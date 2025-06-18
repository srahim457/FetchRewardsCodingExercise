package com.example.fetchrewards_codingexercise

import retrofit2.http.GET
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//to make the app more extensible I'd separate the ApiService from the GET request
//however for simplicity as there is only 1 request being made I've combined them
interface ApiService {
    @GET("hiring.json")
    //I made the Entry class to have the parameters of each JSON entry
    suspend fun getItems(): Response<List<Entry>>

    companion object {
        private const val URL = "https://hiring.fetch.com/"
        val instance: ApiService by lazy{
            Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}