package com.pablo.tfg_chatochat

import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Chat : AppCompatActivity() {

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

        inputMensaje = findViewById(R.id.inputMessage)
        botonEnviar = findViewById(R.id.layoutSend)
        recyclerView = findViewById(R.id.chatRecyclerView)

        uidEmisor = FirebaseAuth.getInstance().currentUser?.uid ?: return
        uidReceptor = intent.getStringExtra("uidReceptor") ?: return

        chatId = if (uidEmisor < uidReceptor)
            "${uidEmisor}_$uidReceptor"
        else
            "${uidReceptor}_$uidEmisor"

        referenciaChat = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)
            .child("mensajes")

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
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
