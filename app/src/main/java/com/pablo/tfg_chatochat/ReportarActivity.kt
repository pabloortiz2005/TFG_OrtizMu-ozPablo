package com.pablo.tfg_chatochat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.pablo.tfg_chatochat.DataClass.Reporte


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

        val uidUsuarioReportado = intent.getStringExtra("uidReportado") ?: "Desconocido"
        textUsuarioId.text = "Usuario: $uidUsuarioReportado"

        btnEnviarReporte.setOnClickListener {
            val descripcion = inputDescripcion.text.toString().trim()
            if (descripcion.isEmpty()) {
                Toast.makeText(this, "Por favor escribe una descripción del problema", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val uidReportante = FirebaseAuth.getInstance().currentUser?.uid ?: "Anónimo"

            val reporte = Reporte(
                uidReportado = uidUsuarioReportado,
                uidReportante = uidReportante,
                descripcion = descripcion
            )

            val referenciaReportes = FirebaseDatabase.getInstance().getReference("reportes")
            val nuevoReporteRef = referenciaReportes.push()

            nuevoReporteRef.setValue(reporte)
                .addOnSuccessListener {
                    Toast.makeText(this, "Reporte enviado correctamente", Toast.LENGTH_LONG).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al enviar el reporte", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
