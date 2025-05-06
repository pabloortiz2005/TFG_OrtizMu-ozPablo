package com.pablo.tfg_chatochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth


class Inicio : AppCompatActivity() {

    private lateinit var BotonIrRegistro: Button
    private lateinit var BotonIrLogeo: Button

    var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_inicio)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.inicio)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        BotonIrRegistro = findViewById(R.id.BotonIrRegistro)
        BotonIrLogeo = findViewById(R.id.BotonIrLogeo)

        BotonIrRegistro.setOnClickListener{
            val intent = Intent(this@Inicio, RegistroActivity::class.java)
            Toast.makeText(applicationContext, "Registros", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

        BotonIrLogeo.setOnClickListener{
            val intent = Intent(this@Inicio, LoginActivity::class.java)
            Toast.makeText(applicationContext, "Login", Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }

    }
    private fun ComprobarSesion(){
        firebaseUser= FirebaseAuth.getInstance().currentUser
        if (firebaseUser!=null){
            val intent = Intent(this@Inicio, MainActivity::class.java)
            Toast.makeText(applicationContext, "Sesión iniciada", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        ComprobarSesion()
        super.onStart()
    }
}