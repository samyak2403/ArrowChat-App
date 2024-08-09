/*
 * Created by Samyak Kamble on8/9/24, 9:51 PM Copyright (c) 2024 . All rights reserved.
 * Last modified 8/9/24, 9:51 PM
 */

package com.samyak2403.arrowchatapp.Adapter



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.samyak2403.arrowchatapp.Model.Messages
import com.samyak2403.arrowchatapp.R

class MessagesAdapter(
    private val context: Context,
    private val messagesArrayList: ArrayList<Messages>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ITEM_SEND = 1
        private const val ITEM_RECIEVE = 2
    }

    @NonNull
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_SEND) {
            val view = LayoutInflater.from(context).inflate(R.layout.senderchatlayout, parent, false)
            SenderViewHolder(view)
        } else {
            val view = LayoutInflater.from(context).inflate(R.layout.recieverchatlayout, parent, false)
            RecieverViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val messages = messagesArrayList[position]
        if (holder is SenderViewHolder) {
            holder.textViewmessaage.text = messages.message
            holder.timeofmessage.text = messages.currenttime
        } else if (holder is RecieverViewHolder) {
            holder.textViewmessaage.text = messages.message
            holder.timeofmessage.text = messages.currenttime
        }
    }

    override fun getItemViewType(position: Int): Int {
        val messages = messagesArrayList[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid == messages.senderId) {
            ITEM_SEND
        } else {
            ITEM_RECIEVE
        }
    }

    override fun getItemCount(): Int {
        return messagesArrayList.size
    }

    inner class SenderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewmessaage: TextView = itemView.findViewById(R.id.sendermessage)
        val timeofmessage: TextView = itemView.findViewById(R.id.timeofmessage)
    }

    inner class RecieverViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewmessaage: TextView = itemView.findViewById(R.id.sendermessage)
        val timeofmessage: TextView = itemView.findViewById(R.id.timeofmessage)
    }
}
