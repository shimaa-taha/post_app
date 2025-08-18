package com.example.post_app.data.api.repository

import com.example.post_app.data.Blog
import com.example.post_app.data.api.ApiResult
import com.example.post_app.data.api.ApiService
import com.example.post_app.data.model.BlogDto
import com.example.post_app.data.repository.Repository
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

interface Repository {
    suspend fun getBlogs(): ApiResult<List<Blog>>
    // Add other repository methods
}

// RepositoryImpl.kt
class RepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : Repository {
    override suspend fun getBlogs(): ApiResult<List<Blog>> {
        return try {
            val response = apiService.getBlogs()
            if (response.isSuccessful) {
                response.body()?.let { blogDtos ->
                    // Explicitly map each DTO to domain model
                    ApiResult.Success(blogDtos.map { dto ->
                        Blog(
                            id = dto.id,
                            title = dto.title,
                            content = dto.content,
                            photo = dto.photoUrl,
                            createdAt = dto.createdAt,
                            updatedAt = dto.updatedAt
                        )
                    })
                } ?: ApiResult.Error(Exception("Empty response"))
            } else {
                ApiResult.Error(Exception("API error: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
}