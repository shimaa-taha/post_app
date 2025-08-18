package com.example.post_app.data.api

import com.example.post_app.data.model.BlogDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*


interface ApiService {
    @GET("blogs")
    suspend fun getBlogs(): Response<List<BlogDto>>

    @Multipart
    @POST("blogs/store")
    suspend fun createBlog(
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part photo: MultipartBody.Part?
    ): Response<BlogDto>

    @Multipart
    @POST("blogs/update/{id}")
    suspend fun updateBlog(
        @Path("id") id: Int,
        @Part("title") title: RequestBody?,
        @Part("content") content: RequestBody?,
        @Part photo: MultipartBody.Part?
    ): Response<BlogDto>

    @POST("blogs/delete/{id}")
    suspend fun deleteBlog(@Path("id") id: Int): Response<Unit>
}