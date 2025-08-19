package com.example.post_app.data.api
import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface PostApiService {
    @GET("blogs")
    suspend fun getAllPosts(): Response<List<PostResponse>>

    @GET("blogs/show/{id}")
    suspend fun getPostById(@Path("id") id: String): Response<PostResponse>

    @Multipart
    @POST("blogs/store")
    suspend fun createPost(
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part photo: MultipartBody.Part
    ): Response<PostResponse>

    @PUT("blogs/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: CreatePostRequest): Response<PostResponse>

    @DELETE("blogs/delete/{id}")
    suspend fun deletePost(@Path("id") id: String): Response<Unit>
}

// Response Models
data class PostResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("photo") val photo: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class CreatePostRequest(
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("photo") val photo: String? = null
)