package com.example.post_app.data

// app/src/main/java/com/example/post_app/data/model/Blog.kt
data class Blog(
    val id: Int,
    val title: String,
    val content: String,
    val photo: String?,
    val createdAt: String,
    val updatedAt: String
)

// For handling API responses
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
