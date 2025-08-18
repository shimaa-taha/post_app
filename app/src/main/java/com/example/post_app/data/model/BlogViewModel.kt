package com.example.post_app.data.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.post_app.data.Blog
import com.example.post_app.data.api.ApiResult
import com.example.post_app.data.api.repository.RepositoryImpl
import com.example.post_app.data.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BlogViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    private val _postsState = MutableStateFlow<ApiResult<List<Blog>>>(ApiResult.Loading)
    val postsState: StateFlow<ApiResult<List<Blog>>> = _postsState.asStateFlow()
    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _postsState.value = ApiResult.Loading
            _postsState.value = repository.getBlogs()
        }
    }

}