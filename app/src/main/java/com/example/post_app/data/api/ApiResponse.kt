package com.example.post_app.data.api

import com.example.post_app.ui.createpost.Post

private fun PostResponse.toPost(): Post {
    return Post(
        id = id.toString(), // Make sure this matches your API response
        title = title ?: "",
        content = content ?: "", // or message if that's the field name
        photo = photo, // or photo if that's the field name
        created_at = createdAt ?: "",
        updated_at = updatedAt ?: ""
    )
}