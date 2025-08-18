package com.example.post_app.data.model


import com.example.post_app.data.Blog
import com.google.gson.annotations.SerializedName

data class BlogDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("photo")
    val photoUrl: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String
)
// Add this extension function to your project
fun BlogDto.toDomain(): Blog = Blog(
    id = this.id,
    title = this.title,
    content = this.content,
    photo = this.photoUrl,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)