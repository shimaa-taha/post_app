package com.example.post_app.ui.postdetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.post_app.data.api.ApiResult
import com.example.post_app.data.api.repository.PostRepository
import com.example.post_app.ui.createpost.Post
import kotlinx.coroutines.launch

class PostDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepository(application.applicationContext)

    private val _post = MutableLiveData<Post?>()
    val post: LiveData<Post?> = _post

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isDeleted = MutableLiveData<Boolean>()
    val isDeleted: LiveData<Boolean> = _isDeleted

    fun loadPost(postId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = repository.getPostById(postId)) {
                is ApiResult.Success -> {
                    val post = result.data as? Post
                    if (post != null) {
                        _post.value = post
                    } else {
                        _error.value = "Invalid post data format"
                    }
                }
                is ApiResult.Error -> _error.value = result.exception.message
            }
            _isLoading.value = false
        }
    }
//    fun updatePost(postId: Int, title: String, message: String, imageUrl: String?) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _error.value = null
//            when (val result = repository.updatePost(postId, title, message, imageUrl)) {
//                is Result.Success -> _post.value = result.data
//                is Result.Error -> _error.value = result.exception.message
//            }
//            _isLoading.value = false
//        }
//    }
//
//    fun deletePost(postId: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _error.value = null
//            when (val result = repository.deletePost(postId)) {
//                is Result.Success -> _isDeleted.value = true
//                is Result.Error -> _error.value = result.exception.message
//            }
//            _isLoading.value = false
//        }
//    }
}