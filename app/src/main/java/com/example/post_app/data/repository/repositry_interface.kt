package com.example.post_app.data.repository

import com.example.post_app.data.Blog
import com.example.post_app.data.api.ApiResult

interface Repository {
    suspend fun getBlogs(): ApiResult<List<Blog>>
    // Add other functions for create, update, delete
}