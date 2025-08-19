package com.example.post_app.data.api.repository
import android.content.Context
import android.net.Uri
import com.example.post_app.data.api.ApiResult
import com.example.post_app.data.api.PostResponse
import com.example.post_app.data.api.RetrofitInstance
import com.example.post_app.ui.createpost.model.Post
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception
import com.google.gson.JsonSyntaxException
import java.io.File

class  PostRepository(private val context: Context) {
    private val apiService = RetrofitInstance.apiService
    suspend fun getAllPosts(): ApiResult<List<Post>> {
        return try {
            val response = apiService.getAllPosts()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    ApiResult.Success(body.map { it.toPost() })
                } else {
                    ApiResult.Error(Exception("Empty response body"))
                }
            } else {
                ApiResult.Error(Exception("HTTP error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: JsonSyntaxException) {
            ApiResult.Error(Exception("Invalid JSON response: ${e.message}"))
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }
    suspend fun getPostById(id: String): ApiResult<Post> {
        return try {
            val response = apiService.getPostById(id)
            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!.toPost())
            } else {
                ApiResult.Error(Exception("Post not found: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    suspend fun createPost(title: String, content: String, imageUri: Uri, fileName: String): ApiResult<Post> {
        return try {
            val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val contentBody = content.toRequestBody("text/plain".toMediaTypeOrNull())

            val inputStream = context.contentResolver.openInputStream(imageUri)
            val file = File.createTempFile("upload", ".jpg", context.cacheDir)

            inputStream?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData(
                "photo",
                fileName,
                requestFile
            )

            val response = apiService.createPost(titleBody, contentBody, imagePart)

            if (response.isSuccessful) {
                ApiResult.Success(response.body()!!.toPost())
            } else {
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                ApiResult.Error(Exception("Failed to create post: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }    suspend fun deletePost(id: String): ApiResult<Unit> {
        return try {
            val response = apiService.deletePost(id)
            if (response.isSuccessful) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error(Exception("Failed to delete post: ${response.code()}"))
            }
        } catch (e: Exception) {
            ApiResult.Error(e)
        }
    }

    private fun PostResponse.toPost(): Post {
        return Post(
            id = id.toString(),
            title = title,
            content = content,
            created_at = createdAt,
            updated_at = updatedAt,
            photo = photo
        )
    }
}