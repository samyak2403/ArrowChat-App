/*
 * Created by Samyak Kamble on8/9/24, 9:47 PM Copyright (c) 2024 . All rights reserved.
 * Last modified 8/9/24, 9:47 PM
 */

package com.samyak2403.arrowchatapp



import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.annotation.NonNull
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.samyak2403.arrowchatapp.Model.UserProfile
import com.squareup.picasso.Picasso

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewUsername: EditText
    private lateinit var moveToUpdateProfile: TextView
    private lateinit var viewUserImageInImageView: ImageView
    private lateinit var toolbarOfViewProfile: Toolbar
    private lateinit var backButtonOfViewProfile: ImageButton

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseStorage: FirebaseStorage
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var storageReference: StorageReference

    private var imageUriAccessToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Initialize Firebase and UI elements
        viewUserImageInImageView = findViewById(R.id.viewuserimageinimageview)
        viewUsername = findViewById(R.id.viewusername)
        moveToUpdateProfile = findViewById(R.id.movetoupdateprofile)
        toolbarOfViewProfile = findViewById(R.id.toolbarofviewprofile)
        backButtonOfViewProfile = findViewById(R.id.backbuttonofviewprofile)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        storageReference = firebaseStorage.reference

        setSupportActionBar(toolbarOfViewProfile)

        // Handle back button click
        backButtonOfViewProfile.setOnClickListener {
            finish()
        }

        // Load user profile image
        storageReference.child("Images")
            .child(firebaseAuth.uid!!)
            .child("Profile Pic")
            .downloadUrl
            .addOnSuccessListener { uri ->
                imageUriAccessToken = uri.toString()
                Picasso.get().load(uri).into(viewUserImageInImageView)
            }

        // Load user profile data from Realtime Database
        val databaseReference: DatabaseReference = firebaseDatabase.getReference(firebaseAuth.uid!!)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(@NonNull snapshot: DataSnapshot) {
                val userProfile = snapshot.getValue(UserProfile::class.java)
                viewUsername.setText(userProfile?.username)
            }

            override fun onCancelled(@NonNull error: DatabaseError) {
                Toast.makeText(applicationContext, "Failed To Fetch", Toast.LENGTH_SHORT).show()
            }
        })

        // Move to update profile activity
        moveToUpdateProfile.setOnClickListener {
            val intent = Intent(this@ProfileActivity, UpdateProfile::class.java)
            intent.putExtra("nameofuser", viewUsername.text.toString())
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val documentReference: DocumentReference = firebaseFirestore.collection("Users").document(firebaseAuth.uid!!)
        documentReference.update("status", "Online")
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Now User is Online", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onStop() {
        super.onStop()
        val documentReference: DocumentReference = firebaseFirestore.collection("Users").document(firebaseAuth.uid!!)
        documentReference.update("status", "Offline")
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Now User is Offline", Toast.LENGTH_SHORT).show()
            }
    }
}
