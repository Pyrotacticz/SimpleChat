package com.jatruong.simplechat

import com.parse.ParseClassName
import com.parse.ParseObject


@ParseClassName("Message")
class Message : ParseObject() {
    var userId: String?
        get() = getString(USER_ID_KEY)
        set(userId) {
            put(USER_ID_KEY, userId!!)
        }
    var body: String?
        get() = getString(BODY_KEY)
        set(body) {
            put(BODY_KEY, body!!)
        }

    companion object {
        const val USER_ID_KEY = "userId"
        const val BODY_KEY = "body"
    }
}