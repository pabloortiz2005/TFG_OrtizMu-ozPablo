package com.pablo.tfg_chatochat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import com.pablo.tfg_chatochat.model.ChatModel
import java.util.*

class ChatsAdapter(
    private val chats: List<ChatModel>,
    private val onChatSelected: (ChatModel) -> Unit
) : RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTxt: TextView = itemView.findViewById(R.id.chatTitle)
        private val lastMsgTxt: TextView = itemView.findViewById(R.id.chatLastMessage)
        private val timeTxt: TextView = itemView.findViewById(R.id.chatTimestamp)

        fun bind(chat: ChatModel) {
            titleTxt.text = chat.titulo
            lastMsgTxt.text = chat.ultimoMensaje
            timeTxt.text = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(chat.timestampUltimoMensaje))

            itemView.setOnClickListener { onChatSelected(chat) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chats[position])
    }

    override fun getItemCount(): Int = chats.size
}
