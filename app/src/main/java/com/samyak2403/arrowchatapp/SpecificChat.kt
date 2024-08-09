/*
 * Created by Samyak Kamble on8/9/24, 9:41 PM Copyright (c) 2024 . All rights reserved.
 * Last modified 8/9/24, 9:40 PM
 */

package com.samyak2403.arrowchatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity




import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.samyak2403.arrowchatapp.Adapter.MessagesAdapter
import com.samyak2403.arrowchatapp.Model.Messages
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class SpecificChat : AppCompatActivity() {

    private lateinit var messageEditText: EditText
    private lateinit var sendMessageButton: ImageButton
    private lateinit var sendMessageCardView: CardView
    private lateinit var chatToolbar: androidx.appcompat.widget.Toolbar
    private lateinit var userImageView: ImageView
    private lateinit var userNameTextView: TextView
    private lateinit var backButton: ImageButton
    private lateinit var messagesRecyclerView: RecyclerView

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var messagesAdapter: MessagesAdapter
    private lateinit var messagesArrayList: ArrayList<Messages>

    private lateinit var currentTime: String
    private lateinit var calendar: Calendar
    private lateinit var simpleDateFormat: SimpleDateFormat

    private var enteredMessage: String? = null
    private var receiverName: String? = null
    private var receiverUid: String? = null
    private var senderUid: String? = null
    private var senderRoom: String? = null
    private var receiverRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_specificchat)

        // Initialize UI elements
        messageEditText = findViewById(R.id.getmessage)
        sendMessageCardView = findViewById(R.id.carviewofsendmessage)
        sendMessageButton = findViewById(R.id.imageviewsendmessage)
        chatToolbar = findViewById(R.id.toolbarofspecificchat)
        userNameTextView = findViewById(R.id.Nameofspecificuser)
        userImageView = findViewById(R.id.specificuserimageinimageview)
        backButton = findViewById(R.id.backbuttonofspecificchat)
        messagesRecyclerView = findViewById(R.id.recyclerviewofspecific)

        // Initialize Firebase instances
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance()
        calendar = Calendar.getInstance()
        simpleDateFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Initialize message list and adapter
        messagesArrayList = ArrayList()
        messagesAdapter = MessagesAdapter(this, messagesArrayList)
        messagesRecyclerView.layoutManager = LinearLayoutManager(this).apply { stackFromEnd = true }
        messagesRecyclerView.adapter = messagesAdapter

        // Get data from Intent
        val intent = intent
        receiverUid = intent.getStringExtra("receiveruid")
        receiverName = intent.getStringExtra("name")
        val imageUri = intent.getStringExtra("imageuri")

        senderUid = firebaseAuth.uid
        senderRoom = senderUid + receiverUid
        receiverRoom = receiverUid + senderUid

        // Set up toolbar
        setSupportActionBar(chatToolbar)
        chatToolbar.setOnClickListener {
            Toast.makeText(applicationContext, "Toolbar is Clicked", Toast.LENGTH_SHORT).show()
        }

        // Set up back button
        backButton.setOnClickListener {
            finish()
        }

        // Set user name and image
        userNameTextView.text = receiverName
        if (!imageUri.isNullOrEmpty()) {
            Picasso.get().load(imageUri).into(userImageView)
        } else {
            Toast.makeText(applicationContext, "null is received", Toast.LENGTH_SHORT).show()
        }

        // Set up messages listener
        val databaseReference = firebaseDatabase.reference.child("chats").child(senderRoom!!).child("messages")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagesArrayList.clear()
                for (snapshot1 in snapshot.children) {
                    val message = snapshot1.getValue(Messages::class.java)
                    messagesArrayList.add(message!!)
                }
                messagesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })

        // Set up send message button
        sendMessageButton.setOnClickListener {
            enteredMessage = messageEditText.text.toString()
            if (enteredMessage.isNullOrEmpty()) {
                Toast.makeText(applicationContext, "Enter message first", Toast.LENGTH_SHORT).show()
            } else {
                val date = Date()
                currentTime = simpleDateFormat.format(calendar.time)
                val message = Messages(enteredMessage!!, senderUid!!, date.time, currentTime)
                firebaseDatabase.reference.child("chats")
                    .child(senderRoom!!)
                    .child("messages")
                    .push().setValue(message).addOnCompleteListener {
                        firebaseDatabase.reference.child("chats")
                            .child(receiverRoom!!)
                            .child("messages")
                            .push()
                            .setValue(message)
                    }
                messageEditText.setText(null)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        messagesAdapter.notifyDataSetChanged()
    }

    override fun onStop() {
        super.onStop()
        messagesAdapter.notifyDataSetChanged()
    }
}
