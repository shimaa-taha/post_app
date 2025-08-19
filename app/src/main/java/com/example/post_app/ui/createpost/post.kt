package com.example.post_app.ui.createpost

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val photo: String?,
    val created_at: String,
    val updated_at: String
)
