package com.pablo.tfg_chatochat

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pablo.tfg_chatochat.DataClass.ChatModel
import java.text.SimpleDateFormat
import java.util.*

class ChatsAdapter(
    private val chats: List<ChatModel>,
    private val onChatSelected: (ChatModel) -> Unit,
    private val onChatLongClick: (ChatModel) -> Unit
) : RecyclerView.Adapter<ChatsAdapter.ChatViewHolder>() {

    private val uidActual = FirebaseAuth.getInstance().currentUser?.uid

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTxt: TextView = itemView.findViewById(R.id.chatTitle)
        private val lastMsgTxt: TextView = itemView.findViewById(R.id.chatLastMessage)
        private val timeTxt: TextView = itemView.findViewById(R.id.chatTimestamp)

        fun bind(chat: ChatModel) {
            // Obtener título personalizado desde el mapa de títulos
            val titulo = chat.titulos[uidActual] ?: "Chat"
            titleTxt.text = titulo

            lastMsgTxt.text = chat.ultimoMensaje
            timeTxt.text = SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(Date(chat.timestampUltimoMensaje))

            itemView.setOnClickListener { onChatSelected(chat) }
            itemView.setOnLongClickListener {
                onChatLongClick(chat)
                true
            }

            // Estado online/offline basado en el "otro" participante
            val uidOtro = if (chat.uidEmisor == uidActual) chat.uidReceptor else chat.uidEmisor
            val estadoRef = FirebaseDatabase.getInstance()
                .getReference("Usuarios")
                .child(uidOtro)
                .child("estado")

            estadoRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val estado = snapshot.getValue(String::class.java) ?: "offline"
                    titleTxt.setTextColor(
                        when (estado) {
                            "online" -> Color.GREEN
                            else -> Color.RED
                        }
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                    titleTxt.setTextColor(Color.GRAY)
                }
            })
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
