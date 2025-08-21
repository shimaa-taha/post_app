package com.example.post_app
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.Manifest
import android.os.Build
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.post_app.data.model.PostViewModel
import com.example.post_app.databinding.ActivityMainBinding
import com.example.post_app.ui.home.PostAdapter
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels()
    private lateinit var adapter: PostAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        setupObservers()
        viewModel.loadPosts()
    }

    private fun setupRecyclerView() {
        adapter = PostAdapter { post ->

            startActivity(intent)
        }

        binding.postsRecyclerView.adapter = adapter
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(this)
    }
    private fun setupListeners() {
        binding.fabAddPost.setOnClickListener {
            showAddPostDialog()
        }
    }

private var selectedImageUri: Uri? = null
    private var selectedFileName: String? = null

    private var currentDialogView: View? = null
    @SuppressLint("ResourceType")
    private fun showAddPostDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_post, null)
        currentDialogView = dialogView

        val tvSelectedImageName = dialogView.findViewById<TextView>(R.id.tvSelectedImageName)
        val selectedImageView = dialogView.findViewById<ImageView>(R.id.ivSelectedImage)
        val defaultCameraLayout = dialogView.findViewById<LinearLayout>(R.id.defaultCameraLayout)
        val createBtn = dialogView.findViewById<MaterialButton>(R.id.create_btn)
        val cancelBtn = dialogView.findViewById<MaterialButton>(R.id.cancel_btn)

        selectedImageView.visibility = View.GONE
        defaultCameraLayout.visibility = View.VISIBLE
        selectedImageUri = null
        selectedFileName = null
        tvSelectedImageName.text = "No image selected"

        defaultCameraLayout.setOnClickListener {
            openGallery()
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        createBtn.setOnClickListener {
            val title = dialogView.findViewById<EditText>(R.id.etTitle).text.toString()
            val content = dialogView.findViewById<EditText>(R.id.etContent).text.toString()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                if (selectedImageUri != null && selectedFileName != null) {
                    viewModel.createPost(title, content, selectedImageUri!!, selectedFileName!!)

                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Title and content are required*", Toast.LENGTH_SHORT).show()
            }
        }

        cancelBtn.setOnClickListener {

            dialog.dismiss()
        }

        dialog.show()
    }
    private fun openGallery() {
        Log.d("openGallery", "shimaaaa")

        val permissionsNeeded = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10-12 - READ_EXTERNAL_STORAGE is still needed but limited
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            // Android 9 and below
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsNeeded.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        } else {
            Log.d("openGallery", "shimaaaa22")
            launchGallery()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        currentDialogView = null
    }
    private fun launchGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                selectedImageUri = uri
                selectedFileName = getFileNameFromUri(uri)

                currentDialogView?.let { dialogView ->
                    val tvSelectedImageName = dialogView.findViewById<TextView>(R.id.tvSelectedImageName)
                    val selectedImageView = dialogView.findViewById<ImageView>(R.id.ivSelectedImage)
                    val defaultCameraLayout = dialogView.findViewById<LinearLayout>(R.id.defaultCameraLayout)

                    tvSelectedImageName.text = "Selected: $selectedFileName"

                    if (selectedImageView != null && defaultCameraLayout != null) {

                        Glide.with(this)
                            .load(uri)
                            .centerCrop()
                            .into(selectedImageView)

                        defaultCameraLayout.visibility = View.GONE
                        selectedImageView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
    private fun getFileNameFromUri(uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> {
                contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (cursor.moveToFirst() && nameIndex != -1) {
                        cursor.getString(nameIndex)
                    } else {
                        "blogs/${System.currentTimeMillis()}.jpg"
                    }
                }
            }
            "file" -> uri.lastPathSegment
            else -> "blogs/${System.currentTimeMillis()}.jpg"
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchGallery()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1001
        private const val PERMISSION_REQUEST_CODE = 1002
    }
    private fun setupObservers() {
        viewModel.posts.observe(this) { posts ->
            adapter.submitList(posts)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
        viewModel.error.observe(this) { error ->
            error?.let {
                println("DEBUG: Error response - $it")
                if (it.contains("successfully", ignoreCase = true)) {
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: $it", Toast.LENGTH_LONG).show()
                }
                viewModel.clearError()
            }
        }
    }
}