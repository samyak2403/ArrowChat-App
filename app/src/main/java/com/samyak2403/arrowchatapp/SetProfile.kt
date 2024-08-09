/*
 * Created by Samyak Kamble on8/9/24, 9:44 PM Copyright (c) 2024 . All rights reserved.
 * Last modified 8/9/24, 9:44 PM
 */
package com.samyak2403.arrowchatapp

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
import java.io.ByteArrayOutputStream
import java.io.IOException

class SetProfile : AppCompatActivity() {

    private lateinit var userImageCardView: CardView
    private lateinit var userImageView: ImageView
    private val PICK_IMAGE = 123
    private var imagePath: Uri? = null

    private lateinit var usernameEditText: EditText
    private lateinit var saveProfileButton: android.widget.Button
    private lateinit var progressBar: ProgressBar

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var storageReference: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore

    private var imageUriAccessToken: String? = null
    private var userName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage.reference
        firebaseFirestore = FirebaseFirestore.getInstance()

        // Initialize UI elements
        usernameEditText = findViewById(R.id.getusername)
        userImageCardView = findViewById(R.id.getuserimage)
        userImageView = findViewById(R.id.getuserimageinimageview)
        saveProfileButton = findViewById(R.id.saveProfile)
        progressBar = findViewById(R.id.progressbarofsetProfile)

        // Set up user image selection
        userImageCardView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE)
        }

        // Set up profile save button
        saveProfileButton.setOnClickListener {
            userName = usernameEditText.text.toString()
            if (userName.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Name is Empty", Toast.LENGTH_SHORT).show()
            } else if (imagePath == null) {
                Toast.makeText(applicationContext, "Image is Empty", Toast.LENGTH_SHORT).show()
            } else {
                progressBar.visibility = View.VISIBLE
                sendDataForNewUser()
                progressBar.visibility = View.INVISIBLE
                val intent = Intent(this, ChatActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun sendDataForNewUser() {
        sendDataToRealTimeDatabase()
    }

    private fun sendDataToRealTimeDatabase() {
        val name = usernameEditText.text.toString().trim()
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child(firebaseAuth.uid!!)
        val userProfile = UserProfile(name, firebaseAuth.uid!!)
        databaseReference.setValue(userProfile)
        Toast.makeText(applicationContext, "User Profile Added Successfully", Toast.LENGTH_SHORT).show()
        sendImageToStorage()
    }

    private fun sendImageToStorage() {
        val imageRef: StorageReference = storageReference.child("Images").child(firebaseAuth.uid!!).child("Profile Pic")

        // Compressing image
        val bitmap: Bitmap? = try {
            MediaStore.Images.Media.getBitmap(contentResolver, imagePath)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }

        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        // Uploading image to storage
        val uploadTask: UploadTask = imageRef.putBytes(data)

        uploadTask.addOnSuccessListener(OnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener(OnSuccessListener { uri ->
                imageUriAccessToken = uri.toString()
                Toast.makeText(applicationContext, "URI get success", Toast.LENGTH_SHORT).show()
                sendDataToCloudFirestore()
            }).addOnFailureListener(OnFailureListener { e ->
                Toast.makeText(applicationContext, "URI get Failed", Toast.LENGTH_SHORT).show()
            })
            Toast.makeText(applicationContext, "Image is uploaded", Toast.LENGTH_SHORT).show()
        }).addOnFailureListener(OnFailureListener { e ->
            Toast.makeText(applicationContext, "Image Not Uploaded", Toast.LENGTH_SHORT).show()
        })
    }

    private fun sendDataToCloudFirestore() {
        val documentReference: DocumentReference = firebaseFirestore.collection("Users").document(firebaseAuth.uid!!)
        val userData = hashMapOf(
            "name" to userName,
            "image" to imageUriAccessToken,
            "uid" to firebaseAuth.uid,
            "status" to "Online"
        )

        documentReference.set(userData).addOnSuccessListener {
            Toast.makeText(applicationContext, "Data on Cloud Firestore sent successfully", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            imagePath = data?.data
            userImageView.setImageURI(imagePath)
        }
    }
}

