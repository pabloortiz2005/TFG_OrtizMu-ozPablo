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
import com.pablo.tfg_chatochat.model.ChatModel

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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
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

        // Referencia para los mensajes
        referenciaChat = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)
            .child("mensajes")

        // Verificar si el chat ya existe
        val referenciaChatMeta = FirebaseDatabase.getInstance()
            .getReference("chats")
            .child(chatId)

        // Obtener el nombre del receptor
        val dbUsuarios = FirebaseDatabase.getInstance().getReference("Usuarios")

        // Log para verificar el UID del receptor
        Log.d("Firebase", "Intentando buscar usuario con UID: $uidReceptor")


        dbUsuarios.child(uidReceptor).get().addOnSuccessListener { snapshot ->

            Log.d("Firebase", "Datos completos del usuario: ${snapshot.value}")

            // Verificamos si existe el nodo del usuario receptor
            if (snapshot.exists()) {

                var nombreReceptor = "prueba"

                // Actualiza el título en la UI si es necesario
                supportActionBar?.title = nombreReceptor

                // Verifica si el chat existe
                referenciaChatMeta.get().addOnSuccessListener { chatSnapshot ->
                    if (!chatSnapshot.exists()) {
                        // Si el chat no existe, crea el nuevo chat con el nombre del receptor como título
                        val chat = ChatModel(
                            chatId = chatId,
                            uidEmisor = uidEmisor,
                            uidReceptor = uidReceptor,
                            ultimoMensaje = "",
                            timestampUltimoMensaje = System.currentTimeMillis(),
                            participants = listOf(uidEmisor, uidReceptor),
                            titulo = nombreReceptor  // Asignar el nombre del receptor como título del chat
                        )


                        referenciaChatMeta.setValue(chat).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Log.d("Firebase", "Chat creado exitosamente con título: $nombreReceptor")
                            } else {
                                Log.d("Firebase", "Error al crear el chat: ${it.exception?.message}")
                            }
                        }
                    }
                }
            } else {
                Log.e("Firebase", "No se encontró al usuario con UID: $uidReceptor")

                val nombreReceptor = "error"
                supportActionBar?.title = nombreReceptor


                referenciaChatMeta.get().addOnSuccessListener { chatSnapshot ->
                    if (!chatSnapshot.exists()) {
                        val chat = ChatModel(
                            chatId = chatId,
                            uidEmisor = uidEmisor,
                            uidReceptor = uidReceptor,
                            ultimoMensaje = "",
                            timestampUltimoMensaje = System.currentTimeMillis(),
                            participants = listOf(uidEmisor, uidReceptor),
                            titulo = nombreReceptor
                        )
                        referenciaChatMeta.setValue(chat)
                    }
                }
            }
        }.addOnFailureListener { exception ->
            Log.e("Firebase", "Error al obtener datos del usuario: ${exception.message}")

            val nombreReceptor = "mal"
            supportActionBar?.title = nombreReceptor

            referenciaChatMeta.get().addOnSuccessListener { chatSnapshot ->
                if (!chatSnapshot.exists()) {
                    val chat = ChatModel(
                        chatId = chatId,
                        uidEmisor = uidEmisor,
                        uidReceptor = uidReceptor,
                        ultimoMensaje = "",
                        timestampUltimoMensaje = System.currentTimeMillis(),
                        participants = listOf(uidEmisor, uidReceptor),
                        titulo = nombreReceptor
                    )
                    referenciaChatMeta.setValue(chat)
                }
            }
        }

        // Configuración del RecyclerView
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
                // Guardar el mensaje en Firebase
                referenciaChat.push().setValue(mensaje)
                inputMensaje.setText("")
            }
        }

        escucharMensajes()
    }

    private fun escucharMensajes() {
        referenciaChat.addChildEventListener(object : ChildEventListener {
            // Este método se llama cada vez que un nuevo mensaje es agregado a la base de datos
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Firebase", "onChildAdded llamado") // Verifica que la función se esté ejecutando correctamente

                val mensaje = snapshot.getValue(Mensaje::class.java)
                Log.d("Firebase", "Mensaje recibido: $mensaje") // Verifica el contenido del mensaje recibido

                mensaje?.let {
                    // Si el mensaje no es nulo, lo agregamos a la lista de mensajes
                    listaMensajes.add(it)

                    // Notificamos al adapter que hemos agregado un nuevo mensaje
                    adapter.notifyItemInserted(listaMensajes.size - 1)

                    // Desplazamos el RecyclerView hasta el último mensaje para mantener la vista actualizada
                    recyclerView.scrollToPosition(listaMensajes.size - 1)

                    // Aseguramos que el RecyclerView se haga visible una vez que haya mensajes
                    recyclerView.visibility = View.VISIBLE
                }
            }


            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Firebase", "onChildChanged llamado")

            }

            // Este método se llama si un mensaje es eliminado
            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("Firebase", "onChildRemoved llamado")

            }

            // Este método se llama si un mensaje se mueve dentro de la base de datos (cambio de orden)
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("Firebase", "onChildMoved llamado")

            }

            // Este método se llama si la lectura de datos es cancelada
            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase", "onCancelled: ${error.message}")

            }
        })
    }


}