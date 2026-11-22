package com.example.dsy1105_005d_2026.data.remote

import com.example.dsy1105_005d_2026.data.model.Post
import retrofit2.http.GET

/** Define los endpoints de la API externa. */
interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>
}
