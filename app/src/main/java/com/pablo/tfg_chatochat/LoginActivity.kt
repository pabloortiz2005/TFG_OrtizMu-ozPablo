package com.pablo.tfg_chatochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var LoginCorreo: EditText
    private lateinit var LoginContra: EditText
    private lateinit var LoginBotonIniciarSesion: Button
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        supportActionBar?.title = "Login"
        InicializarV()

        LoginBotonIniciarSesion.setOnClickListener {
            ValidarDatos()

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun InicializarV() {
        LoginCorreo = findViewById(R.id.LoginCorreo)
        LoginContra = findViewById(R.id.LoginContra)
        LoginBotonIniciarSesion = findViewById(R.id.LoginBotonIniciarSesion)
        auth = FirebaseAuth.getInstance()
    }

    private fun ValidarDatos() {
        val email: String = LoginCorreo.text.toString()
        val contra: String = LoginContra.text.toString()

        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce un correo electrónico", Toast.LENGTH_SHORT)
                .show()
        }
        if (contra.isEmpty()) {
            Toast.makeText(this, "Por favor, introduce una contraseña", Toast.LENGTH_SHORT).show()
        } else {
            IniciarSesion(email, contra)
        }
    }

    private fun IniciarSesion(email: String, contra: String) {
        auth.signInWithEmailAndPassword(email, contra)
            .addOnSuccessListener {
                val uid = auth.currentUser?.uid
                if (uid != null) {
                    val dbRef = FirebaseDatabase.getInstance().getReference("Usuarios").child(uid)
                    dbRef.get().addOnSuccessListener { snapshot ->
                        if (snapshot.exists()) {
                            val nombre = snapshot.child("nombre").value.toString()
                            val correo = snapshot.child("correo").value.toString()



                            Toast.makeText(this, "Bienvenido $nombre", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("nombreUsuario", nombre)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(this, "Error al obtener los datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }




}