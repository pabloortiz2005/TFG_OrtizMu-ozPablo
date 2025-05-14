package com.pablo.tfg_chatochat

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ListaUsuariosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var usuariosAdapter: UsuariosAdapter
    private val listaUsuarios = ArrayList<Usuario>()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuarios)

        recyclerView = findViewById(R.id.recyclerViewUsuarios)
        searchView   = findViewById(R.id.searchView)

        // Inicializa RecyclerView y Adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        usuariosAdapter = UsuariosAdapter(listaUsuarios) { usuario ->
            // Al pulsar un usuario, abre Chat
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("uidReceptor", usuario.uid)
            }
            startActivity(intent)
        }
        recyclerView.adapter = usuariosAdapter

        database = FirebaseDatabase.getInstance().getReference("Usuarios")


        cargarUsuarios()


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank()) {
                    cargarUsuarios()
                } else {
                    buscarUsuarios(newText.lowercase())
                }
                return true
            }
        })
    }

    private fun cargarUsuarios() {
        val uidActual = FirebaseAuth.getInstance().currentUser?.uid ?: return
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaUsuarios.clear()
                for (snap in snapshot.children) {
                    val usuario = snap.getValue(Usuario::class.java)
                    if (usuario != null && usuario.uid != uidActual) {
                        listaUsuarios.add(usuario)
                    }
                }
                usuariosAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ListaUsuariosActivity,
                    "Error al cargar usuarios: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun buscarUsuarios(query: String) {
        database.orderByChild("buscar")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaUsuarios.clear()
                    for (snap in snapshot.children) {
                        val usuario = snap.getValue(Usuario::class.java)
                        if (usuario != null) {
                            listaUsuarios.add(usuario)
                        }
                    }
                    usuariosAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ListaUsuariosActivity,
                        "Error en búsqueda: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
