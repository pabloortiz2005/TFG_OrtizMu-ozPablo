package com.pablo.tfg_chatochat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.pablo.tfg_chatochat.model.ChatModel
import java.text.SimpleDateFormat
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

            // Escuchar estado online/offline
            val estadoRef = FirebaseDatabase.getInstance()
                .getReference("Usuarios")
                .child(chat.uidReceptor)
                .child("estado")

            estadoRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val estado = snapshot.getValue(String::class.java) ?: "offline"
                    if (estado == "online") {
                        titleTxt.setTextColor(Color.GREEN)
                    } else {
                        titleTxt.setTextColor(Color.RED)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    titleTxt.setTextColor(Color.GRAY)
                }
            })

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
