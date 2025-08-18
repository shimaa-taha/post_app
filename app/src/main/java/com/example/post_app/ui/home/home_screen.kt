package com.example.post_app.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.post_app.R
import com.example.post_app.data.Blog
import com.example.post_app.data.api.ApiResult
import com.example.post_app.data.model.BlogViewModel

@Composable
fun HomeScreen(
    postsState: ApiResult<List<Blog>>,  // Changed parameter type
    onPostClick: (Blog) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier,  // Add this parameter

    viewModel: BlogViewModel = hiltViewModel()
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateClick) {
                Icon(Icons.Default.Add, contentDescription = "Create Post")
            }
        }
    ) { padding ->
        when (postsState) {  // Now matching on ApiResult directly
            is ApiResult.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is ApiResult.Success -> {
                val posts = postsState.data
                LazyColumn(modifier = Modifier.padding(padding)) {
                    itemsIndexed(posts) { _, post ->
                        PostItem(post = post, onClick = { onPostClick(post) })
                    }
                }
            }

            is ApiResult.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${postsState.exception.message}")
                }
            }
        }
    }
}

@Composable
fun PostItem(post: Blog, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation =  CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.photo,
                contentDescription = "Post image",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(android.R.drawable.ic_menu_gallery)            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(post.title, fontWeight = FontWeight.Bold)
                Text(post.content, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    post.createdAt,
                    style = MaterialTheme.typography.labelSmall // M3 equivalent
                )            }
        }
    }
}