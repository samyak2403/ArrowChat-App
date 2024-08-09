/*
 * Created by Samyak Kamble on8/9/24, 9:39 PM Copyright (c) 2024 . All rights reserved.
 * Last modified 8/9/24, 9:39 PM
 */

package com.samyak2403.arrowchatapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.samyak2403.arrowchatapp.Model.UserProfile
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.io.IOException

class UpdateProfile : AppCompatActivity() {

    private lateinit var newUsernameEditText: EditText
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var profileImageView: ImageView
    private lateinit var storageReference: StorageReference
    private var imageUriAccessToken: String = ""
    private lateinit var toolbar: Toolbar
    private lateinit var backButton: ImageButton
    private lateinit var progressBar: ProgressBar
    private var imagePath: Uri? = null
    private lateinit var updateProfileButton: android.widget.Button
    private var newName: String = ""

    private val PICK_IMAGE = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)

        // Initialize UI elements
        toolbar = findViewById(R.id.toolbarofupdateprofile)
        backButton = findViewById(R.id.backbuttonofupdateprofile)
        profileImageView = findViewById(R.id.getnewuserimageinimageview)
        progressBar = findViewById(R.id.progressbarofupdateprofile)
        newUsernameEditText = findViewById(R.id.getnewusername)
        updateProfileButton = findViewById(R.id.updateprofilebutton)

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        // Set up toolbar
        setSupportActionBar(toolbar)

        // Back button click listener
        backButton.setOnClickListener {
            finish()
        }

        // Set initial username from intent extras
        val intent = intent
        newUsernameEditText.setText(intent.getStringExtra("nameofuser"))

        // Load existing profile image from Firebase Storage
        loadProfileImage()

        // Update profile button click listener
        updateProfileButton.setOnClickListener {
            newName = newUsernameEditText.text.toString()
            if (newName.isEmpty()) {
                Toast.makeText(applicationContext, "Name is Empty", Toast.LENGTH_SHORT).show()
            } else {
                if (imagePath != null) {
                    progressBar.visibility = View.VISIBLE
                    updateProfileInDatabase()
                    updateImageToStorage()
                } else {
                    progressBar.visibility = View.VISIBLE
                    updateProfileInDatabase()
                    updateNameOnCloudFirestore()
                }
            }
        }

        // Image view click listener to pick a new image
        profileImageView.setOnClickListener {
            val pickImageIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(pickImageIntent, PICK_IMAGE)
        }
    }

    private fun loadProfileImage() {
        storageReference.child("Images").child(firebaseAuth.uid!!).child("Profile Pic")
            .downloadUrl.addOnSuccessListener { uri ->
                imageUriAccessToken = uri.toString()
                Picasso.get().load(uri).into(profileImageView)
            }
    }

    private fun updateProfileInDatabase() {
        val databaseReference = firebaseDatabase.reference.child(firebaseAuth.uid!!)
        val userProfile = UserProfile(newName, firebaseAuth.uid!!)
        databaseReference.setValue(userProfile).addOnSuccessListener {
            Toast.makeText(applicationContext, "Profile Updated", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.INVISIBLE
            val chatIntent = Intent(this, ChatActivity::class.java)
            startActivity(chatIntent)
            finish()
        }
    }

    private fun updateNameOnCloudFirestore() {
        val documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.uid!!)
        val userData = hashMapOf(
            "name" to newName,
            "image" to imageUriAccessToken,
            "uid" to firebaseAuth.uid,
            "status" to "Online"
        )
        documentReference.set(userData).addOnSuccessListener {
            Toast.makeText(applicationContext, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateImageToStorage() {
        val imageRef = storageReference.child("Images").child(firebaseAuth.uid!!).child("Profile Pic")
        val bitmap: Bitmap? = try {
            MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        val uploadTask = imageRef.putBytes(data)
        uploadTask.addOnSuccessListener {
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                imageUriAccessToken = uri.toString()
                Toast.makeText(applicationContext, "Image Updated", Toast.LENGTH_SHORT).show()
                updateNameOnCloudFirestore()
            }.addOnFailureListener {
                Toast.makeText(applicationContext, "Failed to Get URI", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Image Not Updated", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imagePath = data?.data
            profileImageView.setImageURI(imagePath)
        }
    }

    override fun onStop() {
        super.onStop()
        updateUserStatus("Offline")
    }

    override fun onStart() {
        super.onStart()
        updateUserStatus("Online")
    }

    private fun updateUserStatus(status: String) {
        val documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.uid!!)
        documentReference.update("status", status).addOnSuccessListener {
            Toast.makeText(applicationContext, "User is $status", Toast.LENGTH_SHORT).show()
        }
    }
}
