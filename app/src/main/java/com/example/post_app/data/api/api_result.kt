package com.example.post_app.data.api


sealed class ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Exception) : ApiResult<Nothing>()

    // Helper function to get data if it's the correct type
    inline fun <reified R> getDataOrNull(): R? {
        return if (this is Success && data is R) {
            data as R
        } else {
            null
        }
    }
}

