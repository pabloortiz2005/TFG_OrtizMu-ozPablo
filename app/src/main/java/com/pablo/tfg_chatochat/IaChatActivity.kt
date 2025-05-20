package com.pablo.tfg_chatochat

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
//
class IaChatActivity : AppCompatActivity() {

    private lateinit var inputMensaje: EditText
    private lateinit var botonEnviar: FrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MensajesAdapter
    private val listaMensajes: MutableList<Mensaje> = ArrayList()

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
                adapter.agregarMensaje(mensajeUsuario)
                recyclerView.scrollToPosition(listaMensajes.size - 1)

                inputMensaje.text.clear()

                enviarMensajeAOpenAI(texto)
            }
        }
    }

    private fun enviarMensajeAOpenAI(pregunta: String) {
        val apiKey = "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"

        // Implementación completamente explícita del interceptor
        val interceptor = object : okhttp3.Interceptor {
            override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .build()
                return chain.proceed(newRequest)
            }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
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
                    adapter.agregarMensaje(mensajeIa)
                    recyclerView.scrollToPosition(listaMensajes.size - 1)
                } else {
                    // Mensajes de error descriptivos
                    val mensajeError = when (response.code()) {
                        401 -> "Problema de autenticación. Tu clave API de OpenAI no es válida o ha expirado."
                        429 -> "Has excedido el límite de peticiones. Espera un momento antes de enviar más mensajes."
                        500, 502, 503, 504 -> "Los servidores de OpenAI están experimentando problemas. Inténtalo más tarde."
                        else -> "Error de conexión con OpenAI (${response.code()}). Por favor, inténtalo de nuevo."
                    }

                    val mensajeIa = Mensaje(
                        contenido = mensajeError,
                        emisorId = "ia",
                        receptorId = "user",
                        timestamp = System.currentTimeMillis()
                    )
                    adapter.agregarMensaje(mensajeIa)
                    recyclerView.scrollToPosition(listaMensajes.size - 1)
                }
            }

            override fun onFailure(call: Call<OpenAiResponse>, t: Throwable) {
                val mensajeError = Mensaje(
                    contenido = "Error de conexión: ${t.localizedMessage}",
                    emisorId = "ia",
                    receptorId = "user",
                    timestamp = System.currentTimeMillis()
                )
                adapter.agregarMensaje(mensajeError)
                recyclerView.scrollToPosition(listaMensajes.size - 1)
            }
        })
    }
}