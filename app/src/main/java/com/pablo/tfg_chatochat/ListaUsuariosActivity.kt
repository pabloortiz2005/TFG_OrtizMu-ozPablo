package com.pablo.tfg_chatochat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaUsuariosActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var usuariosAdapter: UsuariosAdapter
    private lateinit var listaUsuarios: ArrayList<Usuario>
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuarios)

        recyclerView = findViewById(R.id.recyclerViewUsuarios)
        searchView = findViewById(R.id.searchView)
        listaUsuarios = ArrayList()

        // Referencia a Firebase
        database = FirebaseDatabase.getInstance().getReference("users")

        // Cargar los usuarios de Firebase
        cargarUsuarios()

        // Configurar el adaptador
        usuariosAdapter = UsuariosAdapter(listaUsuarios) { usuario ->
            // Aquí gestionas el click del usuario y lanzas el chat
            val intent = Intent(this, Chat::class.java)
            intent.putExtra("uidReceptor", usuario.uid)
            startActivity(intent)
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = usuariosAdapter

        // Configurar búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    cargarUsuarios()  // Cargar todos los usuarios si no hay texto en la búsqueda
                } else {
                    buscarUsuarios(newText)  // Filtrar los usuarios por la búsqueda
                }
                return true
            }
        })
    }

    private fun cargarUsuarios() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listaUsuarios.clear()
                for (usuarioSnapshot in snapshot.children) {
                    val usuario = usuarioSnapshot.getValue(Usuario::class.java)
                    usuario?.let { listaUsuarios.add(it) }
                }
                usuariosAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun buscarUsuarios(query: String) {
        database.orderByChild("username")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaUsuarios.clear()
                    for (usuarioSnapshot in snapshot.children) {
                        val usuario = usuarioSnapshot.getValue(Usuario::class.java)
                        usuario?.let { listaUsuarios.add(it) }
                    }
                    usuariosAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
