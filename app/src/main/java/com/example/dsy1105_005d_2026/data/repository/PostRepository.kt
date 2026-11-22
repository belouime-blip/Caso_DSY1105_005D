package com.example.dsy1105_005d_2026.data.repository

import com.example.dsy1105_005d_2026.data.model.Post
import com.example.dsy1105_005d_2026.data.remote.ApiService
import com.example.dsy1105_005d_2026.data.remote.RetrofitInstance

class PostRepository(private val api: ApiService = RetrofitInstance.api) {
    suspend fun getPosts(): List<Post> = api.getPosts()
}
