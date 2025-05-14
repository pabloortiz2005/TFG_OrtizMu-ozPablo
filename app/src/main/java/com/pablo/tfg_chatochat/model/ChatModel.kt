package com.pablo.tfg_chatochat.model

data class ChatModel(
    val chatId: String = "",
    val uidEmisor: String = "",
    val uidReceptor: String = "",
    val ultimoMensaje: String = "",
    val timestampUltimoMensaje: Long = 0,
    val participants: List<String> = listOf(),
    val titulo: String = ""
)

