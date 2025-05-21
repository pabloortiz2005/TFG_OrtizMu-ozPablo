package com.pablo.tfg_chatochat.DataClass

data class Reporte(
    val uidReportado: String = "",
    val uidReportante: String = "",
    val descripcion: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
