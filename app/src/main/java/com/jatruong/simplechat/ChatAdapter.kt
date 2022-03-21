package com.jatruong.simplechat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.math.BigInteger
import java.security.MessageDigest


class ChatAdapter(
    context: Context,
    private val mUserId: String,
    private val mMessages: List<Message>
) :
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder?>() {
    val MESSAGE_OUTGOING = 123
    val MESSAGE_INCOMING = 321
    private val mContext: Context = context

    override fun getItemViewType(position: Int): Int {
        return if (isMe(position)) {
            MESSAGE_OUTGOING
        } else {
            MESSAGE_INCOMING
        }
    }

    private fun isMe(position: Int): Boolean {
        val message: Message = mMessages[position]
        return message.userId != null && message.userId.equals(mUserId)
    }


    override fun getItemCount(): Int {
        return mMessages.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatAdapter.MessageViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(mContext)
        return when (viewType) {
            MESSAGE_INCOMING -> {
                val contactView = inflater.inflate(R.layout.message_incoming, parent, false)
                IncomingMessageViewHolder(contactView)
            }
            MESSAGE_OUTGOING -> {
                val contactView = inflater.inflate(R.layout.message_outgoing, parent, false)
                OutgoingMessageViewHolder(contactView)
            }
            else -> {
                throw IllegalArgumentException("unknown view type")
            }
        }
    }


    override fun onBindViewHolder(holder: ChatAdapter.MessageViewHolder, position: Int) {
        val message: Message = mMessages[position]
        holder.bindMessage(message)
    }

    abstract class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bindMessage(message: Message?)
    }

   inner  class IncomingMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
        val imageOther: ImageView = itemView.findViewById(R.id.ivProfileOther)
        val body: TextView = itemView.findViewById(R.id.tvBody)
        val name: TextView = itemView.findViewById(R.id.tvName)

        override fun bindMessage(message: Message?) {
            Glide.with(mContext).load(getProfileUrl(message?.userId))
                .circleCrop()
                .into(imageOther)
            body.text = message?.body
            name.text = message?.userId
        }

    }

    inner class OutgoingMessageViewHolder(itemView: View) : MessageViewHolder(itemView) {
        val imageMe: ImageView = itemView.findViewById(R.id.ivProfileMe)
        val body: TextView = itemView.findViewById(R.id.tvBody)

        override fun bindMessage(message: Message?) {
            Glide.with(mContext).load(getProfileUrl(message?.userId))
                .circleCrop()
                .into(imageMe)
            body.text = message?.body
        }
    }

    companion object {
        fun getProfileUrl(userId: String?): String? {
            val hex: String? = null
            try {
                val digest: MessageDigest = MessageDigest.getInstance("MD5")
                val hash = digest.digest(userId?.toByteArray())
                val bigInt: BigInteger = BigInteger(hash)
                val hex = bigInt.abs().toString(16)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "https://www.gravatar.com/avatar/$hex?d=identicon"
        }
    }
}