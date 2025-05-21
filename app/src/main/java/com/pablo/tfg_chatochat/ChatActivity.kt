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
import com.pablo.tfg_chatochat.DataClass.ChatModel
import com.pablo.tfg_chatochat.DataClass.Mensaje

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

        val imageInfo = findViewById<AppCompatImageView>(R.id.imageInfo)
        imageInfo.setOnClickListener {
            val intent = Intent(this, ReportarActivity::class.java)
            intent.putExtra("uidReportado", uidReceptor)
            startActivity(intent)
        }

        chatId = if (uidEmisor < uidReceptor)
            "${uidEmisor}_$uidReceptor"
        else
            "${uidReceptor}_$uidEmisor"

        referenciaChat = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)
            .child("mensajes")

        val referenciaChatMeta = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)

        val dbUsuarios = FirebaseDatabase.getInstance().getReference("Usuarios")
        val emisorTask = dbUsuarios.child(uidEmisor).get()
        val receptorTask = dbUsuarios.child(uidReceptor).get()

        emisorTask.addOnSuccessListener { emisorSnapshot ->
            receptorTask.addOnSuccessListener { receptorSnapshot ->
                val nombreEmisor = emisorSnapshot.child("nombre").getValue(String::class.java) ?: "Usuario"
                val nombreReceptor = receptorSnapshot.child("nombre").getValue(String::class.java) ?: "Usuario"

                // Mostrar el título correcto para el emisor
                supportActionBar?.title = nombreReceptor

                referenciaChatMeta.get().addOnSuccessListener { chatSnapshot ->
                    if (!chatSnapshot.exists()) {
                        val titulos = mapOf(
                            uidEmisor to nombreReceptor,
                            uidReceptor to nombreEmisor
                        )

                        val chat = ChatModel(
                            chatId = chatId,
                            uidEmisor = uidEmisor,
                            uidReceptor = uidReceptor,
                            ultimoMensaje = "",
                            timestampUltimoMensaje = System.currentTimeMillis(),
                            participants = listOf(uidEmisor, uidReceptor),
                            titulos = titulos
                        )

                        referenciaChatMeta.setValue(chat).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("Firebase", "Chat creado con títulos personalizados.")
                            } else {
                                Log.e("Firebase", "Error al crear el chat: ${it.exception?.message}")
                            }
                        }
                    } else {
                        // Si el chat ya existe, actualizar el título de la barra superior con el que corresponde
                        val chat = chatSnapshot.getValue(ChatModel::class.java)
                        val titulo = chat?.titulos?.get(uidEmisor) ?: "Chat"
                        supportActionBar?.title = titulo
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Error al obtener datos del usuario: ${exception.message}")
            supportActionBar?.title = "Chat"
        }

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
                inputMensaje.setText("")
            }
        }

        escucharMensajes()
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
}
