package com.example.post_app.ui.postdetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.post_app.databinding.ActivityPostDetailBinding

class PostDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostDetailBinding
    private var postId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get post data from intent
        postId = intent.getStringExtra("POST_ID") ?: ""
        val title = intent.getStringExtra("POST_TITLE") ?: ""
        val content = intent.getStringExtra("POST_CONTENT") ?: ""
        val imageUrl = intent.getStringExtra("POST_IMAGE") ?: ""

        // Display post data
        binding.tvTitle.text = title
        binding.tvMessage.text = content

        Glide.with(this)
            .load(imageUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(binding.ivPostImage)

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnEdit.setOnClickListener {
            // Open edit activity/dialog
            showEditDialog()
        }

        binding.btnDelete.setOnClickListener {
            // Show delete confirmation
            showDeleteConfirmation()
        }
    }

    private fun showEditDialog() {
        // Implement edit functionality
        // You can start a new activity or show a dialog
    }

    private fun showDeleteConfirmation() {
        android.app.AlertDialog.Builder(this)
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("Delete") { _, _ ->
                // Call viewModel.deletePost(postId)
                finish() // Go back to main activity
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}