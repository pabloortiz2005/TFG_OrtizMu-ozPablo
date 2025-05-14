package com.pablo.tfg_chatochat

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.pablo.tfg_chatochat.model.ChatModel

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var recyclerViewChats: RecyclerView
    private lateinit var chatsAdapter: ChatsAdapter
    private val listaChats = ArrayList<ChatModel>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configurar RecyclerView de chats
        recyclerViewChats = findViewById(R.id.recyclerViewChats)
        recyclerViewChats.layoutManager = LinearLayoutManager(this)
        chatsAdapter = ChatsAdapter(listaChats) { chat ->
            // Al pulsar un chat, abres la pantalla de ChatActivity
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("chatId", chat.chatId)
            intent.putExtra("uidReceptor", chat.uidReceptor)
            startActivity(intent)
        }
        recyclerViewChats.adapter = chatsAdapter


        database = FirebaseDatabase.getInstance().getReference("chats")


        cargarChats()


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val sys = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom)
            insets
        }
    }

    private fun cargarChats() {

        val uidActual = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaChats.clear()
                for (chatSnap in snapshot.children) {
                    val chat = chatSnap.getValue(ChatModel::class.java)
                    // Filtra solo aquellos en los que participe el usuario logueado
                    if (chat != null && chat.participants.contains(uidActual)) {
                        listaChats.add(chat)
                    }
                }
                chatsAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity,
                    "Error al leer chats: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_principal, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_salir -> {
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, Inicio::class.java))
                finish()
                true
            }
            R.id.menu_lista_usuarios -> {
                // Abre la lista de usuarios
                startActivity(Intent(this, ListaUsuariosActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
