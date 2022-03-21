package com.jatruong.simplechat

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parse.ParseAnonymousUtils
import com.parse.ParseQuery
import com.parse.ParseUser
import java.util.concurrent.TimeUnit

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
    var mMessages: MutableList<Message>? = null
    var mFirstLoad: Boolean = true
    var rvChat: RecyclerView? = null
    lateinit var mAdapter: ChatAdapter

    fun setupMessagePosting() {
        etMessage = findViewById<EditText>(R.id.etMessage)
        ibSend = findViewById<ImageButton>(R.id.ibSend)
        val rvChat = findViewById<RecyclerView>(R.id.rvChat)
        mMessages = mutableListOf()
        val userId = ParseUser.getCurrentUser().objectId
        mAdapter = ChatAdapter(this, userId, mMessages!!)
        rvChat.adapter = mAdapter
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        rvChat.layoutManager = linearLayoutManager

        ibSend?.setOnClickListener(View.OnClickListener {
            val data: String = etMessage?.text.toString()
//            val message = ParseObject.create("Message")
//            message.put(USER_ID_KEY, ParseUser.getCurrentUser().objectId)
//            message.put(BODY_KEY, data)
            val message: Message = Message()
            message.userId = ParseUser.getCurrentUser().objectId
            message.body = data

            message.saveInBackground { e ->
                if (e == null) {
                    Toast.makeText(
                        this,
                        "Successfully created message on Parse",
                        Toast.LENGTH_SHORT
                    )
                    Log.i(TAG, "Message Saved")
                } else {
                    Log.e(TAG, "Failed to save message", e)
                }
            }
        })
        etMessage?.text = null
        refreshMessages()
        myHandler = Handler()
        mRefreshMessagesRunnable = object : Runnable {
            override fun run() {
                refreshMessages()
                myHandler.postDelayed(this, POLL_INTERVAL)
            }
        }
    }

    lateinit var myHandler: Handler
    lateinit var mRefreshMessagesRunnable: Runnable
    val POLL_INTERVAL = TimeUnit.SECONDS.toMillis(3)


    override fun onResume() {
        super.onResume()
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL)
    }

    override fun onPause() {
        myHandler.removeCallbacksAndMessages(null)
        super.onPause()
    }

    fun refreshMessages() {
        val query = ParseQuery.getQuery(Message::class.java)
        query.limit = MAX_CHAT_MESSAGES_TO_SHOW

        query.orderByDescending("createdAt")

        query.findInBackground { messages, e ->
            if (e == null) {
                mMessages?.clear()
                mMessages?.addAll(messages)
                mAdapter.notifyDataSetChanged()
                if (mFirstLoad) {
                    rvChat?.scrollToPosition(0)
                    mFirstLoad = false
                }
            } else {
                Log.e("message", "Error Loading Messages $e")
            }
        }
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
        val MAX_CHAT_MESSAGES_TO_SHOW = 50
    }

}