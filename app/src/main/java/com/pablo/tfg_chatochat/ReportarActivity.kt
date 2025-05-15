package com.pablo.tfg_chatochat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ReportarActivity : AppCompatActivity() {

    private lateinit var textUsuarioId: TextView
    private lateinit var inputDescripcion: EditText
    private lateinit var btnEnviarReporte: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reportar)

        textUsuarioId = findViewById(R.id.textUsuarioId)
        inputDescripcion = findViewById(R.id.inputDescripcion)
        btnEnviarReporte = findViewById(R.id.btnEnviarReporte)

        // Recibir el UID pasado desde ChatActivity
        val uidUsuarioReportado = intent.getStringExtra("uidReportado") ?: "Desconocido"
        textUsuarioId.text = "Usuario: $uidUsuarioReportado"

        btnEnviarReporte.setOnClickListener {
            val descripcion = inputDescripcion.text.toString().trim()
            if (descripcion.isEmpty()) {
                Toast.makeText(this, "Por favor escribe una descripción del problema", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Aquí puedes enviar el reporte a tu backend o Firebase, etc.
            // Por ahora solo mostramos un mensaje de confirmación

            Toast.makeText(this, "Reporte enviado correctamente", Toast.LENGTH_LONG).show()
            finish() // Cierra la activity y vuelve atrás
        }
    }
}
