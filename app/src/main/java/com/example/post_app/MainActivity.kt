package com.example.post_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.post_app.data.model.BlogViewModel
import com.example.post_app.ui.home.HomeScreen
import com.example.post_app.ui.theme.Post_appTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@AndroidEntryPoint  // Add Hilt annotation
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Post_appTheme {
                // Your content
            }
        }
    }
}

@Composable
fun AppContent(
    viewModel: BlogViewModel = hiltViewModel()
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        // Collect the posts state from ViewModel
        val postsState by viewModel.postsState.collectAsState()

        HomeScreen(
            postsState = postsState,
            onPostClick = { blog ->
                // Handle post click
            },
            onCreateClick = {
                // Handle create new post
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}