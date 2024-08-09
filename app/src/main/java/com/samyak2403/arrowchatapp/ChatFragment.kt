package com.samyak2403.arrowchatapp


import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.samyak2403.arrowchatapp.Model.Firebasemodel
import com.squareup.picasso.Picasso

class ChatFragment : Fragment() {

    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mrecyclerview: RecyclerView
    private lateinit var mimageviewofuser: ImageView
    private lateinit var chatAdapter: FirestoreRecyclerAdapter<Firebasemodel, NoteViewHolder>
    private lateinit var linearLayoutManager: LinearLayoutManager

    @Nullable
    override fun onCreateView(
        @NonNull inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.chatfragment, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseFirestore = FirebaseFirestore.getInstance()
        mrecyclerview = v.findViewById(R.id.recyclerview)

        val query: Query = firebaseFirestore.collection("Users").whereNotEqualTo("uid", firebaseAuth.uid)
        val allUsername = FirestoreRecyclerOptions.Builder<Firebasemodel>()
            .setQuery(query, Firebasemodel::class.java)
            .build()

        chatAdapter = object : FirestoreRecyclerAdapter<Firebasemodel, NoteViewHolder>(allUsername) {
            override fun onBindViewHolder(
                @NonNull holder: NoteViewHolder,
                position: Int,
                @NonNull model: Firebasemodel
            ) {
                holder.particularUsername.text = model.name
                val uri = model.image

                Picasso.get().load(uri).into(holder.imageViewOfUser)
                if (model.status == "Online") {
                    holder.statusOfUser.text = model.status
                    holder.statusOfUser.setTextColor(Color.GREEN)
                } else {
                    holder.statusOfUser.text = model.status
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(activity, SpecificChat::class.java)
                    intent.putExtra("name", model.name)
                    intent.putExtra("receiveruid", model.uid)
                    intent.putExtra("imageuri", model.image)
                    startActivity(intent)
                }
            }

            @NonNull
            override fun onCreateViewHolder(
                @NonNull parent: ViewGroup,
                viewType: Int
            ): NoteViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.chatviewlayout, parent, false)
                return NoteViewHolder(view)
            }
        }

        mrecyclerview.setHasFixedSize(true)
        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = RecyclerView.VERTICAL
        mrecyclerview.layoutManager = linearLayoutManager
        mrecyclerview.adapter = chatAdapter

        return v
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val particularUsername: TextView = itemView.findViewById(R.id.nameofuser)
        val statusOfUser: TextView = itemView.findViewById(R.id.statusofuser)
        val imageViewOfUser: ImageView = itemView.findViewById(R.id.imageviewofuser)
    }

    override fun onStart() {
        super.onStart()
        chatAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatAdapter.stopListening()
    }
}
