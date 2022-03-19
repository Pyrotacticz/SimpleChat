package com.jatruong.simplechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.parse.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser()
        } else { // If not logged in, login as a new anonymous user
            login()
        }
    }

    // Get the userId from the cached currentUser object
    fun startWithCurrentUser() {
        setupMessagePosting()
    }

    val USER_ID_KEY = "userId"
    val BODY_KEY = "body"
    var etMessage: EditText? = null
    var ibSend: ImageButton? = null
    fun setupMessagePosting() {
        etMessage = findViewById<EditText>(R.id.etMessage)
        ibSend = findViewById<ImageButton>(R.id.ibSend)

        ibSend?.setOnClickListener(View.OnClickListener {
            val data: String = etMessage?.text.toString()
            val message = ParseObject.create("Message")
            message.put(USER_ID_KEY, ParseUser.getCurrentUser())
            message.put(BODY_KEY, data)
            message.saveInBackground { e ->
                if (e == null) {
                    Toast.makeText(
                        this@ChatActivity,
                        "Successfully created message on Parse",
                        Toast.LENGTH_SHORT
                    )
                } else {
                    Log.e(TAG, "Failed to save message", e)
                }
            }
        })
        etMessage?.text = null
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    fun login() {
        ParseAnonymousUtils.logIn { user, e ->
            if (e != null) {
                Log.e(TAG, "Anonymous login failed: ", e)
            } else {
                startWithCurrentUser()
            }
        }
    }

    companion object {
        val TAG: String = "ChatActivity"
    }

}