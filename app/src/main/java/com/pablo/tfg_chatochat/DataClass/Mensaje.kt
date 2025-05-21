package com.pablo.tfg_chatochat.DataClass

data class Mensaje(
    val contenido: String = "",
    val emisorId: String = "",
    val receptorId: String = "",
    val timestamp: Long = 0
)
