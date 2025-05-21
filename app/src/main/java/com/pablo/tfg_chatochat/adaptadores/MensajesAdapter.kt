package com.pablo.tfg_chatochat.adaptadores

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pablo.tfg_chatochat.DataClass.Mensaje
import com.pablo.tfg_chatochat.R
import java.text.SimpleDateFormat
import java.util.*



//Aqui trabajamos con los mensajes de los chats

class MensajesAdapter(


    private val mensajes: MutableList<Mensaje>,
    private val uidActual: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TIPO_ENVIADO = 1
    private val TIPO_RECIBIDO = 2

    override fun getItemViewType(position: Int): Int {
        return if (mensajes[position].emisorId == uidActual) TIPO_ENVIADO else TIPO_RECIBIDO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TIPO_ENVIADO) {
            val vista = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_container_sent_message, parent, false)
            EnviadoViewHolder(vista)
        } else {
            val vista = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_container_received_message, parent, false)
            RecibidoViewHolder(vista)
        }
    }

    override fun getItemCount(): Int = mensajes.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensaje = mensajes[position]
        val hora = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(mensaje.timestamp))

        when (holder) {
            is EnviadoViewHolder -> {
                holder.textoMensaje.text = mensaje.contenido
                holder.textoHora.text = hora
            }
            is RecibidoViewHolder -> {
                holder.textoMensaje.text = mensaje.contenido
                holder.textoHora.text = hora
            }
        }
    }

    fun agregarMensaje(mensaje: Mensaje) {
        mensajes.add(mensaje)
        notifyItemInserted(mensajes.size - 1)
    }

    class EnviadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoMensaje: TextView = itemView.findViewById(R.id.textMessage)
        val textoHora: TextView = itemView.findViewById(R.id.textDateTime)
    }

    class RecibidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textoMensaje: TextView = itemView.findViewById(R.id.textMessage)
        val textoHora: TextView = itemView.findViewById(R.id.textDateTime)
    }
}
