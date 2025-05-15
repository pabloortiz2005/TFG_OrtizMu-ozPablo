package com.pablo.tfg_chatochat

import Message
import OpenAiRequest
import OpenAiResponse
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class IaChatActivity : AppCompatActivity() {

    private lateinit var inputMensaje: EditText
    private lateinit var botonEnviar: FrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MensajesAdapter
    private val listaMensajes = ArrayList<Mensaje>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ia_chat)

        val btnBack = findViewById<AppCompatImageView>(R.id.imageBack)
        btnBack.setOnClickListener { finish() }

        inputMensaje = findViewById(R.id.inputMessage)
        botonEnviar = findViewById(R.id.layoutSend)
        recyclerView = findViewById(R.id.chatRecyclerView)

        adapter = MensajesAdapter(listaMensajes, "user")
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        botonEnviar.setOnClickListener {
            val texto = inputMensaje.text.toString().trim()
            if (texto.isNotEmpty()) {
                val mensajeUsuario = Mensaje(
                    contenido = texto,
                    emisorId = "user",
                    receptorId = "ia",
                    timestamp = System.currentTimeMillis()
                )
                listaMensajes.add(mensajeUsuario)
                adapter.notifyItemInserted(listaMensajes.size - 1)
                recyclerView.scrollToPosition(listaMensajes.size - 1)

                inputMensaje.text.clear()

                simularRespuestaIA(texto)
            }
        }
    }

    private fun simularRespuestaIA(pregunta: String) {

    }
}
