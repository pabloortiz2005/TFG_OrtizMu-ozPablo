package com.pablo.tfg_chatochat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pablo.tfg_chatochat.DataClass.Mensaje
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class ChatActivity : AppCompatActivity() {

    private lateinit var inputMensaje: EditText
    private lateinit var botonEnviar: FrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MensajesAdapter
    private val listaMensajes = ArrayList<Mensaje>()

    private lateinit var referenciaChat: DatabaseReference
    private lateinit var uidEmisor: String
    private lateinit var uidReceptor: String
    private lateinit var chatId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageBack = findViewById<AppCompatImageView>(R.id.imageBack)
        imageBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        inputMensaje = findViewById(R.id.inputMessage)
        botonEnviar = findViewById(R.id.layoutSend)
        recyclerView = findViewById(R.id.chatRecyclerView)

        uidEmisor = FirebaseAuth.getInstance().currentUser?.uid ?: return
        uidReceptor = intent.getStringExtra("uidReceptor") ?: return

        chatId = if (uidEmisor < uidReceptor)
            "${uidEmisor}_$uidReceptor"
        else
            "${uidReceptor}_$uidEmisor"

        val chatRef = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)

        // Crear nodo participants si no existe y también crear titulos con nombres
        chatRef.child("participants").get().addOnSuccessListener { snapshot ->
            if (!snapshot.exists()) {
                val participantes = listOf(uidEmisor, uidReceptor)
                chatRef.child("participants").setValue(participantes)
                    .addOnSuccessListener {
                        Log.d("ChatActivity", "Participantes guardados correctamente")
                        crearTitulos(chatRef, uidEmisor, uidReceptor)
                    }
                    .addOnFailureListener {
                        Log.e("ChatActivity", "Error guardando participantes")
                    }
            } else {
                // Asegurarse que titulos existen también al abrir chat existente
                crearTitulos(chatRef, uidEmisor, uidReceptor)
            }
        }

        referenciaChat = chatRef.child("mensajes")

        adapter = MensajesAdapter(listaMensajes, uidEmisor)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        botonEnviar.setOnClickListener {
            val texto = inputMensaje.text.toString().trim()
            if (texto.isNotEmpty()) {
                val mensaje = Mensaje(
                    contenido = texto,
                    emisorId = uidEmisor,
                    receptorId = uidReceptor,
                    timestamp = System.currentTimeMillis()
                )
                referenciaChat.push().setValue(mensaje)

                // Enviar notificación al receptor
                val dbUsuarios = FirebaseDatabase.getInstance().getReference("Usuarios")
                dbUsuarios.child(uidReceptor).child("fcmToken").get().addOnSuccessListener { snapshot ->
                    val tokenReceptor = snapshot.getValue(String::class.java)
                    if (tokenReceptor != null) {
                        enviarNotificacion(tokenReceptor, "Nuevo mensaje", texto)
                    } else {
                        Log.e("Notificacion", "Token receptor no encontrado")
                    }
                }

                inputMensaje.setText("")
            }
        }

        escucharMensajes()
    }

    private fun crearTitulos(chatRef: DatabaseReference, uidEmisor: String, uidReceptor: String) {
        val usuariosRef = FirebaseDatabase.getInstance().getReference("Usuarios")

        usuariosRef.child(uidEmisor).child("nombre").get().addOnSuccessListener { snapEmisor ->
            val nombreEmisor = snapEmisor.getValue(String::class.java) ?: "Desconocido"

            usuariosRef.child(uidReceptor).child("nombre").get().addOnSuccessListener { snapReceptor ->
                val nombreReceptor = snapReceptor.getValue(String::class.java) ?: "Desconocido"

                val titulosMap = mapOf(
                    uidEmisor to nombreReceptor,   // Para emisor, título = nombre del receptor
                    uidReceptor to nombreEmisor    // Para receptor, título = nombre del emisor
                )

                chatRef.child("titulos").setValue(titulosMap)
                    .addOnSuccessListener {
                        Log.d("ChatActivity", "Titulos creados correctamente")
                    }
                    .addOnFailureListener {
                        Log.e("ChatActivity", "Error creando titulos")
                    }
            }
        }
    }

    private fun escucharMensajes() {
        referenciaChat.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val mensaje = snapshot.getValue(Mensaje::class.java)
                mensaje?.let {
                    listaMensajes.add(it)
                    adapter.notifyItemInserted(listaMensajes.size - 1)
                    recyclerView.scrollToPosition(listaMensajes.size - 1)
                    recyclerView.visibility = View.VISIBLE
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error al escuchar mensajes: ${error.message}")
            }
        })
    }

    private fun enviarNotificacion(tokenDestino: String, titulo: String, mensaje: String) {
        val serverKey = "BJrb58PtaAmsLXMCdsUAaTT-cLzod038ay93TA8OSrMjavpuZphwAzpRInKfr7c1gcJ7QUxFtqwhyFWorAiwYCg"
        val url = "https://fcm.googleapis.com/fcm/send"

        val json = JSONObject()
        val data = JSONObject()
        data.put("title", titulo)
        data.put("body", mensaje)

        json.put("to", tokenDestino)
        json.put("notification", data)
        json.put("priority", "high")

        val client = OkHttpClient()
        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val body = json.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "key=$serverKey")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: java.io.IOException) {
                Log.e("Notificacion", "Error enviando notificación: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                Log.d("Notificacion", "Notificación enviada. Código: ${response.code}")
            }
        })
    }
}
