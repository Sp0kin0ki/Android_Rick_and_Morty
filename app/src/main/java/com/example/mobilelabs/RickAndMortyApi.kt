package com.example.mobilelabs

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class ApiCharacterResponse(
    val info: PageInfo?,
    val results: List<ApiCharacter>?
)

data class PageInfo(
    val count: Int = 0,
    val pages: Int = 0,
    val next: String? = null,
    val prev: String? = null
)

data class ApiCharacter(
    val id: Int,
    val name: String,
    val species: String?,
    val status: String?,
    val origin: ApiNamedLocation?,
    val location: ApiNamedLocation?,
    val image: String?
)

data class ApiNamedLocation(
    val name: String?
)

interface RickAndMortyService {
    @GET("api/character")
    suspend fun getCharacters(@Query("page") page: Int): ApiCharacterResponse
}

object RickAndMortyApi {
    val service: RickAndMortyService by lazy {
        Retrofit.Builder()
            .baseUrl("https://rickandmortyapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RickAndMortyService::class.java)
    }
}
