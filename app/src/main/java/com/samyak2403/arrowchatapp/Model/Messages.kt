/**
 * Created by Samyak Kamble on8/9/24, 9:32 PM Copyright (c) 2024 . All rights reserved.
 * Last modified 8/9/24, 9:32 PM
 */

package com.samyak2403.arrowchatapp.Model


// Data class to represent a message in the chat
data class Messages(
    // Property to store the message content
    var message: String = "",
    // Property to store the sender's ID
    var senderId: String = "",
    // Property to store the timestamp of the message
    var timestamp: Long = 0L,
    // Property to store the current time as a string
    var currenttime: String = ""
) {
    // Default constructor is not explicitly needed in Kotlin

    // Getters and setters are automatically provided by Kotlin
}
