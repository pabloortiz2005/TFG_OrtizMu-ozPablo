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
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer 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")
                    .build()
                chain.proceed(newRequest)
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(OpenAiService::class.java)
        val mensajes = listOf(Message(role = "user", content = pregunta))

        val request = OpenAiRequest(
            model = "gpt-3.5-turbo",
            messages = mensajes
        )

        service.enviarMensaje(request).enqueue(object : Callback<OpenAiResponse> {
            override fun onResponse(
                call: Call<OpenAiResponse>,
                response: Response<OpenAiResponse>
            ) {
                if (response.isSuccessful) {
                    val respuesta = response.body()?.choices?.firstOrNull()?.message?.content
                        ?: "No se recibió respuesta."
                    val mensajeIa = Mensaje(
                        contenido = respuesta,
                        emisorId = "ia",
                        receptorId = "user",
                        timestamp = System.currentTimeMillis()
                    )
                    listaMensajes.add(mensajeIa)
                    adapter.notifyItemInserted(listaMensajes.size - 1)
                    recyclerView.scrollToPosition(listaMensajes.size - 1)
                } else {
                    val errorMsg = "Error: ${response.code()} ${response.message()}"
                    val mensajeError = Mensaje(
                        contenido = errorMsg,
                        emisorId = "ia",
                        receptorId = "user",
                        timestamp = System.currentTimeMillis()
                    )
                    listaMensajes.add(mensajeError)
                    adapter.notifyItemInserted(listaMensajes.size - 1)
                    recyclerView.scrollToPosition(listaMensajes.size - 1)
                }
            }

            override fun onFailure(call: Call<OpenAiResponse>, t: Throwable) {
                val mensajeError = Mensaje(
                    contenido = "Error: ${t.localizedMessage}",
                    emisorId = "ia",
                    receptorId = "user",
                    timestamp = System.currentTimeMillis()
                )
                listaMensajes.add(mensajeError)
                adapter.notifyItemInserted(listaMensajes.size - 1)
                recyclerView.scrollToPosition(listaMensajes.size - 1)
            }
        })
    }
}
