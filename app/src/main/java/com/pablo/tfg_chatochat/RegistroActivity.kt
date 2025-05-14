package com.pablo.tfg_chatochat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistroActivity : AppCompatActivity() {

    private lateinit var RegistroNombre: EditText
    private lateinit var RegistroCorreo: EditText
    private lateinit var RegistroContraseña: EditText
    private lateinit var RegistroConfirmarContraseña: EditText
    private lateinit var RegistroBotonRegistrar: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registro)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.title = "Registros"
        InicializarV()

        RegistroBotonRegistrar.setOnClickListener {
            ValidarDatos()
        }
    }

    private fun InicializarV() {
        RegistroNombre = findViewById(R.id.RegistroNombre)
        RegistroCorreo = findViewById(R.id.RegistroCorreo)
        RegistroContraseña = findViewById(R.id.RegistroContraseña)
        RegistroConfirmarContraseña = findViewById(R.id.RegistroConfirmarContraseña)
        RegistroBotonRegistrar = findViewById(R.id.RegistroBotonRegistrar)
        auth = FirebaseAuth.getInstance()
    }

    private fun ValidarDatos() {
        val nombre: String = RegistroNombre.text.toString()
        val correo: String = RegistroCorreo.text.toString()
        val contraseña: String = RegistroContraseña.text.toString()
        val confirmarContraseña: String = RegistroConfirmarContraseña.text.toString()

        if (nombre.isEmpty() || correo.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty()) {
            Toast.makeText(this, "Por favor, rellene todos los campos", Toast.LENGTH_SHORT).show()
        } else if (contraseña != confirmarContraseña) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
        } else {
            RegistrarUsuario(nombre, correo, contraseña)
        }
    }

    private fun RegistrarUsuario(nombre: String, correo: String, contraseña: String) {
        auth.createUserWithEmailAndPassword(correo, contraseña)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    reference = FirebaseDatabase.getInstance().reference.child("Usuarios").child(uid)

                    val hashmap = HashMap<String, Any>()
                    val h_nombre = RegistroNombre.text.toString()
                    val h_correo = RegistroCorreo.text.toString()

                    hashmap["uid"] = uid
                    hashmap["nombre"] = h_nombre
                    hashmap["correo"] = h_correo
                    hashmap["imagen"] = ""
                    hashmap["buscar"] = h_nombre.lowercase()
                    Log.d("FIREBASE_REGISTRO", "Guardando datos en: Usuarios/$uid")
                    reference.updateChildren(hashmap).addOnCompleteListener { task2 ->
                        if (task2.isSuccessful) {



                            Toast.makeText(
                                this,
                                "Usuario registrado correctamente",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@RegistroActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()


                        }
                    }.addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Error al registrar usuario: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar usuario: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

}