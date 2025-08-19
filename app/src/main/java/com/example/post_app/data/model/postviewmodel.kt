package com.example.post_app.data.model
import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.post_app.data.api.ApiResult
import com.example.post_app.data.api.repository.PostRepository
import com.example.post_app.ui.createpost.model.Post
import kotlinx.coroutines.launch
class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PostRepository(application.applicationContext)

    private val _posts = MutableLiveData<List<Post>>()
    val posts: LiveData<List<Post>> = _posts

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun clearError() {
        _error.value = null
    }
    private val _navigateToDetail = MutableLiveData<Int?>()
    val navigateToDetail: LiveData<Int?> = _navigateToDetail


    fun loadPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = repository.getAllPosts()) {
                is ApiResult.Success<*> -> {
                    when (val data = result.data) {
                        is List<*> -> {
                            if (data.all { it is Post }) {
                                @Suppress("UNCHECKED_CAST")
                                _posts.value = data as List<Post>
                            } else {
                                _error.value = "Invalid post data in list"
                            }
                        }
                        else -> _error.value = "Unexpected data type received"
                    }
                }
                is ApiResult.Error -> _error.value = result.exception.message
            }
            _isLoading.value = false
        }
    }
    fun createPost(title: String, content: String, imageUri: Uri, fileName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                when (val result = repository.createPost(title, content, imageUri, fileName)) {
                    is ApiResult.Success -> {
                        loadPosts()
                        _error.value = "Post created successfully!"
                    }
                    is ApiResult.Error -> {
                        // Show detailed error message
                        _error.value = result.exception.message
                    }
                }
            } catch (e: Exception) {
                _error.value = "Failed to process request: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun deletePost(postId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val result = repository.deletePost(postId.toString())) {
                is ApiResult.Success<*> -> loadPosts()
                is ApiResult.Error -> _error.value = result.exception.message
            }
            _isLoading.value = false
        }
    }

    fun onPostClicked(postId: Int) {
        _navigateToDetail.value = postId
    }

    fun onNavigationComplete() {
        _navigateToDetail.value = null
    }


}